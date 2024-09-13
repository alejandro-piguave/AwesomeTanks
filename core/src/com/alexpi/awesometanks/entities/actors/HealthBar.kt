package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class HealthBar(private val damageableActor: DamageableActor): Actor() {
    private val background: Texture = GameModule.assetManager.get("sprites/health_bar/health_bar_background.png")
    private val foreground: Texture = GameModule.assetManager.get("sprites/health_bar/health_bar_foreground.png")

    init {
        setSize(TILE_SIZE*.9f, TILE_SIZE * .12f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        setPosition(damageableActor.x + damageableActor.width*.5f - width*.5f, damageableActor.y + height*.5f + damageableActor.height)
   }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(background, x, y,width, height)
        batch.draw(foreground, x, y,width * (damageableActor.health/damageableActor.maxHealth), height)
    }

}