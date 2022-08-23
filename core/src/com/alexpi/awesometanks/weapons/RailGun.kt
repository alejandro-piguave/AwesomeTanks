package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Rail
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class RailGun(assetManager: AssetManager?, ammo: Int, power: Int, filter: Boolean) :
    Weapon(
        "Railgun",
        assetManager,
        "weapons/railgun.png",
        "sounds/railgun.ogg",
        ammo,
        power,
        filter,
        1f
    ) {

    override fun createProjectile(
        group: Group,
        assetManager: AssetManager,
        world: World,
        position: Vector2
    ) {
        group.addActor(
            Rail(
                assetManager,
                world,
                position,
                currentAngleRotation,
                power.toFloat(),
                isPlayer
            )
        )
    }
}