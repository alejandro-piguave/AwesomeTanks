package com.alexpi.awesometanks.game.projectiles.components.physics

import com.alexpi.awesometanks.game.projectiles.Projectile

interface ProjectilePhysicsComponent {
    fun setUp(projectile: Projectile)
    fun update(projectile: Projectile)
    fun dispose()
}