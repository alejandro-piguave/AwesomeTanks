package com.alexpi.awesometanks.game.projectiles.components.remove

import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.game.projectiles.Projectile

class DefaultProjectileRemoveComponent: ProjectileRemoveComponent {
    override fun remove(projectile: Projectile) {
        projectile.parent.addActor(
            ParticleActor(
                projectile.gameContext,
                "particles/collision.party",
                projectile.x + projectile.width /2,
                projectile.y + projectile.height / 2,
                false
            )
        )
    }
}