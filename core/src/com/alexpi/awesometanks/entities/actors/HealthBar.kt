package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class HealthBar(private val damageableActor: DamageableActor,
                //Duration zero means inifinity
                private val duration: Int = 0): Actor() {
    private val firstAct = TimeUtils.millis()
    private val healthBarSprite: Sprite = Sprite(GameModule.assetManager.get("sprites/health_bar.png", Texture::class.java))

    init {
        setSize(Constants.TILE_SIZE, Constants.TILE_SIZE * .125f)
    }

    override fun act(delta: Float) {
        setPosition(damageableActor.x + damageableActor.width*.5f - width*.5f, damageableActor.y + height*.5f + damageableActor.height)
        if(duration == 0 ) return
        if(firstAct + duration < duration){
            remove()
            return
        }
   }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(healthBarSprite, x, y,width * (damageableActor.health/damageableActor.maxHealth), height)
    }

}