package com.alexpi.awesometanks.game.projectiles.components.physics

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.badlogic.gdx.math.Vector2

interface ProjectilePhysicsComponent {
    fun getPosition(): Vector2
    fun setUp(projectile: Projectile)
    fun update(projectile: Projectile)
    fun dispose()
}