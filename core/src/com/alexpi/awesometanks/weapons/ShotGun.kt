package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Bullet
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class ShotGun(ammo: Float, power: Int, filter: Boolean) :
    Weapon("Shotgun", "weapons/shotgun.png", "sounds/shotgun.ogg", ammo, power, filter, 1f, 1f) {
    override fun createProjectile(group: Group, position: Vector2) {
        for (i in 0..9) {
            val num = Utils.getRandomFloat(SHOOTING_ANGLE * 2) - SHOOTING_ANGLE
            group.addActor(
                Bullet(
                    position, currentAngleRotation + num, Utils.getRandomInt(10, 30)
                        .toFloat(), .15f, 10f + power, isPlayer
                )
            )
        }
    }

    companion object {
        private const val SHOOTING_ANGLE = .436332f
    }
}