package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Rocket
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

class RocketLauncher(ammo: Float, power: Int, isPlayer: Boolean, private val rocketListener: RocketListener? = null) :
    Weapon(
        "weapons/rocket.png",
        "sounds/rocket_launch.ogg",
        ammo,
        power,
        isPlayer,
        1.5f,
        1.5f
    ) {
    var rocket: Rocket? = null
        private set

    override fun canShoot(): Boolean = super.canShoot() && rocket?.hasCollided ?: true

    override fun createProjectile(group: Group, position: Vector2) {
        rocket = Rocket(position,currentRotationAngle, power, isPlayer, rocketListener)
        group.addActor(rocket)
    }
}

interface RocketListener {
    fun onRocketMoved(x: Float, y: Float)
    fun onRocketCollided()
}