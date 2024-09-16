package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

class CannonBall(
    gameContext: GameContext,
    pos: Vector2,
    angle: Float,
    power: Float,
    isPlayer: Boolean
) : Bullet(gameContext, pos, angle, 35f, .075f, 80f + power * 16f, isPlayer) {

    override fun remove(): Boolean {
        gameContext.getExplosionManager().createCanonBallExplosion(body.position.x, body.position.y)
        return super.remove()
    }
}