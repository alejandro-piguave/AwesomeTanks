package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.weapons.RocketListener
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//Here the attribute speed is rather used for the magnitude of the force applied
class Rocket(
    gameContext: GameContext,
    pos: Vector2,
    angle: Float,
    power: Int,
    filter: Boolean,
    private val rocketListener: RocketListener? = null
) : BaseProjectile(gameContext, pos, BodyShape.Box(.38f, .1f), angle, .075f, 90F + power * 15, filter){

    private val explosionManager = gameContext.getExplosionManager()
    private var isDestroyedFlag = false
    private val flameSprite: Sprite

    private var forceX: Float = 0f
    private var forceY: Float = 0f

    companion object{
        private const val MAX_VELOCITY = 3f
        private const val MAX_VELOCITY2 = MAX_VELOCITY * MAX_VELOCITY
    }

    init {
        bodyComponent.body.setLinearVelocity(0f,0f)
        forceX = speed * cos(angle)
        forceY = speed * sin(angle)
        bodyComponent.body.linearDamping = .5f
        bodyComponent.body.applyForceToCenter(forceX, forceY, true)
        sprite = Sprite(gameContext.getAssetManager().get<Texture>("sprites/rocket.png"))
        flameSprite = Sprite(gameContext.getAssetManager().get<Texture>("sprites/rocket_flame.png"))
    }


    fun updateOrientation(x: Float, y: Float){
        val angle = atan2(y,x)
        forceX = speed * cos(angle)
        forceY = speed * sin(angle)
        bodyComponent.body.setTransform(bodyComponent.body.position, angle)
    }

    override fun remove(): Boolean {
        explosionManager.createCanonBallExplosion(bodyComponent.body.position.x,  bodyComponent.body.position.y)
        return super.remove()
    }

    override fun act(delta: Float) {
        super.act(delta)
        bodyComponent.body.applyForceToCenter(forceX, forceY,true)
        if(bodyComponent.body.linearVelocity.len2() > MAX_VELOCITY2)
            bodyComponent.body.linearVelocity.setLength2(MAX_VELOCITY2)
        if(shouldBeDestroyed && !isDestroyedFlag){
            rocketListener?.onRocketCollided()
            isDestroyedFlag = true
            return
        } else if (!shouldBeDestroyed) rocketListener?.onRocketMoved(bodyComponent.body.position.x, bodyComponent.body.position.y)
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