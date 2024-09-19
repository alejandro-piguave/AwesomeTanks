package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.projectiles.components.collision.ProjectileCollisionComponent
import com.alexpi.awesometanks.game.projectiles.components.graphics.ProjectileGraphicsComponent
import com.alexpi.awesometanks.game.projectiles.components.physics.ProjectilePhysicsComponent
import com.alexpi.awesometanks.game.projectiles.components.remove.ProjectileRemoveComponent
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class Projectile(
    val gameContext: GameContext,
    private val damage: Float,
    private val physicsComponent: ProjectilePhysicsComponent,
    private val collisionComponent: ProjectileCollisionComponent,
    private val graphicsComponent: ProjectileGraphicsComponent,
    private val projectileRemoveComponent: ProjectileRemoveComponent
) : Actor() {

    init {
        physicsComponent.setUp(this)
    }

    var shouldBeDestroyed = false

    override fun remove(): Boolean {
        physicsComponent.dispose()
        projectileRemoveComponent.remove(this)
        return super.remove()
    }

    fun collide(actor: Actor) {
        collisionComponent.handleCollision(this, actor)
    }

    override fun act(delta: Float) {
        if (shouldBeDestroyed) {
            remove()
            return
        }
        super.act(delta)
        physicsComponent.update(this)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        graphicsComponent.draw(this, batch, parentAlpha)
    }
}