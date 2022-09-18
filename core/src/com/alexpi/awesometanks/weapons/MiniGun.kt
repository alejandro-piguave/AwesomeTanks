package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Bullet
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 03/01/2016.
 */
class MiniGun(ammo: Float, power: Int, isPlayer: Boolean) : Weapon(
    "weapons/minigun.png",
    "sounds/minigun.ogg",
    ammo,
    power,
    isPlayer,
    .06f
) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(Bullet(position, currentRotationAngle, 30f, .1f, 3.5f + power, isPlayer))
    }

    init {
        unlimitedAmmo = true
    }
}