package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Rail(
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile( pos, angle, 50f, .25f, 180 + power * 40, filter) {
    var particleActor: ParticleActor = ParticleActor(
        "particles/railgun.party",
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