package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.projectiles.Laser
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Timer

/**
 * Created by Alex on 04/01/2016.
 */
class LaserGun(
    ammo: Float,
    power: Int,
    isPlayer: Boolean,
) : Weapon(
    "weapons/laser.png",
    "sounds/laser.ogg",
    ammo,
    power,
    isPlayer,
    .05f,
    .2f
) {
    private val laserRay = Image(GameModule.assetManager.get<Texture>("sprites/laser_ray.png"))
    private var playSound = false
    private var minFraction = 1f

    init {
        laserRay.originY = laserRay.height/2
    }

    override fun shoot(group: Group, position: Vector2) {
        laserRay.rotation = currentRotationAngle * MathUtils.radiansToDegrees
        laserRay.setPosition(position.x * Constants.TILE_SIZE, position.y * Constants.TILE_SIZE - laserRay.height/2)
        if(canShoot()){
            val dX = MathUtils.cos(currentRotationAngle) * MAXIMUM_REACH
            val dY = MathUtils.sin(currentRotationAngle) * MAXIMUM_REACH
            val point2 = Vector2(position.x + dX, position.y + dY)


            if(!playSound){
                shotSound.play()
                playSound = true
            }
            if (!unlimitedAmmo) decreaseAmmo()
            isCoolingDown = true
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (isCoolingDown) isCoolingDown = false
                }
            }, coolingDownTime)
            minFraction = 1f

            createProjectile(group, position)

            GameModule.world.rayCast({ fixture, point, _, fraction ->
                if(fixture.userData is DamageableActor){
                    if(fraction < minFraction){
                        minFraction = fraction
                        val distance = Utils.fastHypot((point.x - position.x).toDouble(), (point.y - position.y).toDouble()) * Constants.TILE_SIZE
                        laserRay.width = distance.toFloat()
                    }
                    fraction
                }
                1f
            }, position, point2)
        }

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
        if(playSound) laserRay.draw(batch,parentAlpha)
        batch.color = color
        super.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
    }

    override fun await() {
        playSound = false
    }

    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(Laser( position, currentRotationAngle, power.toFloat(), isPlayer))
    }

    companion object {
        private const val MAXIMUM_REACH = 10f
    }
}