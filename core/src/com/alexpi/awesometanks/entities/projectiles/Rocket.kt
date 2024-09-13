package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.weapons.RocketListener
import com.alexpi.awesometanks.world.ExplosionManager
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//Here the attribute speed is rather used for the magnitude of the force applied
class Rocket(
    private val explosionManager: ExplosionManager,
    pos: Vector2,
    angle: Float,
    power: Int,
    filter: Boolean,
    private val rocketListener: RocketListener? = null
) : Projectile(pos, BodyShape.Box(.38f, 1f), angle, .075f, 90F + power * 15, filter){

    private var isDestroyedFlag = false
    private val flameSprite: Sprite

    private var forceX: Float = 0f
    private var forceY: Float = 0f

    companion object{
        private const val MAX_VELOCITY = 3f
        private const val MAX_VELOCITY2 = MAX_VELOCITY * MAX_VELOCITY
    }

    init {
        body.setLinearVelocity(0f,0f)
        forceX = speed * cos(angle)
        forceY = speed * sin(angle)
        body.linearDamping = .5f
        body.applyForceToCenter(forceX, forceY, true)
        sprite = Sprite(GameModule.assetManager.get<Texture>("sprites/rocket.png"))
        flameSprite = Sprite(GameModule.assetManager.get<Texture>("sprites/rocket_flame.png"))
    }


    fun updateOrientation(x: Float, y: Float){
        val angle = atan2(y,x)
        forceX = speed * cos(angle)
        forceY = speed * sin(angle)
        body.setTransform(body.position, angle)
    }

    override fun destroy() {
        explosionManager.createCanonBallExplosion(x + bodyShape.width / 2, y + bodyShape.height / 2)
        super.destroy()
    }

    override fun act(delta: Float) {
        super.act(delta)
        body.applyForceToCenter(forceX, forceY,true)
        if(body.linearVelocity.len2() > MAX_VELOCITY2)
            body.linearVelocity.setLength2(MAX_VELOCITY2)
        if(hasCollided && !isDestroyedFlag){
            rocketListener?.onRocketCollided()
            isDestroyedFlag = true
            return
        } else if (!hasCollided) rocketListener?.onRocketMoved(body.position.x, body.position.y)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.draw(
            flameSprite,
            x,
            y,
            originX,
            originY,
            width,
            height,
            scaleX,
            scaleY,
            rotation
        )
    }
}