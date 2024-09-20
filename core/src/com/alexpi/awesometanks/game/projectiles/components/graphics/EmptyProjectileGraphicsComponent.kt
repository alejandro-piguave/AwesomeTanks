package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.badlogic.gdx.graphics.g2d.Batch

class EmptyProjectileGraphicsComponent: ProjectileGraphicsComponent {
    override fun update(projectile: Projectile, delta: Float) { }

    override fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float) { }

}