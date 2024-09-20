package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureProjectileGraphicsComponent(gameContext: GameContext, texturePath: String): ProjectileGraphicsComponent {

    private val textureRegion: TextureRegion = TextureRegion(gameContext.getAssetManager().get<Texture>(texturePath))
    override fun update(projectile: Projectile, delta: Float) { }

    override fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float) {
        with(projectile) {
            batch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        }
    }
}