package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.EnemyAI
import com.alexpi.awesometanks.entities.ai.EnemyAICallback
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.experimental.or

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    manager: AssetManager,
    world: World,
    position: Vector2,
    target: PlayerTank,
    tier: Tier,
    type: Int, damageListener: DamageListener?) : Tank(manager, world, position, getSizeByTier(tier),
    ROTATION_SPEED, MOVEMENT_SPEED,
    Constants.CAT_ENEMY,
    Constants.CAT_BLOCK or Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY,
    getHealthByTierAndType(tier, type),true, damageListener, getColorByTier(tier)),
    EnemyAICallback {


    private val enemyAI = EnemyAI(world, body.position, target, this)
    private val nuggetValue: Int
    private val weapon: Weapon
    override val currentWeapon: Weapon
        get() = weapon

    override fun act(delta: Float) {
        if(isAlive && !isFrozen){
            enemyAI.update()
        }else{
            await()
        }
        super.act(delta)
    }

    override fun detach() {
        dropLoot()
        super.detach()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(5, 15)
        for (i in 0 until num1) parent.addActor(
            GoldNugget(
                manager,
                body.world,
                body.position,
                Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
            )
        )
    }

    companion object {
        private const val ROTATION_SPEED = .035f
        private const val MOVEMENT_SPEED = 60f

        private fun getNuggetValue(tier: Tier, type: Int): Int{
            val tierMultiplier: Float = when(tier){
                Tier.MINI -> .5f
                Tier.NORMAL -> 1f
                Tier.BOSS -> 1.5f
            }

            val typeMultiplier: Float = when(type){
                Constants.MINIGUN -> 0f
                Constants.SHOTGUN -> .2f
                Constants.RICOCHET -> .3f
                Constants.FLAMETHROWER -> .6f
                Constants.CANON -> .6f
                Constants.ROCKET -> .6f
                Constants.LASERGUN -> 1f
                else -> 1f

            }
            return 50 + (typeMultiplier* 75 * tierMultiplier).toInt()
        }

        private fun getSizeByTier(tier: Tier): Float{
            return when (tier){
                Tier.MINI -> .6f
                Tier.NORMAL -> .75f
                Tier.BOSS -> .9f
            }
        }
        private fun getHealthByTierAndType(tier: Tier, type: Int): Float{
            return when (tier){
                Tier.MINI -> 100f + type/Constants.RAILGUN * 100f
                Tier.NORMAL -> 150f + type/Constants.RAILGUN * 300f
                Tier.BOSS -> 300f + type/Constants.RAILGUN * 500f
            }
        }

        private fun getColorByTier(tier: Tier): Color {
            return when (tier){
                Tier.MINI -> Color.SKY
                Tier.NORMAL -> Color.GOLD
                Tier.BOSS -> Color.RED
            }
        }

        private fun powerByTier(tier: Tier): Int{
            return when(tier){
                Tier.MINI -> 0
                Tier.NORMAL -> 1
                Tier.BOSS -> 3
            }
        }
    }

    init {
        weapon = Weapon.getWeaponAt(type, manager, 1f, powerByTier(tier), false)
        weapon.setUnlimitedAmmo(true)
        nuggetValue = getNuggetValue(tier, type)
    }

    override fun attack(angle: Float) {
        setOrientation(angle)
        weapon.setDesiredAngleRotation(angle)
        isMoving = true
        isShooting = true
    }

    override fun await() {
        isShooting = false
        isMoving = false
    }

    enum class Tier{
        MINI, NORMAL, BOSS
    }

}
