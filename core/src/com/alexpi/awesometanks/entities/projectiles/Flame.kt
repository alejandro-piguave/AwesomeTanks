package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 16/01/2016.
 */
class Flame(pos: Vector2, angle: Float, var burnDuration: Float, filter: Boolean) : Projectile(
    pos, angle, 15f, .1f, 20f, filter
) {
    private val particleActor: ParticleActor = ParticleActor(
        "particles/flame.party",
        x + bodyWidth / 2,
        y + bodyHeight / 2,
        true
    )

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        particleActor.draw(batch, parentAlpha)
    }

    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + bodyWidth / 2, y + bodyHeight / 2)
        particleActor.act(delta)
    }

}