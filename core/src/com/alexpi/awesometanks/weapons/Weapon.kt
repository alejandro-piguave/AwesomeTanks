package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Settings.soundsOn
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.math.Vector2
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.weapons.MiniGun
import com.alexpi.awesometanks.weapons.ShotGun
import com.alexpi.awesometanks.weapons.Ricochet
import com.alexpi.awesometanks.weapons.Flamethrower
import com.alexpi.awesometanks.weapons.Canon
import com.alexpi.awesometanks.weapons.RocketLauncher
import com.alexpi.awesometanks.weapons.LaserGun
import com.alexpi.awesometanks.weapons.RailGun
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Timer
import kotlin.math.atan2

/**
 * Created by Alex on 03/01/2016.
 */
abstract class Weapon(
    val name: String,
    assetManager: AssetManager,
    texturePath: String,
    shotSoundPath: String,
    ammo: Float,
    power: Int,
    isPlayer: Boolean,
    coolingDownTime: Float,
    private val ammoConsumption: Float
) {
    var ammo: Float
        protected set
    @JvmField
    protected var power: Int
    var sprite: Sprite
    @JvmField
    protected var shotSound: Sound
    var desiredAngleRotation = 0f
        set(value) {
            field = value
            if (field < 0) field += (Math.PI * 2).toFloat()
        }
    var currentAngleRotation = 0f
        protected set
    @JvmField
    protected var isPlayer: Boolean
    protected var isCoolingDown = false
    @JvmField
    var unlimitedAmmo = false
    protected val coolingDownTime: Float
    protected fun decreaseAmmo() {
        if (ammo - ammoConsumption > 0) ammo -= ammoConsumption else ammo = 0f
    }

    private fun hasAmmo(): Boolean =  ammo > 0

    fun setDesiredAngleRotation(x: Float, y: Float) {
        desiredAngleRotation = atan2(y.toDouble(), x.toDouble()).toFloat()
        if (desiredAngleRotation < 0) desiredAngleRotation += (Math.PI * 2).toFloat()
    }

    private fun hasRotated(): Boolean = currentAngleRotation == desiredAngleRotation

    fun updateAngleRotation(rotationSpeed: Float) {
        var diff = desiredAngleRotation - currentAngleRotation
        if (diff < 0) diff += (Math.PI * 2).toFloat()
        if (diff >= Math.PI) {
            currentAngleRotation -= rotationSpeed
            diff -= Math.PI.toFloat()
        } else if (diff < Math.PI) currentAngleRotation += rotationSpeed
        if (diff < rotationSpeed) currentAngleRotation = desiredAngleRotation
        if (currentAngleRotation > Math.PI * 2) currentAngleRotation =
            0f else if (currentAngleRotation < 0) currentAngleRotation = (Math.PI * 2).toFloat()
        sprite.rotation = currentAngleRotation * MathUtils.radiansToDegrees
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


    open fun shoot(assetManager: AssetManager, group: Group, world: World, position: Vector2) {
        if (canShoot()) {
            createProjectile(group, assetManager, world, position)
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
    abstract fun createProjectile(
        group: Group,
        assetManager: AssetManager,
        world: World,
        position: Vector2
    )

    protected open fun canShoot(): Boolean = (hasAmmo() || unlimitedAmmo) && !isCoolingDown && hasRotated()

    companion object {
        fun getWeaponAt(
            i: Int,
            assetManager: AssetManager,
            ammo: Float,
            power: Int,
            isPlayer: Boolean,
            rocketListener: RocketListener? = null
        ): Weapon {
            return when (i) {
                Constants.MINIGUN -> MiniGun(assetManager, ammo, power, isPlayer)
                Constants.SHOTGUN -> ShotGun(assetManager, ammo, power, isPlayer)
                Constants.RICOCHET -> Ricochet(assetManager, ammo, power, isPlayer)
                Constants.FLAMETHROWER -> Flamethrower(assetManager, ammo, power, isPlayer)
                Constants.CANON -> Canon(assetManager, ammo, power, isPlayer)
                Constants.ROCKET -> RocketLauncher(assetManager, ammo, power, isPlayer, rocketListener)
                Constants.LASERGUN -> LaserGun(assetManager, ammo, power, isPlayer)
                Constants.RAILGUN -> RailGun(assetManager, ammo, power, isPlayer)
                else -> throw IllegalArgumentException("Invalid index")
            }
        }
    }

    init {
        sprite = Sprite(assetManager.get(texturePath, Texture::class.java))
        shotSound = assetManager.get(shotSoundPath, Sound::class.java)
        this.ammo = ammo
        this.power = power
        this.isPlayer = isPlayer
        this.coolingDownTime = coolingDownTime
    }
}