package com.alexpi.awesometanks.entities.components

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class FlammableComponent(assetManager: AssetManager, val parent: Actor, val healthComponent: HealthComponent) {
    var isBurning: Boolean = false
        private set

    private var startBurningTime = 0L
    private var burnDuration = 0f
    private var burnDamage = 0f
    private val flameEffect: ParticleEffect = ParticleEffect(assetManager.get("particles/flame.party"))

    //burn duration in seconds
    fun burn(duration: Float, damage: Float) {
        isBurning = true
        startBurningTime = TimeUtils.millis()
        burnDuration = duration
    }


    fun act(delta: Float) {
        flameEffect.setPosition(parent.x + parent.width/2, parent.y + parent.height/2)
        if (isBurning) {
            flameEffect.update(delta)
            if(flameEffect.isComplete) flameEffect.reset()
            healthComponent.takeDamage(burnDamage)

            if(startBurningTime + burnDuration * 1000 > TimeUtils.millis()) isBurning = false
        }
    }

    fun draw(batch: SpriteBatch) {
        if(isBurning) flameEffect.draw(batch)
    }
}