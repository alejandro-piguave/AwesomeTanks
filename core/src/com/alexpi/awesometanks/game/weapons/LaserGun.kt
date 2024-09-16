package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.projectiles.Laser
import com.alexpi.awesometanks.game.utils.fastHypot
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 04/01/2016.
 */
class LaserGun(
    gameContext: GameContext,
    ammo: Float,
    power: Int,
    isPlayer: Boolean,
    rotationSpeed: Float
) : Weapon(
    gameContext,
    "weapons/laser.png",
    "sounds/laser.ogg",
    ammo,
    power,
    isPlayer,
    .05f,
    rotationSpeed,
    .2f
) {
    private val laserRay = Sprite(gameContext.getAssetManager().get<Texture>("sprites/laser_ray.png"))

    private var previousIsShooting = false
    private var isFirstProjectile = true

    init {
        laserRay.setOrigin(0f, laserRay.height / 2)
    }

    override fun draw(
        batch: Batch,
        color: Color,
        x: Float,
        parentAlpha: Float,
        originX: Float,
        originY: Float,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float,
        y: Float
    ) {
        batch.color = Color(1f,1f,1f,parentAlpha)
        if(isShooting) laserRay.draw(batch,parentAlpha)
        batch.color = color
        super.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
    }

    override fun update(delta: Float, group: Group, position: Vector2) {
        super.update(delta, group, position)
        laserRay.setPosition(position.x * TILE_SIZE, position.y * TILE_SIZE - laserRay.height/2)

        if(!previousIsShooting && isShooting) {
            isFirstProjectile = true
        }

        previousIsShooting = isShooting
    }

    override fun createProjectile(group: Group, position: Vector2) {
        if (isFirstProjectile) {
            shotSound.play()
            isFirstProjectile = false
        }
        group.addActor(Laser(gameContext, position, currentRotationAngle, power.toFloat(), isPlayer) { collisionPoint ->
            val distance = fastHypot((collisionPoint.x - position.x).toDouble(), (collisionPoint.y - position.y).toDouble()) * TILE_SIZE
            laserRay.setSize(distance.toFloat(), laserRay.height)

            val angle = MathUtils.atan2(collisionPoint.y - position.y, collisionPoint.x - position.x)
            laserRay.rotation = angle * MathUtils.radiansToDegrees
        })


    }
}