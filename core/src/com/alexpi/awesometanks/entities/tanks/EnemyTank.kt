package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.ai.EnemyTankState
import com.alexpi.awesometanks.entities.ai.FrozenState
import com.alexpi.awesometanks.entities.ai.WanderState
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.entities.components.body.CAT_BLOCK
import com.alexpi.awesometanks.entities.components.body.CAT_ENEMY
import com.alexpi.awesometanks.entities.components.body.CAT_PLAYER
import com.alexpi.awesometanks.entities.components.body.CAT_PLAYER_BULLET
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
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
    CAT_ENEMY,
    CAT_BLOCK or CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ENEMY,
    getHealthByTierAndType(tier, type),true, getColorByTier(tier)), Telegraph {

    val stateMachine = DefaultStateMachine<EnemyTank, EnemyTankState>(this, WanderState(position.cpy()))
    private val nuggetValue: Int
    private val weapon: Weapon = Weapon.getWeaponAt(type, 1f, powerByTier(tier), false)
    override val currentWeapon: Weapon
        get() = weapon

    override fun onAlive(delta: Float) {
        super.onAlive(delta)
        if(isAlive) stateMachine.update()
    }

    override fun takeDamage(damage: Float) {
        super.takeDamage(damage)
        MessageManager.getInstance().dispatchMessage(this, stateMachine, EnemyTankState.DAMAGE_RECEIVED_MESSAGE)
    }

    override fun freeze(){
        super.freeze()
        stateMachine.changeState(FrozenState)
    }

    override fun onDestroy() {
        dropLoot()
        super.onDestroy()
    }

    private fun dropLoot() {
        val count = Utils.getRandomInt(5, 15)
        repeat(count){
            parent.addActor(GoldNugget(body.position, Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5)))
        }
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
        weapon.unlimitedAmmo = true
        nuggetValue = getNuggetValue(tier, type)
    }

    enum class Tier{
        MINI, NORMAL, BOSS
    }

    override fun handleMessage(msg: Telegram): Boolean  = true

}
