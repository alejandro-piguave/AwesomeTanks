package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Rocket
import com.alexpi.awesometanks.utils.Settings
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

class RocketLauncher(assetManager: AssetManager, ammo: Float, power: Int, isPlayer: Boolean, private val rocketListener: RocketListener? = null) :
    Weapon(
        "Rockets",
        assetManager,
        "weapons/rocket.png",
        "sounds/rocket_launch.ogg",
        ammo,
        power,
        isPlayer,
        1.5f,
        1f
    ) {
    private var rocket: Rocket? = null

    override fun canShoot(): Boolean = super.canShoot() && rocket?.isDestroyed ?: true

    override fun createProjectile(
        group: Group,
        assetManager: AssetManager,
        world: World,
        position: Vector2
    ) {
        rocket = Rocket(assetManager, world, position,currentAngleRotation, power, isPlayer, rocketListener)
        group.addActor(rocket)
    }
}

interface RocketListener {
    fun onRocketMoved(x: Float, y: Float)
    fun onRocketCollided()
}