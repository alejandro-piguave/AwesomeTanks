package com.alexpi.awesometanks.entities.components.body

sealed class BodyShape {
    data class Circular(val radius: Float): BodyShape()
    data class Box(val width: Float, val height: Float): BodyShape()
}