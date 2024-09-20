package com.alexpi.awesometanks.game.projectiles.components.graphics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.badlogic.gdx.graphics.g2d.Batch

interface ProjectileGraphicsComponent {
    fun update(projectile: Projectile, delta: Float)
    fun draw(projectile: Projectile, batch: Batch, parentAlpha: Float)
}