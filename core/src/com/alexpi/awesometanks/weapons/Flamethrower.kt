package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Flame
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Flamethrower( ammo: Float, power: Int, filter: Boolean) : Weapon(
    "Flamethrower",
    "weapons/flamethrower.png",
    "sounds/flamethrower.ogg",
    ammo,
    power,
    filter,
    .4f,
    .75f
) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(Flame(position, currentAngleRotation, 2f + power, isPlayer))
    }
}