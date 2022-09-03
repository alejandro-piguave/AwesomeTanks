package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.atan2

class TurretAI(private val world: World,
               private val position: Vector2,
               private val target: PlayerTank,
               private val callback: EnemyAICallback,
               visibilityRadius: Float = VISIBILITY_RADIUS) {
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private var isTargetVisible = false

    fun update(){
        if (!target.isAlive){
            callback.await()
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
                callback.attack(angle)
            } else{
                callback.await()
            }
        } else {
            isTargetVisible = false
            callback.await()
        }

    }

    companion object {
        private const val VISIBILITY_RADIUS = 9f
    }
}