package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.ai.EnemyAI
import com.alexpi.awesometanks.entities.ai.EnemyAICallback
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.experimental.or

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    position: Vector2,
    tier: Tier,
    type: Weapon.Type) : Tank(position, getSizeByTier(tier),
    ROTATION_SPEED, MOVEMENT_SPEED,
    Constants.CAT_ENEMY,
    Constants.CAT_BLOCK or Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY,
    getHealthByTierAndType(tier, type),true, getColorByTier(tier)),
    EnemyAICallback {


    private val enemyAI = EnemyAI(body.position, this)
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

    override fun takeDamage(damage: Float) {
        super.takeDamage(damage)
        enemyAI.receiveDamage()
    }

    override fun detach() {
        dropLoot()
        super.detach()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(5, 15)
        for (i in 0 until num1)
            parent.addActor(
                GoldNugget(body.position, Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5))
            )
    }

    companion object {
        private const val ROTATION_SPEED = .035f
        private const val MOVEMENT_SPEED = 60f

        private fun getTypeMultiplier(type: Weapon.Type): Float = when(type){
            Weapon.Type.MINIGUN -> 0f
            Weapon.Type.SHOTGUN -> .2f
            Weapon.Type.RICOCHET -> .4f
            Weapon.Type.FLAMETHROWER -> .6f
            Weapon.Type.CANNON -> .6f
            Weapon.Type.ROCKETS -> .6f
            Weapon.Type.LASERGUN -> 1f
            Weapon.Type.RAILGUN -> 1f

        }

        private fun getNuggetValue(tier: Tier, type: Weapon.Type): Int{
            val tierMultiplier: Float = when(tier){
                Tier.MINI -> .5f
                Tier.NORMAL -> 1f
                Tier.BOSS -> 1.5f
            }

            return 50 + (getTypeMultiplier(type)* 75 * tierMultiplier).toInt()
        }

        private fun getSizeByTier(tier: Tier): Float{
            return when (tier){
                Tier.MINI -> .6f
                Tier.NORMAL -> .75f
                Tier.BOSS -> .87f
            }
        }
        private fun getHealthByTierAndType(tier: Tier, type: Weapon.Type): Float{
            return when (tier){
                Tier.MINI -> 75f + getTypeMultiplier(type) * 100f
                Tier.NORMAL -> 125f + getTypeMultiplier(type) * 300f
                Tier.BOSS -> 250f + getTypeMultiplier(type) * 400f
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
        weapon = Weapon.getWeaponAt(type, 1f, powerByTier(tier), false)
        weapon.unlimitedAmmo = true
        nuggetValue = getNuggetValue(tier, type)
    }

    override fun attack(angle: Float) {
        weapon.desiredRotationAngle = angle
        isShooting = true
        isMoving = false
    }

    override fun move(angle: Float) {
        setOrientation(angle)
        isMoving = true
        isShooting = false
    }

    override fun await() {
        isShooting = false
        isMoving = false
    }

    enum class Tier{
        MINI, NORMAL, BOSS
    }

}
