package com.alexpi.awesometanks.game.components

import com.artemis.Component

class SmoothRotationComponent(var rotationSpeed: Float = 0f, var threshold: Float = 0f): Component() {
    var currentAngle = 0f
    var desiredAngle = 0f
}