package com.alexpi.awesometanks.weapons

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.projectiles.Rail
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Settings.soundsOn
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
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
class RailGun(assetManager: AssetManager, ammo: Float, power: Int, filter: Boolean) :
    Weapon(
        "Railgun",
        assetManager,
        "weapons/railgun.png",
        "sounds/railgun.ogg",
        ammo,
        power,
        filter,
        1f,
        1f
    ) {

    private val laserRay = Image(assetManager.get<Texture>("sprites/railgun_laser.png"))
    private var drawLaser = false
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
        if (canShoot()) {
            minFraction = 1f
            laserRay.rotation = currentAngleRotation * MathUtils.radiansToDegrees
            laserRay.setPosition(position.x * Constants.TILE_SIZE, position.y * Constants.TILE_SIZE - laserRay.height/2)

            val dX = MathUtils.cos(currentAngleRotation) * MAXIMUM_REACH
            val dY = MathUtils.sin(currentAngleRotation) * MAXIMUM_REACH
            val point2 = Vector2(position.x + dX, position.y + dY)
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

            createProjectile(group, assetManager, world, position)
            if (soundsOn) shotSound.play()
            if (!unlimitedAmmo) decreaseAmmo()
            isCoolingDown = true
            drawLaser = true
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    drawLaser = false
                }
            }, .1f)
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    if (isCoolingDown) isCoolingDown = false
                }
            }, coolingDownTime)
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
        if(drawLaser) laserRay.draw(batch,parentAlpha)
        batch.color = color
        super.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
    }

    override fun await() {}

    override fun createProjectile(group: Group, assetManager: AssetManager, world: World, position: Vector2) {
        group.addActor(Rail( assetManager, world, position, currentAngleRotation, power.toFloat(), isPlayer))
    }

    companion object {
        private const val MAXIMUM_REACH = 10f
    }
}