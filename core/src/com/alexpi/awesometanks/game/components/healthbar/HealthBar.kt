package com.alexpi.awesometanks.game.components.healthbar

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class HealthBar(gameContext: GameContext, private val maxHealth: Float, var currentHealth: Float): Actor() {
    private val background: Texture = gameContext.getAssetManager().get("sprites/health_bar/health_bar_background.png")
    private val foreground: Texture = gameContext.getAssetManager().get("sprites/health_bar/health_bar_foreground.png")

    init {
        setSize(TILE_SIZE*.9f, TILE_SIZE * .12f)
    }

    fun setPosition(parent: Actor) {
        setPosition(parent.x + parent.width*.5f - width*.5f, parent.y + height*.5f + parent.height)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(background, x, y,width, height)
        batch.draw(foreground, x, y,width * (currentHealth/maxHealth), height)
    }

}