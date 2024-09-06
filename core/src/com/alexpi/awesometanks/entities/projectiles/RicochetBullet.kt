package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.utils.Settings.soundsOn
import com.alexpi.awesometanks.world.GameModule
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
) : Projectile(pos, angle, 20f, .2f, 35 + power * 5, isPlayer) {
    private val particleActor: ParticleActor
    var hits = 0
    override fun act(delta: Float) {
        super.act(delta)
        particleActor.setPosition(x + bodyWidth / 2, y + bodyHeight / 2)
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
        particleActor = ParticleActor("particles/ricochets.party", x + bodyWidth / 2, y + bodyHeight / 2, true)
    }
}