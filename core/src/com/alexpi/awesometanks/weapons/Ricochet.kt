package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.RicochetBullet
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Ricochet(ammo: Float, power: Int, isPlayer: Boolean) : Weapon(
    "Ricochet",
    "weapons/ricochet.png",
    "sounds/ricochet.ogg",
    ammo,
    power,
    isPlayer,
    .5f,
    .9f
) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(
            RicochetBullet(
                position,
                shotSound,
                currentAngleRotation,
                power.toFloat(),
                isPlayer
            )
        )
    }
}