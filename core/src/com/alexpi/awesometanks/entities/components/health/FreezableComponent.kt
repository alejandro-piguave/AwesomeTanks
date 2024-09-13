package com.alexpi.awesometanks.entities.components.health

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class FreezableComponent(assetManager: AssetManager, private val parent: Actor) {
    private val frozenTexture: Texture = assetManager.get("sprites/frozen.png", Texture::class.java)
    var isFrozen: Boolean = false
        private set

    private var startFreezeTime = 0L
    private var freezeDuration = 0f

    //burn duration in seconds
    fun freeze(duration: Float) {
        isFrozen = true
        startFreezeTime = TimeUtils.millis()
        freezeDuration = duration
    }

    fun act(delta: Float) {
        if (isFrozen) {
            if(startFreezeTime + freezeDuration * 1000 > TimeUtils.millis()) isFrozen = false
        }
    }

    fun draw(batch: SpriteBatch) {
        if(isFrozen) batch.draw(frozenTexture, parent.x, parent.y, parent.width, parent.height)
    }
}