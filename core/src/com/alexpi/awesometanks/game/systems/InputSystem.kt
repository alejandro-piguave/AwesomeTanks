package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.LinearMovementComponent
import com.alexpi.awesometanks.game.components.PlayerComponent
import com.alexpi.awesometanks.game.utils.SQRT2_2
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor

@All(LinearMovementComponent::class, PlayerComponent::class)
class InputSystem: IteratingSystem(), InputProcessor {

    lateinit var linearMovementMapper: ComponentMapper<LinearMovementComponent>

    private var horizontalDirection = 0f
    private var verticalDirection = 0f

    override fun process(entityId: Int) {
        val component = linearMovementMapper[entityId]
        component.vX = calculateDx() * component.speed
        component.vY = calculateDy() * component.speed
    }

    private fun calculateDx(): Float {
        return if(horizontalDirection == 0f) 0f
        else if(verticalDirection == 0f) horizontalDirection
        else horizontalDirection * SQRT2_2
    }

    private fun calculateDy(): Float {
        return if(verticalDirection == 0f) 0f
        else if(horizontalDirection == 0f) verticalDirection
        else verticalDirection * SQRT2_2
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> {
                verticalDirection += 1f
                return true
            }
            Input.Keys.A -> {
                horizontalDirection -= 1f
                return true
            }
            Input.Keys.S -> {
                verticalDirection -= 1f
                return true
            }
            Input.Keys.D -> {
                horizontalDirection += 1f
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> {
                verticalDirection -= 1f
                return true
            }
            Input.Keys.A -> {
                horizontalDirection += 1f
                return true
            }
            Input.Keys.S -> {
                verticalDirection += 1f
                return true
            }
            Input.Keys.D -> {
                horizontalDirection -= 1f
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