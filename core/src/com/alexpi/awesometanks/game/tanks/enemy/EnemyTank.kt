package com.alexpi.awesometanks.game.tanks.enemy

import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.components.health.HealthComponent
import com.alexpi.awesometanks.game.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.tanks.Tank
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTankState.Companion.DAMAGE_RECEIVED_MESSAGE
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
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    gameContext: GameContext,
    position: Vector2,
    type: EnemyType
) : Tank(
    gameContext, position, FixtureFilter.ENEMY_TANK, type.tier.size,
    MOVEMENT_SPEED, type.tier.color
), Telegraph {

    val stateMachine = DefaultStateMachine<EnemyTank, EnemyTankState>(this, WanderState())
    private val nuggetValue: Int = type.getNuggetValue()

    override val currentWeapon: Weapon = getWeaponAt(type.weapon, gameContext, 1f, type.tier.power)
    override val healthComponent: HealthComponent = HealthComponent(
        this,
        gameContext,
        type.getHealth(),
        isFlammable = true,
        isFreezable = true
    )
    override val healthBarComponent = HealthBarComponent(
        gameContext,
        healthComponent.maxHealth,
        healthComponent.currentHealth,
        2f
    )

    override fun act(delta: Float) {
        super.act(delta)
        stateMachine.update()
    }

    override fun remove(): Boolean {
        dropLoot()
        gameContext.getStage().checkLevelCompletion()
        return super.remove()
    }

    private fun dropLoot() {
        val count = RandomUtils.getRandomInt(5, 15)
        repeat(count) {
            parent.addActor(
                GoldNugget(
                    gameContext,
                    bodyComponent.body.position,
                    RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
                )
            )
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
            EnemyWeapon.MINIGUN -> MiniGun(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.SHOTGUN -> ShotGun(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.RICOCHET -> Ricochet(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.FLAMETHROWER -> Flamethrower(
                gameContext,
                ammo,
                power,
                false,
                WEAPON_ROTATION_SPEED
            )

            EnemyWeapon.CANNON -> Cannon(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.ROCKETS -> RocketLauncher(
                gameContext,
                ammo,
                power,
                false,
                WEAPON_ROTATION_SPEED,
                rocketListener
            )

            EnemyWeapon.LASERGUN -> LaserGun(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.RAILGUN -> RailGun(gameContext, ammo, power, false, WEAPON_ROTATION_SPEED)
        }
    }

    override fun onDeath() {
        super.onDeath()
        addAction(Actions.removeActor())
    }

    override fun onTakeDamage(health: Float) {
        super.onTakeDamage(health)
        healthBarComponent.updateHealth(health)
        MessageManager.getInstance().dispatchMessage(this, stateMachine, DAMAGE_RECEIVED_MESSAGE)
    }

    override fun onHeal(health: Float) {
        super.onHeal(health)
        healthBarComponent.updateHealth(health)
    }

    override fun onFreeze() {
        super.onFreeze()
        stateMachine.changeState(FrozenState)
        addAction(Actions.run { bodyComponent.body.type = BodyDef.BodyType.StaticBody })
    }

    companion object {
        private const val WEAPON_ROTATION_SPEED = 2.1f
        private const val MOVEMENT_SPEED = 60f

    }

    init {
        currentWeapon.unlimitedAmmo = true
    }


    override fun handleMessage(msg: Telegram): Boolean = true

}
