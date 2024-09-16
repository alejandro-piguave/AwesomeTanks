package com.alexpi.awesometanks.game.weapons

import com.alexpi.awesometanks.game.blocks.Block
import com.alexpi.awesometanks.game.module.Settings.soundsOn
import com.alexpi.awesometanks.game.projectiles.Rail
import com.alexpi.awesometanks.game.tanks.Tank
import com.alexpi.awesometanks.game.utils.fastHypot
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
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
class RailGun(gameContext: GameContext, ammo: Float, power: Int, filter: Boolean, rotationSpeed: Float) :
    Weapon(
        gameContext,
        "weapons/railgun.png",
        "sounds/railgun.ogg",
        ammo,
        power,
        filter,
        1f,
        rotationSpeed,
        1f
    ) {

    private val world: World = gameContext.getWorld()
    private val laserRay = Image(gameContext.getAssetManager().get<Texture>("sprites/railgun_laser.png"))
    private var drawLaser = false
    private var minFraction = 1f

    init {
        laserRay.originY = laserRay.height/2
    }

    override fun shoot(group: Group, position: Vector2) {
        if (canShoot()) {
            minFraction = 1f
            laserRay.rotation = currentRotationAngle * MathUtils.radiansToDegrees
            laserRay.setPosition(position.x * TILE_SIZE, position.y * TILE_SIZE - laserRay.height/2)

            val dX = MathUtils.cos(currentRotationAngle) * MAXIMUM_REACH
            val dY = MathUtils.sin(currentRotationAngle) * MAXIMUM_REACH
            val point2 = Vector2(position.x + dX, position.y + dY)
            world.rayCast({ fixture, point, _, fraction ->
                if(fixture.userData is Block || fixture.userData is Tank){
                    if(fraction < minFraction){
                        minFraction = fraction
                        val distance = fastHypot((point.x - position.x).toDouble(), (point.y - position.y).toDouble()) * TILE_SIZE
                        laserRay.width = distance.toFloat()
                    }
                    fraction
                }
                1f
            }, position, point2)

            createProjectile(group, position)
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

    override fun createProjectile(group: Group, position: Vector2) {
        group.addActor(Rail(gameContext, position, currentRotationAngle, power.toFloat(), isPlayer))
    }

    companion object {
        private const val MAXIMUM_REACH = 10f
    }
}