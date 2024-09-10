package com.alexpi.awesometanks.entities.components.body

sealed class BodyShape {
    data class Circular(val radius: Float): BodyShape()
    data class Box(val width: Float, val height: Float): BodyShape()

    fun getWidth(): Float {
        return when(this) {
            is Box -> width
            is Circular -> radius*2
        }
    }

    fun getHeight(): Float {
        return when(this) {
            is Box -> height
            is Circular -> radius*2
        }
    }
}