package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import kotlin.experimental.or

/**
 * Created by Alex on 19/01/2016.
 */
abstract class Block private constructor(
    texturePath: String,
    shapeType: Shape.Type,
    health: Float,
    pos: Vector2,
    size: Float,
    isIndestructible: Boolean,
    isFlammable: Boolean, isFreezable: Boolean,
    rumble: Boolean,
) : DamageableActor(health, isFlammable, isFreezable, rumble, isIndestructible) {
    private val sprite: Sprite = Sprite(GameModule.assetManager.get(texturePath, Texture::class.java))
    val body: Body
    val fixture: Fixture
    protected var size = 0f

    //Constructor for breakable blocks
    constructor(
        texturePath: String,
        shapeType: Shape.Type,
        health: Float,
        pos: Vector2,
        size: Float,
        isFlammable: Boolean, isFreezable: Boolean,
        rumble: Boolean = true
    ): this(texturePath, shapeType, health, pos, size, false, isFlammable, isFreezable, rumble)

    //Constructor for unbreakable blocks
    constructor(
    texturePath: String,
    shapeType: Shape.Type,
    pos: Vector2,
    size: Float,
    ): this(texturePath, shapeType, 1f, pos, size,true, false,  false, false)

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawSprite(batch)
        super.draw(batch, parentAlpha)
    }

    protected fun drawSprite(batch: Batch){
        batch.draw(sprite, x, y, width, height)
    }

    override fun onDestroy() {
        super.onDestroy()
        body.world.destroyBody(body)
        remove()
    }

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(pos.x + .5f, pos.y + .5f)
        val shape = when (shapeType) {
            Shape.Type.Circle -> CircleShape().apply { radius = size/2 }
            Shape.Type.Polygon -> PolygonShape().apply { setAsBox(size/2, size/2) }
            else -> throw IllegalArgumentException("Illegal shape")
        }
        fixtureDef.density = 50f
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = Constants.CAT_BLOCK
        fixtureDef.filter.maskBits =
            (Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY_BULLET or Constants.CAT_ITEM or Constants.CAT_ENEMY).toShort()
        body = GameModule.world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        fixture.userData = this
        body.userData = this
        shape.dispose()
        setSize(size * Constants.TILE_SIZE, size * Constants.TILE_SIZE)
        setPosition(
            (body.position.x - size / 2) * Constants.TILE_SIZE,
            (body.position.y - size / 2) * Constants.TILE_SIZE
        )
    }
}