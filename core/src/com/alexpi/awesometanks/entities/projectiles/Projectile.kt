package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.utils.Constants
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.*
import java.lang.IllegalArgumentException

/**
 * Created by Alex on 16/01/2016.
 */
abstract class Projectile private constructor(
    world: World,
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
    var isDestroyed = false
        private set
    val isEnemy: Boolean
        get() = fixture.filterData.maskBits == Constants.ENEMY_BULLET_MASK

    fun detach() {
        body.destroyFixture(fixture)
        body.world.destroyBody(body)
        remove()
    }

    open fun destroy() {
        isDestroyed = true
    }

    override fun act(delta: Float) {
        if (isDestroyed) {
            detach()
            return
        }
        setPosition(
            (body.position.x - bodyWidth / 2) * Constants.TILE_SIZE,
            (body.position.y - bodyHeight / 2) * Constants.TILE_SIZE
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
        world: World,
        position: Vector2,
        angle: Float,
        speed: Float,
        radius: Float,
        damage: Float,
        isPlayer: Boolean
    ): this(world, position, Shape.Type.Circle, angle, speed, radius, radius, damage, isPlayer)

    constructor(
        world: World,
        position: Vector2,
        angle: Float,
        speed: Float,
        bodyWidth: Float,
        bodyHeight: Float,
        damage: Float,
        isPlayer: Boolean
    ): this(world, position, Shape.Type.Polygon, angle, speed, bodyWidth, bodyHeight, damage, isPlayer)

    init {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(position)
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
            if (isPlayer) Constants.CAT_PLAYER_BULLET else Constants.CAT_ENEMY_BULLET
        fixtureDef.filter.maskBits =
            if (isPlayer) Constants.PLAYER_BULLET_MASK else Constants.ENEMY_BULLET_MASK
        body = world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        fixture.userData = this
        shape.dispose()
        body.isBullet = true
        body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
        body.setTransform(body.position, angle)
        setSize(Constants.TILE_SIZE * bodyWidth, Constants.TILE_SIZE * bodyHeight)
        setOrigin(originX + width / 2, originY + height / 2)
        setPosition(
            (body.position.x - bodyWidth / 2) * Constants.TILE_SIZE,
            (body.position.y - bodyHeight / 2) * Constants.TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }
}