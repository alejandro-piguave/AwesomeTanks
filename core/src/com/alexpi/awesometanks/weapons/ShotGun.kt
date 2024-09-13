package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.projectiles.Bullet
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.utils.RandomUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class ShotGun(gameContext: GameContext, ammo: Float, power: Int, filter: Boolean) :
    Weapon(gameContext,"weapons/shotgun.png", "sounds/shotgun.ogg", ammo, power, filter, 1f) {
    override fun createProjectile(group: Group, position: Vector2) {
        repeat(10){
            val delta = RandomUtils.getRandomFloat(SHOOTING_ANGLE * 2) - SHOOTING_ANGLE
            group.addActor(
                Bullet(
                    position, currentRotationAngle + delta, RandomUtils.getRandomInt(10, 30)
                        .toFloat(), .075f, 10f + power, isPlayer
                )
            )
        }
    }

    companion object {
        private const val SHOOTING_ANGLE = .436332f
    }
}