package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.module.Settings.soundsOn
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Created by Alex on 16/01/2016.
 */
class RicochetBullet(
    gameContext: GameContext,
    pos: Vector2,
    private val hitSound: Sound,
    angle: Float,
    power: Float,
    isPlayer: Boolean
) : BaseProjectile(gameContext, pos, BodyShape.Circular(.1f), angle, 20f, 35 + power * 5, isPlayer) {
    private val particleActor: ParticleActor
    private var hits = 0
    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + width / 2, y + height / 2)
        particleActor.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        particleActor.draw(batch, parentAlpha)
    }

    override fun shouldBeDestroyedAfterCollision(actor: Actor): Boolean {
        if (hits < MAX_HITS) {
            hits++
            return false
        } else {
            return true
        }
    }

    override fun handleCollision(actor: Actor) {
        super.handleCollision(actor)
        if(hits < MAX_HITS && soundsOn) hitSound.play()

    }

    companion object {
        private const val MAX_HITS = 3
    }

    init {
        sprite = Sprite(gameContext.getAssetManager().get("sprites/ricochet_bullet.png", Texture::class.java))
        particleActor = ParticleActor(
            gameContext,
            "particles/ricochets.party",
            x + width / 2,
            y + height / 2,
            true
        )
        bodyComponent.fixture.restitution = .9f
    }
}