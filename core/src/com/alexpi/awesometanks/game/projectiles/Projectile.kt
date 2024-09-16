package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyComponent
import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by Alex on 16/01/2016.
 */
abstract class Projectile(
    val gameContext: GameContext,
    position: Vector2,
    bodyShape: BodyShape,
    angle: Float,
    val speed: Float,
    private val damage: Float,
    val isPlayer: Boolean,
) : Actor() {

    protected var sprite: Sprite? = null
    var shouldBeDestroyed = false
        private set


    val bodyComponent = BodyComponent(
        this,
        gameContext.getWorld(),
        bodyShape,
        BodyDef.BodyType.DynamicBody,
        if(isPlayer) FixtureFilter.PLAYER_BULLET else FixtureFilter.ENEMY_BULLET,
        position,
        1f
    )

    override fun remove(): Boolean {
        bodyComponent.destroy()
        parent.addActor(
            ParticleActor(
                gameContext,
                "particles/collision.party",
                x + width /2,
                y + height / 2,
                false
            )
        )
        return super.remove()
    }

    fun collide(actor: Actor) {
        handleCollision(actor)
        shouldBeDestroyed = shouldBeDestroyedAfterCollision(actor)
    }

    open fun shouldBeDestroyedAfterCollision(actor: Actor) = true

    open fun handleCollision(actor: Actor) {
        if (actor is HealthOwner) actor.healthComponent.takeDamage(damage)
    }

    override fun act(delta: Float) {
        if (shouldBeDestroyed) {
            remove()
            return
        }
        setPosition(
            bodyComponent.left * TILE_SIZE,
            bodyComponent.bottom * TILE_SIZE
        )

        rotation = bodyComponent.body.angle * MathUtils.radiansToDegrees
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (sprite != null) batch.draw(
            sprite,
            x,
            y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
    }

    init {
        bodyComponent.body.isBullet = true
        bodyComponent.body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
        bodyComponent.body.setTransform(bodyComponent.body.position, angle)
        setSize(TILE_SIZE * bodyShape.width, TILE_SIZE * bodyShape.height)
        setOrigin(width / 2,  height / 2)
        rotation = bodyComponent.body.angle * MathUtils.radiansToDegrees
    }
}