package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.atan2

sealed class EnemyTankState: State<EnemyTank>{

    companion object{
        const val SHOOTING_RADIUS_2 = 5*5
        const val FORGET_TARGET_LIMIT_MILLIS = 5000
        const val PATHFINDING_EXECUTION_INTERVAL = 500
        const val AWAIT_TIME_MILLIS = 2000
        const val HALF_VISIBILITY_ANGLE = 60 * MathUtils.degreesToRadians //120 DEGREES OF VISIBILITY / 2
        const val DAMAGE_RECEIVED_MESSAGE = 0
        const val CELL_OFFSET = .05f
        const val VISITED_CELL_COUNT_LIMIT = 3
    }

    val target = GameModule.player
    val gameMap = GameModule.mapTable
    val pathFinding = GameModule.pathFinding

    override fun update(entity: EnemyTank) {
        if(target.healthComponent.isAlive){
            val deltaX = target.position.x - entity.bodyComponent.body.position.x
            val deltaY = target.position.y - entity.bodyComponent.body.position.y

            //If the player is in range, check if it's within the visibility angle
            if(deltaX * deltaX  + deltaY * deltaY < SHOOTING_RADIUS_2){
                val angleOfTarget = MathUtils.atan2(deltaY,deltaX)
                //Angle difference in range of -PI, +PI
                val angleDifference = (entity.currentWeapon.currentRotationAngle - angleOfTarget + MathUtils.PI + MathUtils.PI2) % MathUtils.PI2 - MathUtils.PI
                if(angleDifference <= HALF_VISIBILITY_ANGLE && angleDifference >= -HALF_VISIBILITY_ANGLE){
                    //If the player is within the visibility angle, check if there's no obstacles in between
                    val isTargetVisible = checkTargetVisibility(entity.bodyComponent.body.position)
                    if(isTargetVisible){
                        entity.stateMachine.changeState(ChaseState())
                        return
                    }
                }
            }
        }
    }

    protected fun checkTargetVisibility(position: Vector2): Boolean{
        var isVisible = true
        GameModule.world.rayCast({ fixture, _, _, _ ->
            if(fixture.userData is Block){
                isVisible = false
                0f
            }
            1f
        } , position, target.position)
        return isVisible
    }

    override fun enter(entity: EnemyTank) { }

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

    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean  = false
}


class AwaitState(private val awaitTimeMillis: Int, private val nextState: EnemyTankState): EnemyTankState() {
    private var startTime: Long = 0L

    override fun enter(entity: EnemyTank) {
        super.enter(entity)
        startTime = TimeUtils.millis()
    }
    override fun update(entity: EnemyTank) {
        super.update(entity)
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
        super.update(entity)
        //If it has rotated, switch to another state
        if(entity.currentWeapon.hasRotated()){
            val nextState = if(MathUtils.randomBoolean()){
                PeekState(MathUtils.random()* MathUtils.PI2)
            } else WanderState(entity.bodyComponent.body.position.cpy())

            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, nextState))
        }
    }
}
class WanderState(startingPosition: Vector2): EnemyTankState(){
    private val path: GraphPath<Connection<Cell>>
    private var nextPosition: Vector2
    private var visitedCellCount = 0
    init {
        val currentCell = gameMap.toCell(startingPosition)
        val randomCell = gameMap.getRandomEmptyAdjacentCell(currentCell, 2)
        path = pathFinding.findPath(currentCell, randomCell)
        val nextCell = if (path.count > 0 ) path[0].toNode else null
        nextPosition = nextCell?.let { it.toWorldPosition(gameMap)} ?: startingPosition
    }

