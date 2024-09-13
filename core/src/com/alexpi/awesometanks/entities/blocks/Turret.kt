package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.ai.TurretAI
import com.alexpi.awesometanks.entities.ai.TurretAICallback
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.alexpi.awesometanks.utils.RandomUtils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 18/02/2016.
 */
class Turret(
    gameContext: GameContext,
    pos: Vector2,
    type: Weapon.Type
) : HealthBlock( gameContext,"sprites/turret_base.png", BodyShape.Box(.8f, .8f), pos, getHealthByType(type),true, true, FixtureFilter.TURRET), TurretAICallback {
    private val weapon: Weapon
    private val gameStage: GameStage = gameContext.getStage()
    private val enemyAI = TurretAI(gameContext, bodyComponent.body.position, this)
    private val nuggetValue: Int = getNuggetValue(type)
    private val explosionManager = gameContext.getExplosionManager()

    override fun drawBlock(batch: Batch, parentAlpha: Float) {
        super.drawBlock(batch, parentAlpha)
        weapon.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)

    }

    override fun act(delta: Float) {
        if (!healthComponent.isFrozen){
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

    override fun remove(): Boolean {
        dropLoot()
        gameStage.checkLevelCompletion()
        return super.remove()
    }

    private fun dropLoot() {
        val count = RandomUtils.getRandomInt(10, 15)
        repeat(count){
            parent.addActor(GoldNugget(bodyComponent.body.position, RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)))

        }
    }

    init {
        weapon = Weapon.getWeaponAt(type, gameContext,1f, 2, false)
        weapon.unlimitedAmmo = true
        setOrigin(width / 2, height / 2)
    }

    override fun attack(angle: Float) {
        weapon.desiredRotationAngle = angle
        weapon.updateAngleRotation(ROTATION_SPEED)
        weapon.shoot(parent, bodyComponent.body.position)
    }

    override fun await() {
        weapon.await()
    }
}