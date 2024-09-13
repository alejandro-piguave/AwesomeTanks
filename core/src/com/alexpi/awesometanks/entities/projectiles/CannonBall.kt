package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.world.ExplosionManager
import com.badlogic.gdx.math.Vector2

class CannonBall(private val explosionManager: ExplosionManager, pos: Vector2, angle: Float, power: Float, isPlayer: Boolean) : Bullet( pos, angle, 35f, .075f, 80f + power*16f, isPlayer) {

    override fun remove(): Boolean {
        explosionManager.createCanonBallExplosion(body.position.x,  body.position.y)
        return super.remove()
    }
}