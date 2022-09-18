package com.alexpi.awesometanks.entities.items

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.world.GameModule.getAssetManager
import com.alexpi.awesometanks.world.GameModule.getWorld
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.experimental.or

/**
 * Created by Alex on 19/02/2016.
 */
abstract class Item(fileName: String, position: Vector2, private val size: Float) : Actor() {
    private val sprite: Sprite
    @JvmField
    protected val body: Body
    protected var fixture: Fixture
    var collected = false
    override fun act(delta: Float) {
        if (collected) {
            destroy()
            return
        }
        setPosition(
            (body.position.x - size / 2) * Constants.TILE_SIZE,
            (body.position.y - size / 2) * Constants.TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    private fun destroy() {
        body.world.destroyBody(body)
        remove()
    }

    fun pickUp() {
        collected = true
    }

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        bodyDef.position.x = position.x
        bodyDef.position.y = position.y
        bodyDef.type = BodyDef.BodyType.DynamicBody
        val shape = CircleShape()
        shape.radius = size / 2
        fixtureDef.density = 2f
        fixtureDef.restitution = .1f
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = Constants.CAT_ITEM
        fixtureDef.filter.maskBits = (Constants.CAT_PLAYER or Constants.CAT_BLOCK or Constants.CAT_ENEMY)
        body = getWorld().createBody(bodyDef)
        body.linearDamping = 1f
        body.angularDamping = .5f
        fixture = body.createFixture(fixtureDef)
        shape.dispose()
        fixture.userData = this
        sprite = Sprite(getAssetManager().get(fileName, Texture::class.java))
        setSize(size * Constants.TILE_SIZE, size * Constants.TILE_SIZE)
        setOrigin(originX + width / 2, originY + height / 2)
        setPosition(
            (body.position.x - size / 2) * Constants.TILE_SIZE,
            (body.position.y - size / 2) * Constants.TILE_SIZE
        )
    }
}