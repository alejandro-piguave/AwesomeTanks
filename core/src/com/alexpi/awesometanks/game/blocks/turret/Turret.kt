package com.alexpi.awesometanks.game.blocks.turret

import com.alexpi.awesometanks.game.blocks.HealthBlock
import com.alexpi.awesometanks.game.blocks.turret.TurretState.Companion.DAMAGE_RECEIVED_MESSAGE
import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.tanks.enemy.EnemyWeapon
import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.game.weapons.Cannon
import com.alexpi.awesometanks.game.weapons.Flamethrower
import com.alexpi.awesometanks.game.weapons.LaserGun
import com.alexpi.awesometanks.game.weapons.MiniGun
import com.alexpi.awesometanks.game.weapons.RailGun
import com.alexpi.awesometanks.game.weapons.Ricochet
import com.alexpi.awesometanks.game.weapons.RocketLauncher
import com.alexpi.awesometanks.game.weapons.ShotGun
import com.alexpi.awesometanks.game.weapons.Weapon
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 18/02/2016.
 */
class Turret(
    gameContext: GameContext,
    pos: Vector2,
    type: EnemyWeapon
) : HealthBlock(gameContext,"sprites/turret_base.png", BodyShape.Box(.8f, .8f), pos, getHealthByType(type),true, true, FixtureFilter.TURRET),
    Telegraph {
    val weapon: Weapon = getWeaponAt(type, gameContext)
    private val entityGroup = gameContext.getEntityGroup()
    private val gameStage: GameStage = gameContext.getStage()
    val stateMachine = DefaultStateMachine<Turret, TurretState>(this, PeekState())
    private val nuggetValue: Int = getNuggetValue(type)

    override fun drawBlock(batch: Batch, parentAlpha: Float) {
        super.drawBlock(batch, parentAlpha)
        weapon.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
    }

    override fun act(delta: Float) {
        super.act(delta)
        stateMachine.update()
        weapon.update(delta, entityGroup, bodyComponent.body.position)
    }

    companion object {
        private const val WEAPON_ROTATION_SPEED = 2.1f

        fun getHealthByType(type: EnemyWeapon): Float{
            val typeMultiplier: Float = when(type){
                EnemyWeapon.MINIGUN -> 0f
                EnemyWeapon.SHOTGUN -> .2f
                EnemyWeapon.RICOCHET -> .3f
                EnemyWeapon.FLAMETHROWER -> .6f
                EnemyWeapon.ROCKETS -> .6f
                EnemyWeapon.CANNON -> .6f
                EnemyWeapon.LASERGUN -> 1f
                EnemyWeapon.RAILGUN -> 1f

            }
            return 200f + typeMultiplier * 500f
        }

        private fun getNuggetValue(type: EnemyWeapon): Int{
            return 60 + (type.valueMultiplier*75).toInt()
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
            parent.addActor(GoldNugget(gameContext, bodyComponent.body.position, RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)))
        }
    }

    override fun onFreeze() {
        super.onFreeze()
        stateMachine.changeState(FrozenState)
    }

    override fun onTakeDamage(health: Float) {
        super.onTakeDamage(health)
        MessageManager.getInstance().dispatchMessage(this, stateMachine, DAMAGE_RECEIVED_MESSAGE)
    }

    init {
        weapon.unlimitedAmmo = true
        setOrigin(width / 2, height / 2)
    }

    private fun getWeaponAt(
        type: EnemyWeapon,
        gameContext: GameContext,
    ): Weapon {
        return when (type) {
            EnemyWeapon.MINIGUN -> MiniGun(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.SHOTGUN -> ShotGun(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.RICOCHET -> Ricochet(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.FLAMETHROWER -> Flamethrower(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.CANNON -> Cannon(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.ROCKETS -> RocketLauncher(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED, null)
            EnemyWeapon.LASERGUN -> LaserGun(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
            EnemyWeapon.RAILGUN -> RailGun(gameContext, 1f, 2, false, WEAPON_ROTATION_SPEED)
        }
    }

    override fun handleMessage(msg: Telegram): Boolean = true
}