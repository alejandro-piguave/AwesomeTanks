package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Rocket
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

class RocketLauncher(assetManager: AssetManager, ammo: Float, power: Int, isPlayer: Boolean) :
    Weapon(
        "Rockets",
        assetManager,
        "weapons/rocket.png",
        "sounds/rocket_launch.ogg",
        ammo,
        power,
        isPlayer,
        1f,
        1f
    ) {
    override fun createProjectile(
        group: Group,
        assetManager: AssetManager,
        world: World,
        position: Vector2
    ) {
        group.addActor(Rocket(assetManager, world, position,currentAngleRotation, power, isPlayer))
    }



}