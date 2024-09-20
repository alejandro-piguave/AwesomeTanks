package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class DoubleTextureProjectileGraphicsComponent(gameContext: GameContext, texture1Path: String, texture2Path: String): ProjectileGraphicsComponent {
    private val textureRegion1: TextureRegion = TextureRegion(gameContext.getAssetManager().get<Texture>(texture1Path))
    private val textureRegion2: TextureRegion = TextureRegion(gameContext.getAssetManager().get<Texture>(texture2Path))

    override fun update(projectile: Projectile, delta: Float) { }

    override fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float) {
        with(projectile) {
            batch.draw(textureRegion1, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
            batch.draw(textureRegion2, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        }
    }
}