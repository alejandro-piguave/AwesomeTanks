package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Rail(
    gameContext: GameContext,
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile(gameContext, pos, BodyShape.Circular(.125f), angle, 50f, 180 + power * 40, filter) {
    private val particleActor: ParticleActor =
        ParticleActor(
            gameContext,
            "particles/railgun.party",
            x + width / 2,
            y + height / 2,
            true
        )

    private val explosionManager = gameContext.getExplosionManager()


    override fun remove(): Boolean {
        explosionManager.createCanonBallExplosion(bodyComponent.body.position.x,  bodyComponent.body.position.y)
        return super.remove()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        particleActor.draw(batch, parentAlpha)
    }

    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + width/2, y + height/2)
        particleActor.act(delta)
    }

}