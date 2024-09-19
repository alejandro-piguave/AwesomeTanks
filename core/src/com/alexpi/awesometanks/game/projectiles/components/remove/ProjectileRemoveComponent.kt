package com.alexpi.awesometanks.game.projectiles.components.remove

import com.alexpi.awesometanks.game.projectiles.Projectile

interface ProjectileRemoveComponent {
    fun remove(projectile: Projectile)
}