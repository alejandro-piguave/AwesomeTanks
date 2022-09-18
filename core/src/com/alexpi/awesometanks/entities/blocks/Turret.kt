package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.ai.TurretAI
import com.alexpi.awesometanks.entities.ai.TurretAICallback
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 18/02/2016.
 */
class Turret(
    pos: Vector2,
    type: Weapon.Type
) : Block( "sprites/turret_base.png", Shape.Type.Polygon, getHealthByType(type), pos, .8f, true, true), TurretAICallback {
    private val weapon: Weapon
    private val enemyAI = TurretAI(body.position, this)
    private val nuggetValue: Int
    override fun draw(batch: Batch, parentAlpha: Float) {
        drawSprite(batch)
        weapon.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
        drawBurning(batch, parentAlpha)
        drawFrozen(batch)
    }

    override fun act(delta: Float) {
        if (isAlive && !isFrozen){
            enemyAI.update()
        } else{
            await()
        }
        super.act(delta)
    }

    companion object {
        private const val ROTATION_SPEED = .035f

        fun getHealthByType(type: Weapon.Type): Float{
            val typeMultiplier: Float = when(type){
                Weapon.Type.MINIGUN -> 0f
                Weapon.Type.SHOTGUN -> .2f
                Weapon.Type.RICOCHET -> .3f
                Weapon.Type.FLAMETHROWER -> .6f
                Weapon.Type.ROCKETS -> .6f
                Weapon.Type.CANNON -> .6f
                Weapon.Type.LASERGUN -> 1f
                Weapon.Type.RAILGUN -> 1f

            }
            return 200f + typeMultiplier * 500f
        }

        private fun getNuggetValue(type: Weapon.Type): Int{
            val typeMultiplier: Float = when(type){
                Weapon.Type.MINIGUN -> 0f
                Weapon.Type.SHOTGUN -> .2f
                Weapon.Type.RICOCHET -> .3f
                Weapon.Type.FLAMETHROWER -> .6f
                Weapon.Type.CANNON -> .6f
                Weapon.Type.ROCKETS -> .6f
                Weapon.Type.LASERGUN -> 1f
                else -> 1f

            }
            return 60 + (typeMultiplier*75).toInt()
        }
    }

    override fun detach() {
        dropLoot()
        super.detach()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(10, 15)
        for (i in 0 until num1)
            parent.addActor(GoldNugget(body.position, Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5)))
    }

    init {
        val filter = Filter()
        filter.categoryBits = Constants.CAT_ENEMY
        fixture.filterData = filter
        nuggetValue = getNuggetValue(type)
        weapon = Weapon.getWeaponAt(type, 1f, 2, false)
        weapon.unlimitedAmmo = true
        setOrigin(width / 2, height / 2)
    }

    override fun attack(angle: Float) {
        weapon.desiredRotationAngle = angle
        weapon.updateAngleRotation(ROTATION_SPEED)
        weapon.shoot(parent, body.position)
    }

    override fun await() {
        weapon.await()
    }
}