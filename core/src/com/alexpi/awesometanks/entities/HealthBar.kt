package com.alexpi.awesometanks.entities

import com.alexpi.awesometanks.utils.Constants
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

class HealthBar(assetManager: AssetManager,
                private val damageableActor: DamageableActor,
                //Duration zero means inifinity
                private val duration: Float = 0f): Actor() {
    private var accumulator: Float = 0f
    private val healthBarSprite: Sprite = Sprite(assetManager.get("sprites/health_bar.png", Texture::class.java))

    init {
        setSize(Constants.TILE_SIZE, Constants.TILE_SIZE * .125f)
    }

    override fun act(delta: Float) {
        setPosition(damageableActor.x + damageableActor.width*.5f - width*.5f, damageableActor.y + height*.5f + damageableActor.height)
        if(duration == 0f ) return
        accumulator += delta
        if(accumulator >= duration){
            remove()
            return
        }
   }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(healthBarSprite, x, y,width * (damageableActor.health/damageableActor.maxHealth), height)
    }

}