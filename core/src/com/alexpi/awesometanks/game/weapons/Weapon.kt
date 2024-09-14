package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.module.Settings.soundsOn
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Timer
import kotlin.math.atan2

/**
 * Created by Alex on 03/01/2016.
 */
abstract class Weapon(
    gameContext: GameContext,
    texturePath: String,
    shotSoundPath: String,
    ammo: Float,
    val power: Int,
    val isPlayer: Boolean,
    val coolingDownTime: Float,
    private val ammoConsumption: Float = 1f
) {
    var onAmmoUpdated: ((Float) -> Unit)? = null
    var ammo: Float = ammo
        protected set(value) {
            field = value
            onAmmoUpdated?.invoke(value)
        }
    var sprite: Sprite
    protected var shotSound: Sound

    var desiredRotationAngle = 0f
        set(value) {
            field = value
            if (field < 0) field += (Math.PI * 2).toFloat()
        }

    var currentRotationAngle = 0f
        protected set

    protected var isCoolingDown = false

    var unlimitedAmmo = false

    protected fun decreaseAmmo() {
        if (ammo - ammoConsumption > 0) ammo -= ammoConsumption else ammo = 0f
    }

    private fun hasAmmo(): Boolean =  ammo > 0

    fun setDesiredRotationAngleFrom(x: Float, y: Float) {
        desiredRotationAngle = atan2(y.toDouble(), x.toDouble()).toFloat()
        if (desiredRotationAngle < 0) desiredRotationAngle += (Math.PI * 2).toFloat()
    }

    fun hasRotated(): Boolean = currentRotationAngle == desiredRotationAngle

    fun updateAngleRotation(rotationSpeed: Float) {
        var diff = desiredRotationAngle - currentRotationAngle
        if (diff < 0) diff += (Math.PI * 2).toFloat()
        if (diff >= Math.PI) {
            currentRotationAngle -= rotationSpeed
            diff -= Math.PI.toFloat()
        } else if (diff < Math.PI) currentRotationAngle += rotationSpeed
        if (diff < rotationSpeed) currentRotationAngle = desiredRotationAngle
        if (currentRotationAngle > Math.PI * 2) currentRotationAngle =
            0f else if (currentRotationAngle < 0) currentRotationAngle = (Math.PI * 2).toFloat()
        sprite.rotation = currentRotationAngle * MathUtils.radiansToDegrees
    }

    open fun draw(
        batch: Batch,
        color: Color,
        x: Float,
        parentAlpha: Float,
        originX: Float,
        originY: Float,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float,
        y: Float
    ) {
        batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, sprite.rotation)
    }


    open fun shoot( group: Group, position: Vector2) {
        if (canShoot()) {
            createProjectile(group, position)
            if (soundsOn) shotSound.play()
            if (!unlimitedAmmo) decreaseAmmo()
            isCoolingDown = true
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (isCoolingDown) isCoolingDown = false
                }
            }, coolingDownTime)
        }
    }

    open fun await() {}
    abstract fun createProjectile(group: Group, position: Vector2)

    protected open fun canShoot(): Boolean = (hasAmmo() || unlimitedAmmo) && !isCoolingDown && hasRotated()

    companion object {
        fun getWeaponAt(
            type: Type,
            gameContext: GameContext,
            ammo: Float,
            power: Int,
            isPlayer: Boolean,
            rocketListener: RocketListener? = null
        ): Weapon {
            return when (type) {
                Type.MINIGUN -> MiniGun(gameContext, ammo, power, isPlayer)
                Type.SHOTGUN -> ShotGun(gameContext, ammo, power, isPlayer)
                Type.RICOCHET -> Ricochet(gameContext, ammo, power, isPlayer)
                Type.FLAMETHROWER -> Flamethrower(gameContext, ammo, power, isPlayer)
                Type.CANNON -> Cannon(gameContext, ammo, power, isPlayer)
                Type.ROCKETS -> RocketLauncher(gameContext, ammo, power, isPlayer, rocketListener)
                Type.LASERGUN -> LaserGun(gameContext, ammo, power, isPlayer)
                Type.RAILGUN -> RailGun(gameContext, ammo, power, isPlayer)
            }
        }
    }

    init {
        sprite = Sprite(gameContext.getAssetManager().get(texturePath, Texture::class.java))
        shotSound = gameContext.getAssetManager().get(shotSoundPath, Sound::class.java)
        this.ammo = ammo
    }

    enum class Type{
        MINIGUN,
        SHOTGUN,
        RICOCHET,
        FLAMETHROWER,
        CANNON,
        ROCKETS,
        LASERGUN,
        RAILGUN
    }
}