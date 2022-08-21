package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.DamageableActor
import com.alexpi.awesometanks.utils.Constants
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.*
import kotlin.experimental.or

/**
 * Created by Alex on 19/01/2016.
 */
abstract class Block(
    manager: AssetManager,
    texturePath: String,
    world: World,
    shape: Shape,
    health: Int,
    protected var posX: Int,
    protected var posY: Int,
    size: Float,
    isFlammable: Boolean, damageListener: DamageListener? = null
) : DamageableActor(
    manager, health.toFloat(), isFlammable, false, damageListener
) {
    private val sprite: Sprite = Sprite(manager.get(texturePath, Texture::class.java))
    @JvmField
    var body: Body
    @JvmField
    var fixture: Fixture
    protected var size = 0f
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y, width, height)
        super.draw(batch, parentAlpha)
    }

    override fun detach() {
        super.detach()
        body.destroyFixture(fixture)
        body.world.destroyBody(body)
        remove()
    }

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position[posX + .5f] = posY + .5f
        if (shape.type == Shape.Type.Polygon) (shape as PolygonShape).setAsBox(
            size / 2,
            size / 2
        ) else if (shape.type == Shape.Type.Circle) shape.radius = size / 2
        fixtureDef.density = 50f
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = Constants.CAT_BLOCK
        fixtureDef.filter.maskBits =
            (Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY_BULLET or Constants.CAT_ITEM or Constants.CAT_ENEMY).toShort()
        body = world.createBody(bodyDef)
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