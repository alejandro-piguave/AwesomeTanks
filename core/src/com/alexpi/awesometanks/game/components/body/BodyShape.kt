package com.alexpi.awesometanks.game.components.body

import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape

sealed class BodyShape(val width: Float, val height: Float) {

    abstract fun createShape(): Shape
    class Circular(val radius: Float): BodyShape(radius*2, radius*2) {
        override fun createShape(): Shape = CircleShape().apply { radius = this@Circular.radius }
    }
    class Box(width: Float, height: Float): BodyShape(width, height) {
        override fun createShape(): Shape = PolygonShape().apply {
            setAsBox(
                this@Box.width / 2,
                this@Box.height / 2
            )
        }
    }

}