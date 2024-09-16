package com.alexpi.awesometanks.game.ai

import com.alexpi.awesometanks.game.blocks.Block
import com.alexpi.awesometanks.game.blocks.Turret
import com.alexpi.awesometanks.game.tanks.player.PlayerTank
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.atan2

class TurretAI(gameContext: GameContext, private val owner: Turret, private val position: Vector2,
               visibilityRadius: Float = VISIBILITY_RADIUS) {
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private val target: PlayerTank = gameContext.getPlayer()
    private val world: World = gameContext.getWorld()
    private var isTargetVisible = false

    fun update(){
        if (!target.healthComponent.isAlive){
            owner.weapon.isShooting = false
            return
        }
        val dX = target.position.x - position.x
        val dY = target.position.y - position.y

        val distanceFromTarget2 = dX*dX + dY*dY
        if(distanceFromTarget2 < visibilityRadius2){
            isTargetVisible = true
            world.rayCast({ fixture, _, _, _ ->
                if(fixture.userData is Block){
                    isTargetVisible = false
                    0f
                }
                1f
            } , position, target.position)
            if(isTargetVisible){
                val angle = atan2(dY, dX)
                owner.weapon.desiredRotationAngle = angle
                owner.weapon.isShooting = true
            } else{
                owner.weapon.isShooting = false
            }
        } else {
            isTargetVisible = false
            owner.weapon.isShooting = false
        }

    }

    companion object {
        private const val VISIBILITY_RADIUS = 9f
    }
}