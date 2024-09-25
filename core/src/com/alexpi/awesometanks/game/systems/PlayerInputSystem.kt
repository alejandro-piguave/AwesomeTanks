package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.LinearMovementComponent
import com.alexpi.awesometanks.game.components.SmoothRotationComponent
import com.alexpi.awesometanks.game.tags.Tags
import com.alexpi.awesometanks.game.utils.SQRT2_2
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.managers.TagManager
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.MathUtils

@All(LinearMovementComponent::class)
class PlayerInputSystem: BaseEntitySystem(), InputProcessor {

    lateinit var tagManager: TagManager
    lateinit var linearMovementMapper: ComponentMapper<LinearMovementComponent>
    lateinit var smoothRotationMapper: ComponentMapper<SmoothRotationComponent>

    private var horizontalDirection = 0f
    private var verticalDirection = 0f

    private var dx = 0f
    private var dy = 0f
    private var desiredRotation = 0f

    override fun processSystem() {
        val linearMovementComponent = linearMovementMapper[tagManager.getEntityId(Tags.PLAYER)]
        linearMovementComponent.vX = dx * linearMovementComponent.speed
        linearMovementComponent.vY = dy* linearMovementComponent.speed

        val smoothRotationComponent = smoothRotationMapper[tagManager.getEntityId(Tags.PLAYER)]
        smoothRotationComponent.desiredAngle = desiredRotation
    }

    private fun updateDesiredAngle() {
        if(horizontalDirection == 0f && verticalDirection == 0f) return
        desiredRotation =  MathUtils.atan2(verticalDirection, horizontalDirection)
    }

    private fun updateDx() {
        dx = if(horizontalDirection == 0f) 0f
        else if(verticalDirection == 0f) horizontalDirection
        else horizontalDirection * SQRT2_2
    }

    private fun updateDy() {
        dy = if(verticalDirection == 0f) 0f
        else if(horizontalDirection == 0f) verticalDirection
        else verticalDirection * SQRT2_2
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> {
                verticalDirection += 1f
                updateDy()
                updateDesiredAngle()
                return true
            }
            Input.Keys.A -> {
                horizontalDirection -= 1f
                updateDx()
                updateDesiredAngle()
                return true
            }
            Input.Keys.S -> {
                verticalDirection -= 1f
                updateDy()
                updateDesiredAngle()
                return true
            }
            Input.Keys.D -> {
                horizontalDirection += 1f
                updateDx()
                updateDesiredAngle()
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> {
                verticalDirection -= 1f
                updateDy()
                updateDesiredAngle()
                return true
            }
            Input.Keys.A -> {
                horizontalDirection += 1f
                updateDx()
                updateDesiredAngle()
                return true
            }
            Input.Keys.S -> {
                verticalDirection += 1f
                updateDy()
                updateDesiredAngle()
                return true
            }
            Input.Keys.D -> {
                horizontalDirection -= 1f
                updateDx()
                updateDesiredAngle()
                return true
            }
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }


}