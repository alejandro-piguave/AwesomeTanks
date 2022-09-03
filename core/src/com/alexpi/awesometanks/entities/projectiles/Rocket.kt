package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.weapons.RocketListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.atan
import kotlin.math.atan2

class Rocket(
    manager: AssetManager,
    world: World,
    pos: Vector2,
    angle: Float,
    power: Int,
    filter: Boolean,
    private val rocketListener: RocketListener? = null
) : Projectile(world, pos, angle, 4f, .38f, .1f, 90F + power * 15, filter){

    private var isDestroyedFlag = false
    private val flameSprite: Sprite

    init {
        sprite = Sprite(manager.get<Texture>("sprites/rocket.png"))
        flameSprite = Sprite(manager.get<Texture>("sprites/rocket_flame.png"))
    }

    fun updateOrientation(x: Float, y: Float){
        val angle = atan2(y,x)
        body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
        body.setTransform(body.position, angle)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if(isDestroyed && !isDestroyedFlag){
            rocketListener?.onRocketCollided()
            isDestroyedFlag = true
            return
        } else if (!isDestroyed) rocketListener?.onRocketMoved(body.position.x, body.position.y)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(flameSprite, x, y, originX, originY, bodyWidth, bodyHeight, scaleX, scaleY, rotation)
        super.draw(batch, parentAlpha)
    }
}