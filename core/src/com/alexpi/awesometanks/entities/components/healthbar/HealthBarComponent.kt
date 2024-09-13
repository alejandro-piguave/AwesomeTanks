package com.alexpi.awesometanks.entities.components.healthbar

import com.alexpi.awesometanks.entities.actors.HealthBar
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class HealthBarComponent(private val healthBarGroup: Group, maxHealth: Float, currentHealth:Float) {
    private val healthBar: HealthBar = HealthBar(maxHealth, currentHealth)
    fun updatePosition(parent: Actor) {
        healthBar.setPosition(parent)
    }

    fun updateHealth(currentHealth: Float) {
        healthBar.currentHealth = currentHealth
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