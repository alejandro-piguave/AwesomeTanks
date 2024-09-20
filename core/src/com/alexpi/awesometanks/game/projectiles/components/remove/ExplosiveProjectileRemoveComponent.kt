package com.alexpi.awesometanks.game.projectiles.components.remove

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext

class ExplosiveProjectileRemoveComponent(gameContext: GameContext): ProjectileRemoveComponent {
    private val explosionManager = gameContext.getExplosionManager()
    override fun remove(projectile: Projectile) {
        explosionManager.createCanonBallExplosion(projectile.physicsComponent.getPosition().x, projectile.physicsComponent.getPosition().y)
    }
}