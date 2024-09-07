package com.alexpi.awesometanks.world

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.MathUtils

object Rumble {
    var rumbleTimeLeft = 0f
        private set
    private var currentTime = 0f
    private var power = 0f
    private var currentPower = 0f
    val pos = Vector2()
    fun rumble(rumblePower: Float, rumbleLength: Float) {
        power = rumblePower
        rumbleTimeLeft = rumbleLength
        currentTime = 0f
    }

    fun tick(delta: Float): Vector2 {
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
}