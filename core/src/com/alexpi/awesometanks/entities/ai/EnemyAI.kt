package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.atan2

class EnemyAI(private val world: World,
              private val position: Vector2,
              private val targetPosition: Vector2,
              private val callback: Callback,
              visibilityRadius: Float = VISIBILITY_RADIUS) : RayCastCallback {
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private var isTargetVisible = false

    fun update(delta: Float){
        val dX = targetPosition.x - position.x
        val dY = targetPosition.y - position.y

        val distanceFromTarget2 = dX*dX + dY*dY
        if(distanceFromTarget2 < visibilityRadius2){
            if(isTargetVisible){
                val angle = atan2(dY, dX)
                callback.attack(angle)
            } else{
                world.rayCast(this, position, targetPosition)
            }
        } else {
            isTargetVisible = false
            callback.await()
        }

    }
    interface Callback{
        fun attack(angle: Float)
        fun await()
    }

    override fun reportRayFixture(fixture: Fixture, point: Vector2, normal: Vector2, fraction: Float): Float {
        if(fixture.userData is Block){
            isTargetVisible = false
            return 0f
        }else if(fixture.userData is PlayerTank && !isTargetVisible){
            isTargetVisible = true
        }
        return -1f
    }


    companion object {
        private const val VISIBILITY_RADIUS = 5f
    }
}