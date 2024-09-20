package com.alexpi.awesometanks.game.projectiles.components.collision

import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Actor

class RicochetCollisionComponent(gameContext: GameContext): ProjectileCollisionComponent {
    companion object {
        private const val MAX_COLLISIONS = 3
    }

    private val bounceSound: Sound = gameContext.getAssetManager().get("sounds/ricochet.ogg")
    private var collisionCount = 0

    override fun handleCollision(projectile: Projectile, actor: Actor) {
        if(collisionCount < MAX_COLLISIONS) {
            collisionCount++
            bounceSound.play()
        } else {
            projectile.shouldBeDestroyed = true
        }
    }
}