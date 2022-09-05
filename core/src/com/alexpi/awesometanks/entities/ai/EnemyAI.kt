package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.atan2

class EnemyAI(private val world: World,
              private val position: Vector2,
              private val target: PlayerTank,
              private val callback: EnemyAICallback,
              visibilityRadius: Float = VISIBILITY_RADIUS) {
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private var isTargetVisible = false
    private var lastTargetSighting = 0L

    fun update(){
        if (!target.isAlive) {
            callback.await()
            return
        }

        val dX = target.position.x - position.x
        val dY = target.position.y - position.y

        val distanceFromTarget2 = dX*dX + dY*dY
        val angle = atan2(dY, dX)
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
                lastTargetSighting = TimeUtils.millis()
                callback.attack(angle)
            }else{
                if(lastTargetSighting + FORGET_TARGET_LIMIT_MILLIS > TimeUtils.millis()){
                    callback.approach(angle)
                } else {
                    //It's been 5 seconds since the target was last seen. Forget about it and await
                    isTargetVisible = false
                    callback.await()
                }
            }
        } else {
            if(lastTargetSighting + FORGET_TARGET_LIMIT_MILLIS > TimeUtils.millis()){
                callback.approach(angle)
            } else {
                //It's been 5 seconds since the target was last seen. Forget about it and await
                isTargetVisible = false
                callback.await()
            }
        }

    }

    companion object {
        private const val VISIBILITY_RADIUS = 7f
        private const val FORGET_TARGET_LIMIT_MILLIS = 5000
    }
}

interface EnemyAICallback{
    fun attack(angle: Float)
    fun approach(angle: Float)
    fun await()
}