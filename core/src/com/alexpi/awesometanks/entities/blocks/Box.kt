package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.screens.LEVEL_COUNT
import com.alexpi.awesometanks.utils.RandomUtils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 19/01/2016.
 */
class Box(level: Int, pos: Vector2) : Block("sprites/box.png", Shape.Type.Polygon, 50f, pos, .8f, true, false) {
    private var generatedTypes: List<Weapon.Type> = getEnemyTypes(level)
    private val nuggetValue: Int = getNuggetValue(level)
    private fun dropLoot() {
        when (RandomUtils.getRandomInt(4)) {
            0 -> {
                val num1 = RandomUtils.getRandomInt(10, 16)
                var i = 0
                while (i < num1) {
                    parent.addActor(
                        GoldNugget(
                            body.position,
                            RandomUtils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
                        )
                    )
                    i++
                }
            }
            1 -> parent.addActor(FreezingBall(body.position))
            2 -> parent.addActor(HealthPack( body.position))
            3 -> parent.addActor(
                EnemyTank(
                    body.position,
                    EnemyTank.Tier.MINI,
                    generatedTypes.random()
                )
            )
        }
    }

    override fun onDestroy() {
        dropLoot()
        super.onDestroy()
    }

    companion object {
        private fun getNuggetValue(level: Int): Int {
            return (20 + level.toFloat() / (LEVEL_COUNT - 1) * 50).toInt()
        }

        private fun getMaxType(level: Int): Int {
            return if (level <= 7) Weapon.Type.RICOCHET.ordinal
            else if(level <= 15) Weapon.Type.ROCKETS.ordinal
            else if(level <= 22) Weapon.Type.LASERGUN.ordinal
            else Weapon.Type.RAILGUN.ordinal
        }

        private fun getEnemyTypes(level: Int): List<Weapon.Type> {
            val maxType = getMaxType(level)
            return Weapon.Type.values().slice(0..maxType)
        }
    }

}