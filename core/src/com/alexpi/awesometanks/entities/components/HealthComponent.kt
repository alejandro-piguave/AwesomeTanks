package com.alexpi.awesometanks.entities.components

import com.badlogic.gdx.scenes.scene2d.Actor

class HealthComponent(
    private val parent: Actor,
    private val maxHealth: Float,
    var onDamageTaken: ((Actor) -> Unit)? = null,
    var onDeath: ((Actor) -> Unit)? = null) : Actor() {

    var currentHealth: Float = maxHealth
        private set

    fun takeDamage(damage: Float) {
        if(damage <= 0) throw IllegalArgumentException("damage must be greater than 0.")
        currentHealth = (currentHealth - damage).coerceAtLeast(0f)
        onDamageTaken?.invoke(parent)
        if(currentHealth <= 0) onDeath?.invoke(parent)
    }

    fun heal(health: Float) {
        if(health <= 0) throw IllegalArgumentException("health must be greater than 0.")
        currentHealth = (currentHealth + health).coerceAtMost(maxHealth)
    }

}