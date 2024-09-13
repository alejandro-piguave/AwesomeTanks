package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.CAT_ENEMY_BULLET
import com.alexpi.awesometanks.entities.components.body.CAT_PLAYER_BULLET
import com.alexpi.awesometanks.entities.components.body.ENEMY_BULLET_MASK
import com.alexpi.awesometanks.entities.components.body.PLAYER_BULLET_MASK
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by Alex on 16/01/2016.
 */
abstract class Projectile(
    position: Vector2,
    val bodyShape: BodyShape,
    angle: Float,
    val speed: Float,
    protected val damage: Float,
    val isPlayer: Boolean
) : Actor() {
    val body: Body
    private val fixture: Fixture
    @JvmField
    protected var sprite: Sprite? = null
    var hasCollided = false
    protected set
    open fun destroy() {
        body.world.destroyBody(body)
        stage.addActor(ParticleActor("particles/collision.party", x + bodyShape.width/2, y + bodyShape.height/2, false))
        remove()
    }

    open fun collide(actor: Actor) {
        if(actor is DamageableActor) actor.takeDamage(damage)
        hasCollided = true
    }

    override fun act(delta: Float) {
        if (hasCollided) {
            destroy()
            return
        }
        setPosition(
            (body.position.x - bodyShape.width / 2) * TILE_SIZE,
            (body.position.y - bodyShape.height / 2) * TILE_SIZE
        )

        rotation = body.angle * MathUtils.radiansToDegrees
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
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.x = position.x
        bodyDef.position.y = position.y
        bodyDef.bullet = true
        val fixtureDef = FixtureDef()
        fixtureDef.density = 1f
        val shape = when (bodyShape) {
            is BodyShape.Circular -> CircleShape().apply { radius = bodyShape.radius }
            is BodyShape.Box -> PolygonShape().apply { setAsBox(bodyShape.width/2, bodyShape.height/2) }
        }
        fixtureDef.shape = shape
        fixtureDef.restitution = .9f
        fixtureDef.filter.categoryBits =
            if (isPlayer) CAT_PLAYER_BULLET else CAT_ENEMY_BULLET
        fixtureDef.filter.maskBits =
            if (isPlayer) PLAYER_BULLET_MASK else ENEMY_BULLET_MASK
        body = GameModule.world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        shape.dispose()
        fixture.userData = this
        body.isBullet = true
        body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
        body.setTransform(body.position, angle)
        setSize(TILE_SIZE * bodyShape.width, TILE_SIZE * bodyShape.height)
        setOrigin(originX + width / 2, originY + height / 2)
        setPosition(
            (body.position.x - bodyShape.width / 2) * TILE_SIZE,
            (body.position.y - bodyShape.height / 2) * TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }
}