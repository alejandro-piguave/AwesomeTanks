package com.alexpi.awesometanks.entities.components.healthbar

import com.alexpi.awesometanks.entities.actors.HealthBar
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class HealthBarComponent(gameContext: GameContext, maxHealth: Float, currentHealth:Float) {
    private val healthBarGroup = gameContext.getHealthBarGroup()
    private val healthBar: HealthBar = HealthBar(gameContext, maxHealth, currentHealth)
    fun updatePosition(parent: Actor) {
        healthBar.setPosition(parent)
    }

    fun updateHealth(currentHealth: Float, duration: Float? = null) {
        healthBar.currentHealth = currentHealth
        if(!healthBar.hasParent()) showHealthBar(duration)
    }

    fun hideHealthBar(){
        healthBar.remove()
    }

    fun showHealthBar(duration: Float? = null) {
        healthBarGroup.addActor(
            healthBar.apply {
                if(duration != null) {
                    addAction(
                        Actions.sequence(
                            Actions.delay(duration),
                            Actions.fadeOut(1f),
                            Actions.removeActor(),
                        )
                    )

                }
            }
        )
    }
}