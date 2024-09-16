package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.projectiles.Rocket
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

class RocketLauncher(gameContext: GameContext, ammo: Float, power: Int, isPlayer: Boolean, rotationSpeed: Float, private val rocketListener: RocketListener? = null) :
    Weapon(
        gameContext,
        "weapons/rocket.png",
        "sounds/rocket_launch.ogg",
        ammo,
        power,
        isPlayer,
        1.5f,
        rotationSpeed,
        1.5f
    ) {
    var rocket: Rocket? = null
        private set

    override fun canShoot(): Boolean = super.canShoot() && rocket?.shouldBeDestroyed ?: true

    override fun createProjectile(group: Group, position: Vector2) {
        shotSound.play()
        rocket = Rocket(gameContext, position,currentRotationAngle, power, isPlayer, rocketListener)
        group.addActor(rocket)
    }
}

interface RocketListener {
    fun onRocketMoved(x: Float, y: Float)
    fun onRocketCollided()
}