package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.projectiles.RicochetBullet
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Ricochet(gameContext: GameContext, ammo: Float, power: Int, isPlayer: Boolean) : Weapon(
    gameContext,
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
                gameContext,
                position,
                shotSound,
                currentRotationAngle,
                power.toFloat(),
                isPlayer
            )
        )
    }
}