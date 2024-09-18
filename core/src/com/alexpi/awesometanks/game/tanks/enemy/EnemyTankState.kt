package com.alexpi.awesometanks.game.tanks.enemy

import com.alexpi.awesometanks.game.blocks.Block
import com.alexpi.awesometanks.game.map.Cell
import com.alexpi.awesometanks.game.tanks.player.PlayerTank
import com.alexpi.awesometanks.game.utils.getNormalizedAbsoluteDifference
import com.alexpi.awesometanks.game.utils.normalizeAngle
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.atan2

sealed class EnemyTankState: State<EnemyTank>{

    companion object{
        const val SHOOTING_RADIUS_2 = 5*5
        const val FORGET_TARGET_LIMIT_MILLIS = 5000
        const val PATHFINDING_EXECUTION_INTERVAL = 500
        const val AWAIT_TIME_MILLIS = 2000
        const val FOV_ANGLE = 150 * MathUtils.degreesToRadians //120 DEGREES OF VISIBILITY
        const val DAMAGE_RECEIVED_MESSAGE = 0
        const val CELL_OFFSET = .05f
    }

    protected fun isPlayerInFOV(world: World, entity: EnemyTank, playerTank: PlayerTank): Boolean {
        //If the player is not alive, return false
        if(!playerTank.healthComponent.isAlive){
            return false
        }

        val deltaX = playerTank.bodyComponent.body.position.x - entity.bodyComponent.body.position.x
        val deltaY = playerTank.bodyComponent.body.position.y - entity.bodyComponent.body.position.y

        //If the player is not within range, return false
        if(deltaX * deltaX  + deltaY * deltaY > SHOOTING_RADIUS_2){
            return false
        }

        val angleOfTarget = MathUtils.atan2(deltaY,deltaX)
        val angleDifference = getNormalizedAbsoluteDifference(entity.currentWeapon.currentRotationAngle.normalizeAngle(), angleOfTarget.normalizeAngle())

        if(angleDifference > FOV_ANGLE/2){
            return false
        }

        //If the player is within the visibility angle, check if there's no obstacles in between
        val isTargetVisible = checkTargetVisibility(world, entity.bodyComponent.body.position, playerTank.bodyComponent.body.position)
        return isTargetVisible
    }

    protected fun checkTargetVisibility(world: World, position: Vector2, targetPosition: Vector2): Boolean {
        var isVisible = true
        world.rayCast({ fixture, _, _, _ ->
            if(fixture.userData is Block){
                isVisible = false
                0f
            }
            1f
        } , position, targetPosition)
        return isVisible
    }

    override fun enter(entity: EnemyTank) {

    }

    override fun exit(entity: EnemyTank) { }

    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean {
        if(telegram.message == DAMAGE_RECEIVED_MESSAGE){
            entity.stateMachine.changeState(ChaseState())
            return true
        }
        return false
    }
}

object FrozenState: EnemyTankState(){
    override fun update(entity: EnemyTank) {
        if(!entity.healthComponent.isFrozen){
            entity.stateMachine.changeState(entity.stateMachine.previousState)
        }
    }

    override fun exit(entity: EnemyTank) {
        super.exit(entity)
        entity.bodyComponent.body.type = BodyDef.BodyType.DynamicBody
    }

    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean  = false
}


class AwaitState(private val awaitTimeMillis: Int, private val nextState: EnemyTankState): EnemyTankState() {
    private var startTime: Long = 0L

    override fun enter(entity: EnemyTank) {
        super.enter(entity)
        startTime = TimeUtils.millis()
    }
    override fun update(entity: EnemyTank) {
        if(startTime + awaitTimeMillis < TimeUtils.millis()){
            //Time has elapsed, transition into next state
            entity.stateMachine.changeState(nextState)
        }
    }

}

class PeekState(private val peekAngle: Float): EnemyTankState(){
    override fun enter(entity: EnemyTank) {
        super.enter(entity)
        entity.currentWeapon.desiredRotationAngle = peekAngle
    }
    override fun update(entity: EnemyTank) {
        if(isPlayerInFOV(entity.gameContext.getWorld(), entity, entity.gameContext.getPlayer())) entity.stateMachine.changeState(ChaseState())

        //If it has rotated, switch to another state
        if(entity.currentWeapon.hasRotated()){
            val nextState = if(MathUtils.randomBoolean()){
                PeekState(MathUtils.random()* MathUtils.PI2)
            } else WanderState()

            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, nextState))
        }
    }
}

class WanderState: EnemyTankState(){
    private lateinit var path: GraphPath<Connection<Cell>>
    private var visitedCellCount = 0

    override fun enter(entity: EnemyTank) {
        super.enter(entity)
        path = generatePath(entity)
    }

    private fun generatePath(entity: EnemyTank): GraphPath<Connection<Cell>> {
        val mapTable = entity.gameContext.getMapTable()
        val pathFinding = entity.gameContext.getPathFinding()

        val currentCell = mapTable.toCell(entity.bodyComponent.body.position)
        val randomCell = mapTable.getRandomEmptyAdjacentCell(currentCell, 5)

        return pathFinding.findPath(currentCell, randomCell)
    }

