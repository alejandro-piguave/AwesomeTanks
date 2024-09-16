package com.alexpi.awesometanks.game.items

import com.alexpi.awesometanks.game.components.body.CAT_BLOCK
import com.alexpi.awesometanks.game.components.body.CAT_ENEMY
import com.alexpi.awesometanks.game.components.body.CAT_ITEM
import com.alexpi.awesometanks.game.components.body.CAT_PLAYER
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.experimental.or

/**
 * Created by Alex on 19/02/2016.
 */
abstract class Item(gameContext: GameContext, fileName: String, position: Vector2, private val size: Float) : Actor() {
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
            (body.position.x - size / 2) * TILE_SIZE,
            (body.position.y - size / 2) * TILE_SIZE
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
        fixtureDef.filter.categoryBits = CAT_ITEM
        fixtureDef.filter.maskBits = (CAT_PLAYER or CAT_BLOCK or CAT_ENEMY)
        body = gameContext.getWorld().createBody(bodyDef)
        body.linearDamping = 1f
        body.angularDamping = .5f
        fixture = body.createFixture(fixtureDef)
        shape.dispose()
        fixture.userData = this
        sprite = Sprite(gameContext.getAssetManager().get(fileName, Texture::class.java))
        setSize(size * TILE_SIZE, size * TILE_SIZE)
        setOrigin(originX + width / 2, originY + height / 2)
        setPosition(
            (body.position.x - size / 2) * TILE_SIZE,
            (body.position.y - size / 2) * TILE_SIZE
        )
    }
}