package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.CannonBall
import com.alexpi.awesometanks.world.ExplosionManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Cannon(private val explosionManager: ExplosionManager, ammo: Float, power: Int, filter: Boolean) :
    Weapon("weapons/canon.png", "sounds/canon.ogg", ammo, power, filter, .5f) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(CannonBall(explosionManager, position, currentRotationAngle, power.toFloat(), isPlayer))
    }
}