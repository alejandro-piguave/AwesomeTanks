package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.HealthOwner
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by Alex on 16/01/2016.
 */
class Flame(pos: Vector2, angle: Float, private var burnDuration: Float, filter: Boolean) : Projectile(
    pos, BodyShape.Circular(.05f), angle, 15f, 20f, filter
) {
    private val particleActor: ParticleActor = ParticleActor(
        "particles/flame.party",
        x + bodyShape.width / 2,
        y + bodyShape.height / 2,
        true
    )

    override fun handleCollision(actor: Actor) {
        super.handleCollision(actor)
        if(actor is HealthOwner) actor.healthComponent.burn(burnDuration, .35f)
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