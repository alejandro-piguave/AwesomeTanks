package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.atan2

class EnemyAI(private val world: World,
              private val position: Vector2,
              private val targetPosition: Vector2,
              private val callback: Callback,
              visibilityRadius: Float = VISIBILITY_RADIUS) {
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private var isTargetVisible = false

    init {
        aiNum++
    }

    fun update(delta: Float){
        val dX = targetPosition.x - position.x
        val dY = targetPosition.y - position.y

        val distanceFromTarget2 = dX*dX + dY*dY
        if(distanceFromTarget2 < visibilityRadius2){
            Gdx.app.log("AI $aiNum","Target in range, distance: ${Utils.fastHypot(dX.toDouble(),
                dY.toDouble()
            )}")
            if(isTargetVisible){
                Gdx.app.log("AI $aiNum","Attack")
                val angle = atan2(dY, dX)
                callback.attack(angle)
            } else{
                isTargetVisible = true
                Gdx.app.log("AI $aiNum","Begin ray cast")
                world.rayCast({ fixture, point, normal, fraction ->
                    if(fixture.userData is Block){
                        Gdx.app.log("AI $aiNum", "Block found")
                        isTargetVisible = false
                        0f
                    }
                    1f
                } , position, targetPosition)
                Gdx.app.log("AI $aiNum", "End ray cast")
                if(isTargetVisible)Gdx.app.log("AI $aiNum", "Target visible")
                else callback.await()
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

    companion object {
        private const val VISIBILITY_RADIUS = 5f
        private var aiNum = 0
    }
}