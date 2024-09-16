package com.alexpi.awesometanks.game.blocks

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.FreezingBall
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.items.HealthPack
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTank
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTier
import com.alexpi.awesometanks.game.tanks.enemy.EnemyType
import com.alexpi.awesometanks.game.tanks.enemy.EnemyWeapon
import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.screens.LEVEL_COUNT
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 19/01/2016.
 */
class Box(gameContext: GameContext, level: Int, pos: Vector2) :
    HealthBlock(gameContext, "sprites/box.png", BodyShape.Box(.8f, .8f), pos,50f,  true, false, FixtureFilter.BLOCK) {
    private var generatedTypes: List<EnemyWeapon> = getEnemyTypes(level)
    private val nuggetValue: Int = getNuggetValue(level)
    private fun dropLoot() {
        when (RandomUtils.getRandomInt(4)) {
            0 -> {
                val num1 = RandomUtils.getRandomInt(10, 16)
                var i = 0
                while (i < num1) {
                    parent.addActor(
                        GoldNugget(
                            gameContext,
                            bodyComponent.body.position,
                            RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
                        )
                    )
                    i++
                }
            }

            1 -> parent.addActor(FreezingBall(gameContext, bodyComponent.body.position))
            2 -> parent.addActor(HealthPack(gameContext, bodyComponent.body.position))
            3 -> parent.addActor(
                EnemyTank(
                    gameContext,
                    bodyComponent.body.position,
                    EnemyType(EnemyTier.MINI, generatedTypes.random())
                )
            )
        }
    }

    override fun remove(): Boolean {
        dropLoot()
        return super.remove()
    }

    companion object {
        private fun getNuggetValue(level: Int): Int {
            return (20 + level.toFloat() / (LEVEL_COUNT - 1) * 50).toInt()
        }

        private fun getMaxType(level: Int): Int {
            return if (level <= 7) EnemyWeapon.RICOCHET.ordinal
            else if (level <= 15) EnemyWeapon.ROCKETS.ordinal
            else if (level <= 22) EnemyWeapon.LASERGUN.ordinal
            else EnemyWeapon.RAILGUN.ordinal
        }

        private fun getEnemyTypes(level: Int): List<EnemyWeapon> {
            val maxType = getMaxType(level)
            return EnemyWeapon.values().slice(0..maxType)
        }
    }

}