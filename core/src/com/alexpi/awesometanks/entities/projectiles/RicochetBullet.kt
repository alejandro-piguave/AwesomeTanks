package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.world.GameModule
import com.alexpi.awesometanks.world.Settings.soundsOn
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 16/01/2016.
 */
class RicochetBullet(
    pos: Vector2,
    var hitSound: Sound,
    angle: Float,
    power: Float,
    isPlayer: Boolean
) : Projectile(pos, BodyShape.Circular(.2f), angle, 20f, 35 + power * 5, isPlayer) {
    private val particleActor: ParticleActor
    var hits = 0
    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + bodyShape.width / 2, y + bodyShape.height / 2)
        particleActor.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        particleActor.draw(batch, parentAlpha)
    }

    override fun collide() {
        if (hits < MAX_HITS) {
            hits++
            if (soundsOn) hitSound.play()
        } else {
            super.collide()
        }
    }

    companion object {
        private const val MAX_HITS = 3
    }

    init {
        sprite = Sprite(GameModule.assetManager.get("sprites/ricochet_bullet.png", Texture::class.java))
        particleActor = ParticleActor("particles/ricochets.party", x + bodyShape.width / 2, y + bodyShape.height / 2, true)
    }
}