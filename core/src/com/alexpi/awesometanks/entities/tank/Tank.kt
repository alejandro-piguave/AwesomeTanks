package com.alexpi.awesometanks.entities.tank

import com.alexpi.awesometanks.entities.DamageableActor
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Alex on 02/01/2016.
 */
abstract class Tank(
    protected val manager: AssetManager,
    protected val entityGroup: Group,
    world: World,
    position: Vector2,
    private val bodySize: Float,
    private val rotationSpeed: Float,
    private val movementSpeed: Float,
    categoryBits: Short,
    maskBits: Short,
    maxHealth: Float,
    isFreezable: Boolean,
    protected val allowSounds: Boolean,
    ) : DamageableActor(manager, maxHealth, true, isFreezable){

    private val bodySprite: Sprite = Sprite(manager.get("sprites/tank_body.png", Texture::class.java))
    private val wheelsSprite: Sprite = Sprite(manager.get("sprites/tank_wheels.png", Texture::class.java))

    val body: Body
    private val fixture: Fixture
    var currentAngleRotation: Float = 0f
    private var desiredAngleRotation: Float = 0f
    set(value) {
        field = if (value < -MathUtils.PI) value + MathUtils.PI
        else if(value > MathUtils.PI) value - MathUtils.PI
        else value
    }
    var isMoving: Boolean = false
    var isShooting = false
    private val movement: Vector2 = Vector2()
    override fun draw(batch: Batch, parentAlpha: Float) {
        color.a *= parentAlpha
        batch.draw(wheelsSprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.color = color
        batch.draw(bodySprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.draw(
            getCurrentWeapon().sprite,
            x,
            y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            getCurrentWeapon().sprite.rotation
        )
        batch.setColor(1f, 1f, 1f, parentAlpha)
        super.draw(batch, parentAlpha)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (isMoving) {
            updateAngleRotation()
            body.setLinearVelocity(movement.x * delta, movement.y * delta)
            body.setTransform(body.position, currentAngleRotation)
        } else {
            body.setLinearVelocity(0f, 0f)
            body.angularVelocity = 0f
        }
        getCurrentWeapon().updateAngleRotation(rotationSpeed)
        if (isShooting && getCurrentWeapon().hasRotated() && isAlive) {
            getCurrentWeapon().shoot(
                manager,
                entityGroup,
                body.world,
                body.position
            )
        }
        setPosition(
            (body.position.x - bodySize*.5f) * Constants.TILE_SIZE,
            (body.position.y - bodySize*.5f) * Constants.TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }

    //x and y values must be normalized
    fun setOrientation(x: Float, y: Float) {
        desiredAngleRotation = MathUtils.atan2(y, x)
        movement.set(x * movementSpeed, y * movementSpeed)
    }

    //In radians
    fun setOrientation(rotationAngle: Float) {
        desiredAngleRotation = rotationAngle
        movement.set(MathUtils.cos(rotationAngle) * movementSpeed, MathUtils.sin(rotationAngle) * movementSpeed)
    }

    private fun updateAngleRotation() {
        val change = .1f
        var diff = desiredAngleRotation - currentAngleRotation
        if (diff < -MathUtils.PI) diff += MathUtils.PI * 2
        else if (diff > MathUtils.PI) diff -= MathUtils.PI * 2

        if(diff == 0f) return
        else if (abs(diff) < change) {
            currentAngleRotation = desiredAngleRotation
            return
        }

        currentAngleRotation += min(change, max(-change, diff))
    }

    override fun detach() {
        body.destroyFixture(fixture)
        body.world.destroyBody(body)
        remove()
    }

    abstract fun getCurrentWeapon(): Weapon

    val posY: Float
        get() = body.position.y
    val posX: Float
        get() = body.position.x

    init {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(position.x + .5f, position.y + .5f)
        val shape = PolygonShape()
        shape.setAsBox(bodySize / 2, bodySize / 2)
        val fixtureDef = FixtureDef()
        fixtureDef.density = 10f
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = categoryBits
        fixtureDef.filter.maskBits = maskBits
        body = world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        fixture.userData = this
        body.userData = this
        shape.dispose()
        setSize(bodySize * Constants.TILE_SIZE, bodySize * Constants.TILE_SIZE)
        setOrigin(width / 2, height / 2)
        setPosition((body.position.x - bodySize / 2) * width, (body.position.y - bodySize / 2) * height)
    }
}