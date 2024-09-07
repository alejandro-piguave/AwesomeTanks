package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Alex on 02/01/2016.
 */
abstract class Tank(
    position: Vector2,
    private val bodySize: Float,
    private val rotationSpeed: Float,
    private val movementSpeed: Float,
    categoryBits: Short,
    maskBits: Short,
    maxHealth: Float,
    isFreezable: Boolean,
    tankColor: Color
    ) : DamageableActor( maxHealth, true, isFreezable, true){

    private val bodySprite: Sprite = Sprite(GameModule.assetManager.get("sprites/tank_body.png", Texture::class.java))
    private val wheelsSprite: Sprite = Sprite(GameModule.assetManager.get("sprites/tank_wheels.png", Texture::class.java))

    val body: Body
    private val fixture: Fixture
    private var currentAngleRotation: Float = 0f
    private var desiredAngleRotation: Float = 0f
    set(value) {
        field = if (value < -MathUtils.PI) value + MathUtils.PI
        else if(value > MathUtils.PI) value - MathUtils.PI
        else value
    }
    val isMoving: Boolean get() = !movementVector.isZero
    var isShooting = false
    private val movementVector: Vector2 = Vector2()

    abstract val currentWeapon: Weapon

    override fun draw(batch: Batch, parentAlpha: Float) {
        color.a *= parentAlpha
        batch.draw(wheelsSprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.color = color
        batch.draw(bodySprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        currentWeapon.draw(
            batch,color,
            x,
            parentAlpha,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            y,
        )
        batch.setColor(1f, 1f, 1f, parentAlpha)
        super.draw(batch, parentAlpha)
    }

    override fun onAlive(delta: Float) {
        if (isMoving) {
            updateAngleRotation()
            body.setLinearVelocity(movementVector.x * movementSpeed * delta, movementVector.y * movementSpeed * delta)
            body.setTransform(body.position, currentAngleRotation)
        } else {
            body.setLinearVelocity(0f, 0f)
            body.angularVelocity = 0f
        }
        currentWeapon.updateAngleRotation(rotationSpeed)
        if (isShooting && isAlive) {
            currentWeapon.shoot(parent, body.position)
        } else currentWeapon.await()
        setPosition(
            (body.position.x - bodySize*.5f) * TILE_SIZE,
            (body.position.y - bodySize*.5f) * TILE_SIZE
        )
        rotation = body.angle * MathUtils.radiansToDegrees
    }

    override fun onDestroy() {
        body.world.destroyBody(body)
        remove()
    }

    fun setMovementDirection(x: Float, y: Float) {
        desiredAngleRotation = MathUtils.atan2(y, x)
        movementVector.set(Vector2(x,y).nor())
    }

    fun stopMovement() {
        movementVector.set(0f, 0f)
    }

    //In radians
    fun setOrientation(rotationAngle: Float) {
        desiredAngleRotation = rotationAngle
        movementVector.set(MathUtils.cos(rotationAngle), MathUtils.sin(rotationAngle))
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

    init {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        //Position from enemies generated from spawners are given from the center of the block (+.5f)
        // whereas the player position is passed with no decimal part,
        // therefore we make sure to round the number to place the tank in the center in both cases
        bodyDef.position.x = position.x.toInt() + .5f
        bodyDef.position.y = position.y.toInt() + .5f
        val shape = PolygonShape()
        shape.setAsBox(bodySize / 2, bodySize / 2)
        val fixtureDef = FixtureDef()
        fixtureDef.density = 10f
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = categoryBits
        fixtureDef.filter.maskBits = maskBits
        body = GameModule.world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        fixture.userData = this
        body.userData = this
        shape.dispose()
        color = tankColor
        setSize(bodySize * TILE_SIZE, bodySize * TILE_SIZE)
        setOrigin(width / 2, height / 2)
        setPosition((body.position.x - bodySize / 2) * TILE_SIZE, (body.position.y - bodySize / 2) * TILE_SIZE)
    }
}