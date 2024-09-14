package com.alexpi.awesometanks.game.tanks.enemy

import com.alexpi.awesometanks.game.ai.EnemyTankState
import com.alexpi.awesometanks.game.ai.FrozenState
import com.alexpi.awesometanks.game.ai.WanderState
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.tanks.Tank
import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.game.weapons.Cannon
import com.alexpi.awesometanks.game.weapons.Flamethrower
import com.alexpi.awesometanks.game.weapons.LaserGun
import com.alexpi.awesometanks.game.weapons.MiniGun
import com.alexpi.awesometanks.game.weapons.RailGun
import com.alexpi.awesometanks.game.weapons.Ricochet
import com.alexpi.awesometanks.game.weapons.RocketLauncher
import com.alexpi.awesometanks.game.weapons.RocketListener
import com.alexpi.awesometanks.game.weapons.ShotGun
import com.alexpi.awesometanks.game.weapons.Weapon
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    val gameContext: GameContext,
    position: Vector2,
    type: EnemyType) : Tank(gameContext, position, FixtureFilter.ENEMY_TANK, type.tier.size,
    type.getHealth(),
    true,
    ROTATION_SPEED, MOVEMENT_SPEED, type.tier.color
), Telegraph {

    val stateMachine = DefaultStateMachine<EnemyTank, EnemyTankState>(this, WanderState)
    private val nuggetValue: Int
    private val weapon: Weapon = getWeaponAt(type.weapon, gameContext,1f, type.tier.power)
    override val currentWeapon: Weapon
        get() = weapon

    override fun act(delta: Float) {
        super.act(delta)
        stateMachine.update()
    }

    override fun remove(): Boolean {
        dropLoot()
        return super.remove()
    }

    private fun dropLoot() {
        val count = RandomUtils.getRandomInt(5, 15)
        repeat(count){
            parent.addActor(GoldNugget(bodyComponent.body.position, RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)))
        }
    }

    private fun getWeaponAt(
        type: EnemyWeapon,
        gameContext: GameContext,
        ammo: Float,
        power: Int,
        rocketListener: RocketListener? = null
    ): Weapon {
        return when (type) {
            EnemyWeapon.MINIGUN -> MiniGun(gameContext, ammo, power, false)
            EnemyWeapon.SHOTGUN -> ShotGun(gameContext, ammo, power, false)
            EnemyWeapon.RICOCHET -> Ricochet(gameContext, ammo, power, false)
            EnemyWeapon.FLAMETHROWER -> Flamethrower(gameContext, ammo, power, false)
            EnemyWeapon.CANNON -> Cannon(gameContext, ammo, power, false)
            EnemyWeapon.ROCKETS -> RocketLauncher(gameContext, ammo, power, false, rocketListener)
            EnemyWeapon.LASERGUN -> LaserGun(gameContext, ammo, power, false)
            EnemyWeapon.RAILGUN -> RailGun(gameContext, ammo, power, false)
        }
    }

    companion object {
        private const val ROTATION_SPEED = .035f
        private const val MOVEMENT_SPEED = 60f

    }

    init {
        weapon.unlimitedAmmo = true
        nuggetValue = type.getNuggetValue()
        healthComponent.onDamageTaken = {
            healthBarComponent.updateHealth(it, 2f)
            MessageManager.getInstance().dispatchMessage(this, stateMachine, EnemyTankState.DAMAGE_RECEIVED_MESSAGE)
        }
        healthComponent.onFreeze = {
            stateMachine.changeState(FrozenState)
        }
    }
    

    override fun handleMessage(msg: Telegram): Boolean  = true

}
