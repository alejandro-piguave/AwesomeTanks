package com.alexpi.awesometanks.game.blocks.turret

import com.alexpi.awesometanks.game.blocks.Block
import com.alexpi.awesometanks.game.tanks.player.PlayerTank
import com.alexpi.awesometanks.game.utils.getNormalizedAbsoluteDifference
import com.alexpi.awesometanks.game.utils.normalizeAngle
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.TimeUtils

sealed class TurretState: State<Turret> {

    companion object{
        const val SHOOTING_RADIUS_2 = 5*5
        const val AWAIT_TIME_MILLIS = 2000
        const val FOV_ANGLE = 150 * MathUtils.degreesToRadians //120 DEGREES OF VISIBILITY
        const val DAMAGE_RECEIVED_MESSAGE = 0
    }

    protected fun isPlayerInFOV(world: World, entity: Turret, playerTank: PlayerTank): Boolean {
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
        val angleDifference = getNormalizedAbsoluteDifference(entity.weapon.currentRotationAngle.normalizeAngle(), angleOfTarget.normalizeAngle())

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

    override fun enter(entity: Turret) { }

    override fun exit(entity: Turret) { }

    override fun onMessage(entity: Turret, telegram: Telegram): Boolean {
        if(telegram.message == DAMAGE_RECEIVED_MESSAGE){
            entity.stateMachine.changeState(ShootState)
            return true
        }
        return false
    }

}

object FrozenState: TurretState(){
    override fun update(entity: Turret) {
        if(!entity.healthComponent.isFrozen){
            entity.stateMachine.changeState(entity.stateMachine.previousState)
        }
    }

    override fun onMessage(entity: Turret, telegram: Telegram): Boolean  = false
}


class AwaitState(private val awaitTimeMillis: Int, private val nextState: TurretState): TurretState() {
    private var startTime: Long = 0L

    override fun enter(entity: Turret) {
        super.enter(entity)
        startTime = TimeUtils.millis()
    }
    override fun update(entity: Turret) {
        if(startTime + awaitTimeMillis < TimeUtils.millis()){
            //Time has elapsed, transition into next state
            entity.stateMachine.changeState(nextState)
        }
    }

}

class PeekState(private val peekAngle: Float = MathUtils.random() * MathUtils.PI2): TurretState(){
    override fun enter(entity: Turret) {
        super.enter(entity)
        entity.weapon.desiredRotationAngle = peekAngle
    }
    override fun update(entity: Turret) {
        if(isPlayerInFOV(entity.gameContext.getWorld(), entity, entity.gameContext.getPlayer())) entity.stateMachine.changeState(ShootState)

        //If it has rotated, peek again
        if(entity.weapon.hasRotated()){
            entity.stateMachine.changeState(AwaitState(AWAIT_TIME_MILLIS, PeekState(MathUtils.random()* MathUtils.PI2)))
        }
    }
}

object ShootState: TurretState(){

    override fun update(entity: Turret) {
        val target = entity.gameContext.getPlayer()

        if(!target.healthComponent.isAlive){
            entity.stateMachine.changeState(PeekState())
            return
        }

        val deltaX = target.bodyComponent.body.position.x - entity.bodyComponent.body.position.x
        val deltaY = target.bodyComponent.body.position.y - entity.bodyComponent.body.position.y


        //If it is not in range, peek
        if(deltaX * deltaX + deltaY * deltaY > SHOOTING_RADIUS_2){
            entity.stateMachine.changeState(PeekState())
            return
        }

        val world = entity.gameContext.getWorld()
        val isTargetVisible = checkTargetVisibility(world, entity.bodyComponent.body.position, target.bodyComponent.body.position)
        if(isTargetVisible){
            //If the player is in range and visible, aim and keep shooting
            val rotationAngle = MathUtils.atan2(deltaY, deltaX)
            entity.weapon.desiredRotationAngle = rotationAngle
            entity.weapon.isShooting = true
            return
        }


    }

    override fun exit(entity: Turret) {
        entity.weapon.isShooting = false
    }

    //Ignore damage when chasing
    override fun onMessage(entity: Turret, telegram: Telegram): Boolean = false
}
