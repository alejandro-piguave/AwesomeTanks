package com.alexpi.awesometanks.entities.tank

import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Timer
import kotlin.experimental.or
import kotlin.math.atan2

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    manager: AssetManager,
    entityGroup: Group,
    world: World,
    position: Vector2,
    private val targetPosition: Vector2,
    size: Float,
    type: Int) : Tank(manager, entityGroup, world, position, size,
    ROTATION_SPEED, MOVEMENT_SPEED,
    Constants.CAT_ENEMY,
    Constants.CAT_BLOCK or Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY,
    100f,true, Gdx.app.getPreferences("settings").getBoolean("areSoundsActivated")) {

    private val weapon: Weapon

    override fun act(delta: Float) {
        val dX = targetPosition.x - body.position.x
        val dY = targetPosition.y - body.position.y
        val distanceFromTarget = Utils.fastHypot(dX.toDouble(), dY.toDouble()).toFloat()
        if (distanceFromTarget < 7 && !isFrozen && isAlive) {
            val angle = atan2(dY, dX)
            setOrientation(angle)
            weapon.setDesiredAngleRotation(dX, dY)
            isMoving = true
            isShooting = true
        } else {
            isShooting = false
            isMoving = false
        }
        super.act(delta)
    }

    override fun detach() {
        super.detach()
        dropLoot()
    }

    override fun getCurrentWeapon(): Weapon = weapon

    fun freeze(freezingTime: Float) {
        isFrozen = true
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                isFrozen = false
            }
        }, freezingTime)
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(5, 10)
        for (i in 0 until num1) entityGroup.addActor(
            GoldNugget(
                manager,
                body.world,
                body.position
            )
        )
    }

    companion object {
        private const val ROTATION_SPEED = .035f
        private const val MOVEMENT_SPEED = 60f
    }

    init {
        weapon = Weapon.getWeaponAt(type, manager, 1, 2, false, allowSounds)
        weapon.setUnlimitedAmmo(true)

    }
}