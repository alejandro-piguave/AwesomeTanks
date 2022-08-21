package com.alexpi.awesometanks.entities

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Timer

/**
 * Created by Alex on 23/02/2016.
 */
abstract class DamageableActor(private val manager: AssetManager,
                               val maxHealth: Float,
                               private val isFlammable: Boolean,
                               private val isFreezable: Boolean,
                               private val damageListener: DamageListener? = null) :
    Actor() {
    var health: Float = maxHealth
    private set
    protected var isBurning = false
    protected var isFrozen = false
    private var lastHit: Long = System.currentTimeMillis()
    var flameSprite: ParticleActor = ParticleActor(manager, "particles/flame.party", x, y, true)
    private val frozenSprite: Sprite = Sprite(manager.get("sprites/frozen.png", Texture::class.java))

    open fun takeDamage(damage: Float) {
        if (health - damage > 0) {
            health -= damage
            if(lastHit + HEALTH_BAR_DURATION < System.currentTimeMillis()){
                damageListener?.onDamage(this)
                lastHit = System.currentTimeMillis()
            }
        }
        else health = 0f
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isAlive) {
            detach()
            return
        }
        if (isBurning && isFlammable) {
            flameSprite.setPosition(x + width / 2, y + height / 2)
            flameSprite.act(delta)
            takeDamage(.25f)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (isBurning && isFlammable) flameSprite.draw(batch, parentAlpha)
        if (isFrozen && isFreezable) batch.draw(frozenSprite, x, y, width, height)
    }

    fun burn(duration: Float) {
        isBurning = true
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                if (isBurning) isBurning = false
            }
        }, duration)
    }

    open fun detach() {
        stage.addActor(
            ParticleActor(
                manager,
                "particles/explosion.party",
                x + width / 2,
                y + height / 2,
                false
            )
        )
    }

    val isAlive: Boolean
        get() = health > 0

    fun heal(healthValue: Int) {
        if (health + healthValue < maxHealth) health += healthValue.toFloat()
        else health = maxHealth
    }

    companion object{
        private const val HEALTH_BAR_DURATION = 1500
        const val HEALTH_BAR_DURATION_SECONDS = 1.5f

    }

}