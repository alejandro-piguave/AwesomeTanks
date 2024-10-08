package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect

class ParticleProjectileGraphicsComponent(gameContext: GameContext, particlePath: String): ProjectileGraphicsComponent {

    private val particleEffect: ParticleEffect = ParticleEffect(gameContext.getAssetManager().get(particlePath))
    override fun update(projectile: Projectile, delta: Float) {
        particleEffect.setPosition(projectile.x + projectile.width/2, projectile.y + projectile.height/2)
        particleEffect.update(delta)
        if(particleEffect.isComplete) particleEffect.reset()
    }

    override fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float) {
        particleEffect.draw(batch)
    }
}