    override fun update(entity: EnemyTank) {
        if(isPlayerInFOV(entity.gameContext.getWorld(), entity, entity.gameContext.getPlayer())) entity.stateMachine.changeState(ChaseState())

        if(!::path.isInitialized) {
            path = generatePath(entity)
            while(path.count == 0){
                path = generatePath(entity)
            }
        }

        val nextCell = if(visitedCellCount < path.count) path[visitedCellCount].toNode else null

        if(nextCell == null) {
            //If there is no next position, rotate to check the area
            val nextState = PeekState(MathUtils.random() * MathUtils.PI2)
            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, nextState))
        } else {
            val mapTable = entity.gameContext.getMapTable()
            val nextPosition = nextCell.toWorldPosition(mapTable)

            //Else, move to the next position
            val rotationAngle = MathUtils.atan2(nextPosition.y - entity.bodyComponent.body.position.y, nextPosition.x - entity.bodyComponent.body.position.x)
            entity.currentWeapon.desiredRotationAngle = rotationAngle
            entity.setOrientation(rotationAngle)


            if (withinBounds(entity.bodyComponent.body.position, nextPosition)){
                visitedCellCount++
            }
        }
    }

    private fun withinBounds(position: Vector2, targetPosition: Vector2): Boolean{
        return position.x >= targetPosition.x - CELL_OFFSET && position.x <= targetPosition.x + CELL_OFFSET
                && position.y >= targetPosition.y - CELL_OFFSET && position.y <= targetPosition.y + CELL_OFFSET
    }

    override fun exit(entity: EnemyTank) {
        entity.stopMovement()
    }
}
class ChaseState: EnemyTankState(){
    private val playerLastSeen = TimeUtils.millis()
    private var lastPathFindingExecution = TimeUtils.millis()

    override fun update(entity: EnemyTank) {
        val target = entity.gameContext.getPlayer()

        if(!target.healthComponent.isAlive){
            entity.stateMachine.changeState(WanderState())
            return
        }


        //If it has forgotten having seen the player, keep wandering
        if(TimeUtils.millis() - playerLastSeen > FORGET_TARGET_LIMIT_MILLIS) {
            val wanderState = WanderState()
            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, wanderState))
            return
        }
        //Else, check if it's in range

        val deltaX = target.bodyComponent.body.position.x - entity.bodyComponent.body.position.x
        val deltaY = target.bodyComponent.body.position.y - entity.bodyComponent.body.position.y

        //If the player is in range to shoot, check if its directly visible
        if(deltaX * deltaX  + deltaY * deltaY < SHOOTING_RADIUS_2){
            val world = entity.gameContext.getWorld()
            val isTargetVisible = checkTargetVisibility(world, entity.bodyComponent.body.position, target.bodyComponent.body.position)
            if(isTargetVisible){
                //If it is, then shoot
                entity.stateMachine.changeState(ShootState)
                return
            }
        }

        //Otherwise, chase it according to the path finding algorithm
        if(lastPathFindingExecution + PATHFINDING_EXECUTION_INTERVAL < TimeUtils.millis()){

            val pathFinding = entity.gameContext.getPathFinding()

            val nextPosition = pathFinding.findNextPosition(entity.bodyComponent.body.position, target.bodyComponent.body.position)
            if(nextPosition == null){
                //Path is blocked or doesn't exist, keep wandering
                entity.stateMachine.changeState(WanderState())
            }
            else {
                //Else, update position
                val nextDeltaX = nextPosition.x - entity.bodyComponent.body.position.x
                val nextDeltaY = nextPosition.y - entity.bodyComponent.body.position.y
                val moveAngle = atan2(nextDeltaY, nextDeltaX)

                entity.currentWeapon.desiredRotationAngle = moveAngle
                entity.setOrientation(moveAngle)
            }

            lastPathFindingExecution = TimeUtils.millis()
        }
    }

    override fun exit(entity: EnemyTank) {
        entity.stopMovement()
    }
    //Ignore damage when chasing
    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean = false
}

object ShootState: EnemyTankState(){

    override fun update(entity: EnemyTank) {
        val target = entity.gameContext.getPlayer()

        if(!target.healthComponent.isAlive){
            entity.stateMachine.changeState(WanderState())
            return
        }

        val deltaX = target.bodyComponent.body.position.x - entity.bodyComponent.body.position.x
        val deltaY = target.bodyComponent.body.position.y - entity.bodyComponent.body.position.y


        //If it is not in range, chase the target
        if(deltaX * deltaX + deltaY * deltaY > SHOOTING_RADIUS_2){
            entity.stateMachine.changeState(ChaseState())
            return
        }

        val world = entity.gameContext.getWorld()
        val isTargetVisible = checkTargetVisibility(world, entity.bodyComponent.body.position, target.bodyComponent.body.position)
        if(isTargetVisible){
            //If the player is in range and visible, aim and keep shooting
            val rotationAngle = MathUtils.atan2(deltaY, deltaX)
            entity.currentWeapon.desiredRotationAngle = rotationAngle
            entity.currentWeapon.isShooting = true
            return
        }


    }

    override fun exit(entity: EnemyTank) {
        entity.currentWeapon.isShooting = false
    }

    //Ignore damage when chasing
    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean = false
}
