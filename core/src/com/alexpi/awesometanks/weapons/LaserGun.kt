package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.projectiles.Laser
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Timer

/**
 * Created by Alex on 04/01/2016.
 */
class LaserGun(
    assetManager: AssetManager,
    ammo: Int,
    power: Int,
    isPlayer: Boolean,
    sound: Boolean
) : Weapon(
    "Lasergun",
    assetManager,
    "weapons/laser.png",
    "sounds/laser.ogg",
    ammo,
    power,
    isPlayer,
    sound,
    .05f
) {
    private val laserRay = Image(assetManager.get<Texture>("sprites/laser_ray.png"))
    private var playSound = false
    private var minFraction = 1f

    init {
        laserRay.originY = laserRay.height/2
    }

    override fun shoot(
        assetManager: AssetManager,
        group: Group,
        world: World,
        position: Vector2
    ) {
        if(canShoot()){
            val dX = MathUtils.cos(currentAngleRotation) * MAXIMUM_REACH
            val dY = MathUtils.sin(currentAngleRotation) * MAXIMUM_REACH
            val point2 = Vector2(position.x + dX, position.y + dY)
            laserRay.rotation = currentAngleRotation * MathUtils.radiansToDegrees
            laserRay.setPosition(position.x * Constants.TILE_SIZE, position.y * Constants.TILE_SIZE - laserRay.height/2)


            if(!playSound){
                shotSound.play()
                playSound = true
            }
            //if (!unlimitedAmmo) decreaseAmmo()
            isCoolingDown = true
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (isCoolingDown) isCoolingDown = false
                }
            }, coolingDownTime)
            minFraction = 1f

            createProjectile(group, assetManager, world, position)

            world.rayCast({ fixture, point, _, fraction ->
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
        parentAlpha: Float,
        x: Float,
        y: Float,
        originX: Float,
        originY: Float,
        width: Float,
        height: Float,
        scaleX: Float,
        scaleY: Float) {
        if(playSound) laserRay.draw(batch,parentAlpha)
        super.draw(batch, parentAlpha, x, y, originX, originY, width, height, scaleX, scaleY)
    }

    override fun await() {
        playSound = false
    }

    override fun createProjectile(group: Group, assetManager: AssetManager, world: World, position: Vector2) {
        group.addActor(Laser(assetManager, world, position, currentAngleRotation, power.toFloat(), isPlayer))
    }

    companion object {
        private const val MAXIMUM_REACH = 10f
    }
}