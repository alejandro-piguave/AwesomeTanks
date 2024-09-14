package com.alexpi.awesometanks.game.blocks

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.tanks.EnemyTank
import com.alexpi.awesometanks.screens.LEVEL_COUNT
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.game.weapons.Weapon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils

/**
 * Created by Alex on 15/02/2016.
 */
class Spawner(private val gameContext: GameContext, level: Int, pos: Vector2) : HealthBlock(
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
    private var generatedTypes: List<Weapon.Type> = getEnemyTypes(level)
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
                    EnemyTank.Tier.NORMAL,
                    generatedTypes.random()
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
            return if (level <= 7) Weapon.Type.RICOCHET.ordinal
            else if (level <= 15) Weapon.Type.ROCKETS.ordinal
            else if (level <= 22) Weapon.Type.LASERGUN.ordinal
            else Weapon.Type.RAILGUN.ordinal
        }

        private fun getMinType(level: Int): Int {
            return if (level <= 10) Weapon.Type.MINIGUN.ordinal
            else if (level <= 12) Weapon.Type.SHOTGUN.ordinal
            else if (level <= 16) Weapon.Type.RICOCHET.ordinal
            else Weapon.Type.FLAMETHROWER.ordinal
        }

        private fun getEnemyTypes(level: Int): List<Weapon.Type> {
            val minType = getMinType(level)
            val maxType = getMaxType(level)

            return Weapon.Type.values().slice(minType..maxType)
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