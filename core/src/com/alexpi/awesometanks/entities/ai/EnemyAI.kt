package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.atan2

class EnemyAI(
              private val position: Vector2,
              private val callback: EnemyAICallback,
              visibilityRadius: Float = VISIBILITY_RADIUS) {
    private val world: World = GameModule.getWorld()
    private val target: PlayerTank = GameModule.getPlayer()
    private val pathFinding: AStartPathFinding = GameModule.getPathFinding()
    private val visibilityRadius2 = visibilityRadius * visibilityRadius
    private var isTargetVisible = false
    private var lastTargetSighting = 0L
    private var lastPathFindingExecution = 0L

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
            checkTargetVisibility()

            if(isTargetVisible){
                lastTargetSighting = TimeUtils.millis()
                callback.attack(angle)
                return
            }
        }

        if(lastTargetSighting + FORGET_TARGET_LIMIT_MILLIS > TimeUtils.millis()){
            searchPath()
        } else {
            //It's been 10 seconds since the target was last seen. Forget about it and await
            isTargetVisible = false
            callback.await()
        }

    }

    private fun searchPath(){
        if(lastPathFindingExecution + PATHFINDING_INTERVAL < TimeUtils.millis()){
            lastPathFindingExecution = TimeUtils.millis()
            Gdx.app.log("EnemyAI", "Finding next position...")
            val nextPosition = pathFinding.findNextPosition(position, target.position)
            if(nextPosition == null) callback.await()
            else {
                val deltaX = nextPosition.x - position.x
                val deltaY = nextPosition.y - position.y
                val moveAngle = atan2(deltaY, deltaX)
                Gdx.app.log("EnemyAI", "Moving to next position...")
                callback.move(moveAngle)
            }
        }
    }


    private fun checkTargetVisibility(){
        isTargetVisible = true
        world.rayCast({ fixture, _, _, _ ->
            if(fixture.userData is Block){
                isTargetVisible = false
                0f
            }
            1f
        } , position, target.position)
    }

    companion object {
        private const val VISIBILITY_RADIUS = 5f
        private const val FORGET_TARGET_LIMIT_MILLIS = 7000
        private const val PATHFINDING_INTERVAL = 500
    }
}

interface EnemyAICallback{
    fun attack(angle: Float)
    fun move(angle: Float)
    fun await()
}