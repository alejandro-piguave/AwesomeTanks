package com.alexpi.awesometanks.game.blocks

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTank
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTier
import com.alexpi.awesometanks.game.tanks.enemy.EnemyType
import com.alexpi.awesometanks.game.tanks.enemy.EnemyWeapon
import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.screens.LEVEL_COUNT
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils

/**
 * Created by Alex on 15/02/2016.
 */
class Spawner(gameContext: GameContext, level: Int, pos: Vector2) : HealthBlock(
    gameContext,
    "sprites/spawner.png",
    BodyShape.Box(1f, 1f),
    pos,
    getHealth(level),
    true,
    true, FixtureFilter.SPAWNER
) {
    private val gameStage: GameStage = gameContext.getStage()
    private var lastSpawn: Long
    private var interval: Long
    private var maxSpan = 15000
    private var generatedTypes: List<EnemyWeapon> = getEnemyTypes(level)
    private val nuggetValue: Int
    override fun act(delta: Float) {
        super.act(delta)
        if (lastSpawn + interval < TimeUtils.millis() && !healthComponent.isFrozen) {
            lastSpawn = TimeUtils.millis()
            //It increments the spawn interval by 5 seconds each time to prevent excessive enemy generation
            interval = RandomUtils.getRandomInt(maxSpan - 5000, maxSpan).toLong()
            maxSpan += 5000
            parent.addActor(
                EnemyTank(
                    gameContext,
                    bodyComponent.body.position,
                    EnemyType(EnemyTier.NORMAL, generatedTypes.random())
                )
            )
        }
    }

    companion object {
        private fun getHealth(level: Int): Float {
            return 400f + level.toFloat() / (LEVEL_COUNT - 1) * 1000f
        }

        private fun getNuggetValue(level: Int): Int {
            return 60 + (level.toFloat() / (LEVEL_COUNT - 1) * 80).toInt()
        }

        private fun getMaxType(level: Int): Int {
            return if (level <= 7) EnemyWeapon.RICOCHET.ordinal
            else if (level <= 15) EnemyWeapon.ROCKETS.ordinal
            else if (level <= 22) EnemyWeapon.LASERGUN.ordinal
            else EnemyWeapon.RAILGUN.ordinal
        }

        private fun getMinType(level: Int): Int {
            return if (level <= 10) EnemyWeapon.MINIGUN.ordinal
            else if (level <= 12) EnemyWeapon.SHOTGUN.ordinal
            else if (level <= 16) EnemyWeapon.RICOCHET.ordinal
            else EnemyWeapon.FLAMETHROWER.ordinal
        }

        private fun getEnemyTypes(level: Int): List<EnemyWeapon> {
            val minType = getMinType(level)
            val maxType = getMaxType(level)

            return EnemyWeapon.values().slice(minType..maxType)
        }
    }

    override fun remove(): Boolean {
        dropLoot()
        gameStage.checkLevelCompletion()
        return super.remove()
    }

    private fun dropLoot() {
        val num1 = RandomUtils.getRandomInt(10, 15)
        for (i in 0 until num1)
            parent.addActor(
                GoldNugget(
                    gameContext,
                    bodyComponent.body.position,
                    RandomUtils.getRandomInt(nuggetValue - 10, nuggetValue + 10)
                )
            )
    }

    init {
        lastSpawn = TimeUtils.millis()
        interval = 1000
        nuggetValue = getNuggetValue(level)
    }
}