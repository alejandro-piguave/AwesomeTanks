package com.alexpi.awesometanks.entities.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

// Must be added to the stage after the player since the player also positions the camera
class RumbleController: Actor() {

    private var rumbleTimeLeft = 0f
    private var currentTime = 0f
    private var power = 0f
    private var currentPower = 0f
    val pos = Vector2()
    
    fun rumble(rumblePower: Float, rumbleLength: Float) {
        power = rumblePower
        rumbleTimeLeft = rumbleLength
        currentTime = 0f
    }

    private fun tick(delta: Float): Vector2 {
        if (currentTime <= rumbleTimeLeft) {
            currentPower = power * ((rumbleTimeLeft - currentTime) / rumbleTimeLeft)
            pos.x = (MathUtils.random() - 0.5f) * 2 * currentPower
            pos.y = (MathUtils.random() - 0.5f) * 2 * currentPower
            currentTime += delta
        } else {
            rumbleTimeLeft = 0f
        }
        return pos
    }

    override fun act(delta: Float) {
        if (rumbleTimeLeft > 0){
            tick(delta)
            stage.camera.translate(pos.x, pos.y,0f)
        }
    }
}