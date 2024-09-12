package com.alexpi.awesometanks.entities.components.body

sealed class BodyShape(val width: Float, val height: Float) {
    class Circular(val radius: Float): BodyShape(radius*2, radius*2)
    class Box(width: Float, height: Float): BodyShape(width, height)

}