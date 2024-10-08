package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.utils.getNormalizedAbsoluteDifference
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.TimeUtils

/**
 * Created by Alex on 03/01/2016.
 */
abstract class Weapon(
    val gameContext: GameContext,
    texturePath: String,
    shotSoundPath: String,
    ammo: Float,
    val power: Int,
    val isPlayer: Boolean,
    private val coolDownTime: Float,
    private val rotationSpeed: Float,
    private val ammoConsumption: Float
) {
    var onAmmoUpdated: ((Float) -> Unit)? = null
    var ammo: Float = ammo
        private set(value) {
            field = value
            onAmmoUpdated?.invoke(value)
        }

    private val sprite: TextureRegion = TextureRegion(gameContext.getAssetManager().get<Texture>(texturePath))
    protected val shotSound: Sound = gameContext.getAssetManager().get(shotSoundPath)

    var desiredRotationAngle = 0f
        set(value) {
            field = if(value >= MathUtils.PI2)
                value % MathUtils.PI2
            else if(value < 0){
                value % MathUtils.PI2 + MathUtils.PI2
            } else value
        }

    var currentRotationAngle = 0f
        protected set

    private val isCoolingDown: Boolean
        get() = TimeUtils.millis() - lastShotTime < coolDownTime * 1000

    private var lastShotTime = 0L

    var unlimitedAmmo = false

    var isShooting = false

    private fun decreaseAmmo() {
        if(unlimitedAmmo) return
        if (ammo - ammoConsumption > 0) ammo -= ammoConsumption else ammo = 0f
    }

    private fun hasAmmo(): Boolean =  ammo > 0

    fun hasRotated(): Boolean = currentRotationAngle == desiredRotationAngle

    private fun updateRotationAngle(delta: Float) {
        if(currentRotationAngle == desiredRotationAngle) {
            return
        }

        val difference = getNormalizedAbsoluteDifference(currentRotationAngle, desiredRotationAngle)

        if(difference < ANGLE_THRESHOLD) {
            currentRotationAngle = desiredRotationAngle
            return
        }

        if(currentRotationAngle < MathUtils.PI) {
            if(desiredRotationAngle > currentRotationAngle && desiredRotationAngle < currentRotationAngle + MathUtils.PI) {
                currentRotationAngle += rotationSpeed * delta
            } else {
                currentRotationAngle -= rotationSpeed * delta
            }
        } else {
            if(desiredRotationAngle > currentRotationAngle - MathUtils.PI && desiredRotationAngle < currentRotationAngle) {
                currentRotationAngle -= rotationSpeed * delta
            } else {
                currentRotationAngle += rotationSpeed * delta
            }
        }

        if(currentRotationAngle < 0) currentRotationAngle += MathUtils.PI2
        else if(currentRotationAngle >= MathUtils.PI2) currentRotationAngle -= MathUtils.PI2
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
        batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, currentRotationAngle * MathUtils.radiansToDegrees)
    }

    open fun update(delta: Float, group: Group, position: Vector2) {
        updateRotationAngle(delta)
        if(isShooting && canShoot()) {
            createProjectile(group, position)
            decreaseAmmo()
            lastShotTime = TimeUtils.millis()

        }
    }

    abstract fun createProjectile(group: Group, position: Vector2)

    protected open fun canShoot(): Boolean =  !isCoolingDown && (hasAmmo() || unlimitedAmmo) && hasRotated()

    companion object {
        private const val ANGLE_THRESHOLD = 1/40f
    }

}