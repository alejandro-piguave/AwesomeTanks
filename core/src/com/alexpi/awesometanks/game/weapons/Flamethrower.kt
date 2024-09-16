package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.projectiles.Flame
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Flamethrower(gameContext: GameContext, ammo: Float, power: Int, filter: Boolean, rotationSpeed: Float) : Weapon(
    gameContext,
    "weapons/flamethrower.png",
    "sounds/flamethrower.ogg",
    ammo,
    power,
    filter,
    .4f,
    rotationSpeed,
    .7f,
) {
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(Flame(gameContext, position, currentRotationAngle, 2f + power, isPlayer))
    }
}