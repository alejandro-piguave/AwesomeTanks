package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.SmoothRotationComponent
import com.alexpi.awesometanks.game.utils.getNormalizedAbsoluteDifference
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils

@All(SmoothRotationComponent::class)
class SmoothRotationSystem: IteratingSystem() {
    lateinit var smoothRotationMapper: ComponentMapper<SmoothRotationComponent>

    override fun process(entityId: Int) {
        with(smoothRotationMapper[entityId]) {
            desiredAngle = normalizeAngle(desiredAngle)
            currentAngle = updateRotationAngle(currentAngle, desiredAngle, rotationSpeed, threshold)
        }
    }

    private fun updateRotationAngle(currentRotationAngle: Float, desiredRotationAngle: Float, rotationSpeed: Float, threshold: Float): Float {
        if(currentRotationAngle == desiredRotationAngle) {
            return currentRotationAngle
        }

        val difference = getNormalizedAbsoluteDifference(currentRotationAngle, desiredRotationAngle)

        if(difference < threshold) {
            return desiredRotationAngle
        }

        var updatedRotationAngle = currentRotationAngle

        if(currentRotationAngle < MathUtils.PI) {
            if(desiredRotationAngle > currentRotationAngle && desiredRotationAngle < currentRotationAngle + MathUtils.PI) {
                updatedRotationAngle += rotationSpeed * world.delta
            } else {
                updatedRotationAngle -= rotationSpeed * world.delta
            }
        } else {
            if(desiredRotationAngle > currentRotationAngle - MathUtils.PI && desiredRotationAngle < currentRotationAngle) {
                updatedRotationAngle -= rotationSpeed * world.delta
            } else {
                updatedRotationAngle += rotationSpeed * world.delta
            }
        }

        if(updatedRotationAngle < 0) updatedRotationAngle += MathUtils.PI2
        else if(updatedRotationAngle >= MathUtils.PI2) updatedRotationAngle -= MathUtils.PI2

        return updatedRotationAngle
    }

    private fun normalizeAngle(angle: Float): Float {
        return if(angle >= MathUtils.PI2)
            angle % MathUtils.PI2
        else if(angle < 0){
            angle % MathUtils.PI2 + MathUtils.PI2
        } else angle
    }
}