    override fun update(entity: EnemyTank) {
        super.update(entity)

        if (withinBounds(entity.bodyComponent.body.position)){
            visitedCellCount++
            val currentCell = gameMap.toCell(entity.bodyComponent.body.position)
            val nextCell = path.firstOrNull { it.fromNode.col == currentCell.col && it.fromNode.row == currentCell.row }?.toNode
            if(nextCell == null || visitedCellCount > VISITED_CELL_COUNT_LIMIT){
                //If there is no next position, rotate to check the area
                val nextState = PeekState(MathUtils.random() * MathUtils.PI2)
                entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, nextState))
                return
            } else {
                nextPosition = nextCell.toWorldPosition(gameMap)
            }
        }

        //Else, move to the next position
        val rotationAngle = MathUtils.atan2(nextPosition.y - entity.bodyComponent.body.position.y, nextPosition.x - entity.bodyComponent.body.position.x)

        entity.currentWeapon.desiredRotationAngle = rotationAngle
        entity.setOrientation(rotationAngle)
    }

    private fun withinBounds(position: Vector2): Boolean{
        return position.x >= nextPosition.x - CELL_OFFSET && position.x <= nextPosition.x + CELL_OFFSET
                && position.y >= nextPosition.y - CELL_OFFSET && position.y <= nextPosition.y + CELL_OFFSET
    }

    override fun exit(entity: EnemyTank) {
        entity.stopMovement()
    }
}
class ChaseState: EnemyTankState(){
    private val playerLastSeen = TimeUtils.millis()
    private var lastPathFindingExecution = TimeUtils.millis()

    override fun update(entity: EnemyTank) {
        if(!target.healthComponent.isAlive){
            entity.stateMachine.changeState(WanderState(entity.bodyComponent.body.position.cpy()))
            return
        }
        //If it still remembers having seen the player, check if it's in range
        if(playerLastSeen + FORGET_TARGET_LIMIT_MILLIS > TimeUtils.millis()){
            val deltaX = target.position.x - entity.bodyComponent.body.position.x
            val deltaY = target.position.y - entity.bodyComponent.body.position.y

            //If the player is in range to shoot, check if its directly visible
            if(deltaX * deltaX  + deltaY * deltaY < SHOOTING_RADIUS_2){
                val isTargetVisible = checkTargetVisibility(entity.bodyComponent.body.position)
                if(isTargetVisible){
                    //If it is, then shoot
                    entity.stateMachine.changeState(ShootState)
                    return
                }
            }


            //Otherwise, chase it according to the path finding algorithm
            if(lastPathFindingExecution + PATHFINDING_EXECUTION_INTERVAL < TimeUtils.millis()){
                lastPathFindingExecution = TimeUtils.millis()

                val nextPosition = pathFinding.findNextPosition(entity.bodyComponent.body.position, target.position)
                if(nextPosition == null){
                    //Path is blocked or doesn't exist, keep wandering
                    entity.stateMachine.changeState(WanderState(entity.bodyComponent.body.position.cpy()))
                }
                else {
                    //Else, update position
                    val nextDeltaX = nextPosition.x - entity.bodyComponent.body.position.x
                    val nextDeltaY = nextPosition.y - entity.bodyComponent.body.position.y
                    val moveAngle = atan2(nextDeltaY, nextDeltaX)

                    entity.currentWeapon.desiredRotationAngle = moveAngle
                    entity.setOrientation(moveAngle)
                }
            }

        } else{
            //Else keep wandering
            val wanderState = WanderState(entity.bodyComponent.body.position.cpy())
            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, wanderState))
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
        if(!target.healthComponent.isAlive){
            entity.stateMachine.changeState(WanderState(entity.bodyComponent.body.position.cpy()))
            return
        }

        val deltaX = target.position.x - entity.bodyComponent.body.position.x
        val deltaY = target.position.y - entity.bodyComponent.body.position.y


        if(deltaX * deltaX + deltaY * deltaY < SHOOTING_RADIUS_2){
            val isTargetVisible = checkTargetVisibility(entity.bodyComponent.body.position)
            if(isTargetVisible){
                //If the player is in range and visible, aim and keep shooting
                val rotationAngle = MathUtils.atan2(deltaY, deltaX)
                entity.currentWeapon.desiredRotationAngle = rotationAngle
                entity.isShooting = true
                return
            }
        }

        //Else, chase it
        entity.stateMachine.changeState(ChaseState())

    }

    override fun exit(entity: EnemyTank) {
        entity.isShooting = false
    }

    //Ignore damage when chasing
    override fun onMessage(entity: EnemyTank, telegram: Telegram): Boolean = false
}
