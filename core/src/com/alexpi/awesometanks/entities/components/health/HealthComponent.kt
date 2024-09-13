package com.alexpi.awesometanks.entities.components.health

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class HealthComponent(
    assetManager: AssetManager,
    private val parent: Actor,
    private val maxHealth: Float,
    initialState: HealthState = HealthState.Normal,
    private val isFlammable: Boolean = true,
    private val isFreezable: Boolean = true,
    var onDamageTaken: ((Actor) -> Unit)? = null,
    var onDeath: ((Actor) -> Unit)? = null){

    private var currentHealth: Float = maxHealth
    var healthState = initialState
        private set

    private val flameEffect: ParticleEffect = ParticleEffect(assetManager.get("particles/flame.party"))
    private val frozenTexture: Texture = assetManager.get("sprites/frozen.png", Texture::class.java)

    fun takeDamage(damage: Float) {
        if(damage <= 0) throw IllegalArgumentException("damage must be greater than 0.")
        currentHealth = (currentHealth - damage).coerceAtLeast(0f)
        onDamageTaken?.invoke(parent)
    }

    fun heal(health: Float) {
        if(health <= 0) throw IllegalArgumentException("health must be greater than 0.")
        currentHealth = (currentHealth + health).coerceAtMost(maxHealth)
    }

    //burn duration in seconds
    fun burn(duration: Float, damage: Float) {
        if(!isFlammable) return
        healthState = HealthState.Burning(startTime = TimeUtils.millis(), duration = duration, damage = damage)
    }

    fun freeze(duration: Float) {
        if(!isFreezable) return
        healthState = HealthState.Frozen(startTime = TimeUtils.millis(), duration = duration)
    }

    fun act(delta: Float) {
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

    fun draw(batch: Batch) {
        when(healthState) {
            HealthState.Normal -> {}
            is HealthState.Burning -> flameEffect.draw(batch)
            is HealthState.Frozen -> batch.draw(frozenTexture, parent.x, parent.y, parent.width, parent.height)
        }
    }

}