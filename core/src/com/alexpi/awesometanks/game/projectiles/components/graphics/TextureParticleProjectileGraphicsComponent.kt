package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureParticleProjectileGraphicsComponent(gameContext: GameContext, texturePath: String, particlePath: String): ProjectileGraphicsComponent {

    private val textureRegion: TextureRegion = TextureRegion(gameContext.getAssetManager().get<Texture>(texturePath))
    private val particleEffect: ParticleEffect = ParticleEffect(gameContext.getAssetManager().get(particlePath))
    override fun update(projectile: Projectile, delta: Float) {
        particleEffect.setPosition(projectile.x + projectile.width/2, projectile.y + projectile.height/2)
        particleEffect.update(delta)
        if(particleEffect.isComplete) particleEffect.reset()
    }

    override fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float) {
        with(projectile) {
            batch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
            particleEffect.draw(batch)
        }
    }
}