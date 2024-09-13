package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.world.ExplosionManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Rail(
    private val explosionManager: ExplosionManager,
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile(pos, BodyShape.Circular(.25f), angle, 50f, 180 + power * 40, filter) {
    private val particleActor: ParticleActor = ParticleActor(
        "particles/railgun.party",
        x + bodyShape.width / 2,
        y + bodyShape.height / 2,
        true
    )

    override fun destroy() {
        explosionManager.createCanonBallExplosion(x + bodyShape.width / 2, y + bodyShape.height / 2)
        super.destroy()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        particleActor.draw(batch, parentAlpha)
    }

    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + bodyShape.width / 2, y + bodyShape.height / 2)
        particleActor.act(delta)
    }

}