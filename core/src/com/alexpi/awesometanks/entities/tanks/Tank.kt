package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.actors.HealthOwner
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.components.body.BodyComponent
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.entities.components.health.HealthComponent
import com.alexpi.awesometanks.entities.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Alex on 02/01/2016.
 */
abstract class Tank(
    gameContext: GameContext,
    position: Vector2,
    fixtureFilter: FixtureFilter,
    bodySize: Float,
    maxHealth: Float,
    isFreezable: Boolean,
    private val rotationSpeed: Float,
    private val movementSpeed: Float,
    tankColor: Color
) : Actor(), HealthOwner {

    private val bodySprite: Sprite =
        Sprite(gameContext.getAssetManager().get("sprites/tank_body.png", Texture::class.java))
    private val wheelsSprite: Sprite =
        Sprite(gameContext.getAssetManager().get("sprites/tank_wheels.png", Texture::class.java))

    val bodyComponent = BodyComponent(
        this,
        gameContext.getWorld(),
        BodyShape.Box(bodySize, bodySize),
        BodyDef.BodyType.DynamicBody,
        fixtureFilter,
        position,
        10f
    )
    private val _healthComponent = HealthComponent(
        gameContext,
        maxHealth,
        true,
        isFreezable,
        onDamageTaken = { healthBarComponent.updateHealth(it) },
        onDeath = { remove() })
    override val healthComponent: HealthComponent get() = _healthComponent

    val healthBarComponent: HealthBarComponent = HealthBarComponent(
        gameContext,
        _healthComponent.maxHealth,
        _healthComponent.currentHealth
    )

    private var currentAngleRotation: Float = 0f
    private var desiredAngleRotation: Float = 0f
        set(value) {
            field = if (value < -MathUtils.PI) value + MathUtils.PI
            else if (value > MathUtils.PI) value - MathUtils.PI
            else value
        }
    val isMoving: Boolean get() = !movementVector.isZero
    var isShooting = false
    private val movementVector: Vector2 = Vector2()

    abstract val currentWeapon: Weapon

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        color.a *= parentAlpha
        batch.draw(wheelsSprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.color = color
        batch.draw(bodySprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        currentWeapon.draw(
            batch, color,
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
        _healthComponent.draw(parent, batch)
    }

    override fun act(delta: Float) {
        super.act(delta)
        healthComponent.update(this, delta)
        healthBarComponent.updatePosition(this)
        if (isMoving) {
            updateAngleRotation()
            bodyComponent.body.setLinearVelocity(
                movementVector.x * movementSpeed * delta,
                movementVector.y * movementSpeed * delta
            )
            bodyComponent.body.setTransform(bodyComponent.body.position, currentAngleRotation)
        } else {
            bodyComponent.body.setLinearVelocity(0f, 0f)
            bodyComponent.body.angularVelocity = 0f
        }
        currentWeapon.updateAngleRotation(rotationSpeed)
        if (isShooting) {
            currentWeapon.shoot(parent, bodyComponent.body.position)
        } else currentWeapon.await()

        setPosition(
            bodyComponent.left * TILE_SIZE,
            bodyComponent.bottom * TILE_SIZE
        )
        rotation = bodyComponent.body.angle * MathUtils.radiansToDegrees
    }

    override fun remove(): Boolean {
        healthBarComponent.hideHealthBar()
        stage.addActor(
            ParticleActor(
                "particles/explosion.party",
                x + width / 2,
                y + height / 2,
                false
            )
        )
        bodyComponent.destroy()
        return super.remove()
    }

    fun setMovementDirection(x: Float, y: Float) {
        desiredAngleRotation = MathUtils.atan2(y, x)
        movementVector.set(Vector2(x, y).nor())
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

        if (diff == 0f) return
        else if (abs(diff) < change) {
            currentAngleRotation = desiredAngleRotation
            return
        }

        currentAngleRotation += min(change, max(-change, diff))
    }

    init {
        color = tankColor
        setSize(bodySize * TILE_SIZE, bodySize * TILE_SIZE)
        setOrigin(width / 2, height / 2)
    }
}