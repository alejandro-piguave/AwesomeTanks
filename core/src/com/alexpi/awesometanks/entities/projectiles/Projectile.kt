package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.world.GameModule
import com.alexpi.awesometanks.world.collision.CAT_ENEMY_BULLET
import com.alexpi.awesometanks.world.collision.CAT_PLAYER_BULLET
import com.alexpi.awesometanks.world.collision.ENEMY_BULLET_MASK
import com.alexpi.awesometanks.world.collision.PLAYER_BULLET_MASK
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
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by Alex on 16/01/2016.
 */
abstract class Projectile private constructor(
    position: Vector2,
    shapeType: Shape.Type,
    angle: Float,
    val speed: Float,
    val bodyWidth: Float,
    val bodyHeight: Float,
    val damage: Float,
    isPlayer: Boolean
) : Actor() {
    val body: Body
    private val fixture: Fixture
    @JvmField
    protected var sprite: Sprite? = null
    var hasCollided = false
        private set
    val isEnemy: Boolean
        get() = fixture.filterData.maskBits == ENEMY_BULLET_MASK

    private fun destroy() {
        body.world.destroyBody(body)
        remove()
    }

    open fun collide() {
        hasCollided = true
    }

    override fun act(delta: Float) {
        if (hasCollided) {
            destroy()
            return
        }
        setPosition(
            (body.position.x - bodyWidth / 2) * TILE_SIZE,
            (body.position.y - bodyHeight / 2) * TILE_SIZE
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

    constructor(
        position: Vector2,
        angle: Float,
        speed: Float,
        radius: Float,
        damage: Float,
        isPlayer: Boolean
    ): this(position, Shape.Type.Circle, angle, speed, radius, radius, damage, isPlayer)

    constructor(
        position: Vector2,
        angle: Float,
        speed: Float,
        bodyWidth: Float,
        bodyHeight: Float,
        damage: Float,
        isPlayer: Boolean
    ): this(position, Shape.Type.Polygon, angle, speed, bodyWidth, bodyHeight, damage, isPlayer)

    init {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.x = position.x
        bodyDef.position.y = position.y
        bodyDef.bullet = true
        val fixtureDef = FixtureDef()
        fixtureDef.density = 1f
        val shape = when (shapeType) {
            Shape.Type.Circle -> CircleShape().apply { radius = bodyWidth/2 }
            Shape.Type.Polygon -> PolygonShape().apply { setAsBox(bodyWidth/2, bodyHeight/2) }
            else -> throw IllegalArgumentException("Illegal shape")
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
        setSize(TILE_SIZE * bodyWidth, TILE_SIZE * bodyHeight)
        setOrigin(originX + width / 2, originY + height / 2)
        setPosition(
            (body.position.x - bodyWidth / 2) * TILE_SIZE,
            (body.position.y - bodyHeight / 2) * TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }
}