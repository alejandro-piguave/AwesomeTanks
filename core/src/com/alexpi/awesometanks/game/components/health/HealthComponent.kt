package com.alexpi.awesometanks.game.components.health

import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class HealthComponent(
    gameContext: GameContext,
    val maxHealth: Float,
    private val isFlammable: Boolean,
    private val isFreezable: Boolean,
    initialState: HealthState = HealthState.Normal,
    var onHealthChanged: ((Float) -> Unit)? = null,
    var onFreeze: (() -> Unit)? = null,
    var onDeath: ((Actor) -> Unit)? = null){
    var currentHealth: Float = maxHealth
    private set
    var healthState = initialState
        private set

    var damageReduction = 0f
        set(value) {
            if(value < 0 || value > 1) throw IllegalArgumentException("damageReduction must be between 0 and 1")
            field = value
        }

    val isAlive: Boolean get() = currentHealth > 0
    val isFrozen: Boolean get() = healthState is HealthState.Frozen

    private val flameEffect: ParticleEffect = ParticleEffect(gameContext.getAssetManager().get("particles/flame.party"))
    private val frozenTexture: Texture = gameContext.getAssetManager().get("sprites/frozen.png", Texture::class.java)

    fun takeDamage(damage: Float) {
        if(damage <= 0) throw IllegalArgumentException("damage must be greater than 0.")
        val finalDamage = damage * (1 - damageReduction)
        currentHealth = (currentHealth - finalDamage).coerceAtLeast(0f)
        onHealthChanged?.invoke(currentHealth)
    }

    fun kill() {
        currentHealth = 0f
    }

    fun heal(health: Float) {
        if(health <= 0) throw IllegalArgumentException("health must be greater than 0.")
        currentHealth = (currentHealth + health).coerceAtMost(maxHealth)
        onHealthChanged?.invoke(currentHealth)
    }

    //burn duration in seconds
    fun burn(duration: Float, damage: Float) {
        if(!isFlammable) return
        healthState = HealthState.Burning(startTime = TimeUtils.millis(), duration = duration, damage = damage)
    }

    fun freeze(duration: Float) {
        if(!isFreezable) return
        healthState = HealthState.Frozen(startTime = TimeUtils.millis(), duration = duration)
        onFreeze?.invoke()
    }


    fun update(parent: Actor, delta: Float) {
        if(currentHealth <= 0) {
            onDeath?.invoke(parent)
            return
        }
        when(val state = healthState) {
            is HealthState.Burning -> {
                flameEffect.setPosition(parent.x + parent.width/2, parent.y + parent.height/2)
                flameEffect.update(delta)
                if(flameEffect.isComplete) flameEffect.reset()
                takeDamage(state.damage)

                if(state.startTime + state.duration * 1000 > TimeUtils.millis()) healthState = HealthState.Normal
            }
            is HealthState.Frozen -> {
                if(state.startTime + state.duration * 1000 > TimeUtils.millis()) healthState = HealthState.Normal
            }
            HealthState.Normal -> {}
        }
    }

    fun draw(parent: Actor, batch: Batch) {
        when(healthState) {
            HealthState.Normal -> {}
            is HealthState.Burning -> flameEffect.draw(batch)
            is HealthState.Frozen -> batch.draw(frozenTexture, parent.x, parent.y, parent.width, parent.height)
        }
    }

}