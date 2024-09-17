package com.alexpi.awesometanks.game.components.healthbar

import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class HealthBarComponent(gameContext: GameContext, maxHealth: Float, currentHealth:Float, private val duration: Float?) {
    private val healthBarGroup = gameContext.getHealthBarGroup()
    private val healthBar: HealthBar = HealthBar(gameContext, maxHealth, currentHealth)
    fun updatePosition(parent: Actor) {
        healthBar.setPosition(parent)
    }

    fun updateHealth(currentHealth: Float) {
        healthBar.currentHealth = currentHealth
        if(!healthBar.hasParent()) showHealthBar()
    }

    fun hideHealthBar(){
        healthBar.remove()
    }

    private fun showHealthBar() {
        healthBarGroup.addActor(
            healthBar.apply {
                if(duration != null) {
                    addAction(
                        Actions.sequence(
                            Actions.alpha(1f),
                            Actions.delay(duration),
                            Actions.fadeOut(1f),
                            Actions.removeActor(this@apply),
                        )
                    )

                }
            }
        )
    }
}