package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.CanonBall
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Canon(ammo: Float, power: Int, filter: Boolean) :
    Weapon("Canon", "weapons/canon.png", "sounds/canon.ogg", ammo, power, filter, .5f, 1f) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(CanonBall(position, currentAngleRotation, power.toFloat(), isPlayer))
    }
}