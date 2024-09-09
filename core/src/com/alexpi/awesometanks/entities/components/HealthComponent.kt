package com.alexpi.awesometanks.entities.components

import com.badlogic.gdx.scenes.scene2d.Actor

class HealthComponent(
    private val parent: Actor,
    private val maxHealth: Float,
    var onDamageTaken: ((Actor) -> Unit)? = null,
    var onDeath: ((Actor) -> Unit)? = null) : Actor() {

    var health: Float = maxHealth
        set(value) {
            var damageTaken = false

            if(value >= maxHealth)
                field = maxHealth
            else if(value <= 0){
                if(value < health) damageTaken = true
                field = 0f
                if(damageTaken) onDamageTaken?.invoke(parent)
                onDeath?.invoke(parent)
            }
            else {
                if(value < health) damageTaken = true
                field = value
                if(damageTaken) onDamageTaken?.invoke(parent)
            }

        }

}