package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.projectiles.CannonBall
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class Cannon(gameContext: GameContext, ammo: Float, power: Int, filter: Boolean) :
    Weapon(gameContext,"weapons/canon.png", "sounds/canon.ogg", ammo, power, filter, .5f) {
        private val explosionManager = gameContext.getExplosionManager()
    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(CannonBall(explosionManager, position, currentRotationAngle, power.toFloat(), isPlayer))
    }
}