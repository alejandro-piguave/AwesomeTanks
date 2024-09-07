package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.screens.LEVEL_COUNT
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.collision.CAT_ITEM
import com.alexpi.awesometanks.world.collision.CAT_PLAYER
import com.alexpi.awesometanks.world.collision.CAT_PLAYER_BULLET
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.utils.TimeUtils
import kotlin.experimental.or

/**
 * Created by Alex on 15/02/2016.
 */
class Spawner(level: Int, pos: Vector2) : Block(
    "sprites/spawner.png",
    Shape.Type.Polygon,
    getHealth(level),
    pos,
    1f,
    true,
    true
) {
    private var lastSpawn: Long
    private var interval: Long
    private var maxSpan = 15000
    private var generatedTypes: List<Weapon.Type> = getEnemyTypes(level)
    private val nuggetValue: Int
    override fun onAlive(delta: Float) {
        super.onAlive(delta)

        if (lastSpawn + interval < TimeUtils.millis() && !isFrozen) {
            lastSpawn = TimeUtils.millis()
            //It increments the spawn interval by 5 seconds each time to prevent excessive enemy generation
            interval = Utils.getRandomInt(maxSpan - 5000, maxSpan).toLong()
            maxSpan += 5000
            parent.addActor(
                EnemyTank(
                    body.position,
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

    override fun onDestroy() {
        dropLoot()
        super.onDestroy()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(10, 15)
        for (i in 0 until num1)
            parent.addActor(
                GoldNugget(
                    body.position,
                    Utils.getRandomInt(nuggetValue - 10, nuggetValue + 10)
                )
            )
    }

    init {
        fixture.filterData.maskBits =
            (CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ITEM)
        lastSpawn = TimeUtils.millis()
        interval = 1000
        nuggetValue = getNuggetValue(level)
    }
}