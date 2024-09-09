package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.listener.DamageListener
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.Timer

/**
 * Created by Alex on 23/02/2016.
 */
abstract class DamageableActor(
                               val maxHealth: Float,
                               private val isFlammable: Boolean,
                               private val isFreezable: Boolean,
                               val rumble: Boolean,
                               private val isImmortal: Boolean = false) :
    Actor() {
    var health: Float = maxHealth
    private set
    protected var isBurning = false
    var isFrozen = false
        private set
    private var lastFreezingTime = 0L
    private var lastHit: Long = System.currentTimeMillis()
    private val flameSprite: ParticleActor = ParticleActor( "particles/flame.party", x, y, true)
    private val frozenSprite: Sprite = Sprite(GameModule.assetManager.get("sprites/frozen.png", Texture::class.java))
    var damageListener: DamageListener? = null
    open fun takeDamage(damage: Float) {
        if(isImmortal) return
        if (health - damage > 0) {
            health -= damage
            if(lastHit + HEALTH_BAR_DURATION < TimeUtils.millis()){
                damageListener?.onDamage(this)
                lastHit = TimeUtils.millis()
            }
        }
        else {
            health = 0f
        }
    }

    open fun killInstantly(){
        health = 0f
    }

    open fun freeze() {
        isFrozen = true
        lastFreezingTime = TimeUtils.millis()
    }

    final override fun act(delta: Float) {
        super.act(delta)
        if (!isAlive) {
            damageListener?.onDeath(this)
            onDestroy()
        } else {
            if (isBurning && isFlammable) {
                flameSprite.setPosition(x + width / 2, y + height / 2)
                flameSprite.act(delta)
                takeDamage(.35f)
            }
            if(isFrozen && lastFreezingTime + FREEZING_TIME_MILLIS < TimeUtils.millis()){
                isFrozen = false
            }
            onAlive(delta)
        }
    }

    open fun onAlive(delta: Float) {}

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawBurning(batch, parentAlpha)
        drawFrozen(batch)
    }

    protected fun drawBurning(batch: Batch, parentAlpha: Float){
        if (isBurning && isFlammable) flameSprite.draw(batch, parentAlpha)
    }

    protected fun drawFrozen(batch: Batch){
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

    open fun onDestroy() {}

    val isAlive: Boolean
        get() = health > 0

    fun heal(healthValue: Int) {
        if (health + healthValue < maxHealth) health += healthValue.toFloat()
        else health = maxHealth
    }

    companion object{
        const val HEALTH_BAR_DURATION = 1500
        private const val FREEZING_TIME_MILLIS = 5000
    }

}