package com.alexpi.awesometanks.game.projectiles.components.collision

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.badlogic.gdx.scenes.scene2d.Actor

interface ProjectileCollisionComponent {

    fun handleCollision(projectile: Projectile, actor: Actor)
}