package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.TimeUtils
import kotlin.experimental.or

/**
 * Created by Alex on 15/02/2016.
 */
class Spawner(
    listener: DamageListener,
    manager: AssetManager,
    world: World,
    private val target: PlayerTank,
    pos: Vector2,
    level: Int
) : Block(
    manager,
    "sprites/spawner.png",
    world,
    PolygonShape(),
    getHealth(level),
    pos,
    1f,
    true,
    listener
) {
    private var lastSpawn: Long
    private var interval: Long
    private var maxSpan = 15000
    private val maxType: Int
    private val minType: Int
    private val nuggetValue: Int
    override fun act(delta: Float) {
        super.act(delta)

        if (lastSpawn + interval < TimeUtils.millis()) {
            lastSpawn = TimeUtils.millis()
            //It increments the spawn interval by 5 seconds each time to prevent excessive enemy generation
            interval = Utils.getRandomInt(maxSpan - 5000, maxSpan).toLong()
            maxSpan += 5000
            parent.addActor(
                EnemyTank(
                    manager,
                    body.world,
                    body.position,
                    target,
                    EnemyTank.Tier.NORMAL,
                    Utils.getRandomInt(minType, maxType + 1),
                    damageListener
                )
            )
        }
    }

    companion object {
        private fun getHealth(level: Int): Float {
            return 400f + (1000f) / (Constants.LEVEL_COUNT - 1) * level
        }

        private fun getNuggetValue(level: Int): Int{
            return 60 + (level.toFloat()/(Constants.LEVEL_COUNT -1)*80).toInt()
        }

        @JvmStatic
        fun getMaxType(level: Int): Int {
            return if (level <= 7) Constants.RICOCHET
            else if(level <= 15) Constants.ROCKET
            else if(level <= 22) Constants.LASERGUN
            else Constants.RAILGUN
        }

        @JvmStatic
        fun getMinType(level: Int): Int {
            return if (level <= 10) Constants.MINIGUN
            else if(level <= 12) Constants.SHOTGUN
            else if(level <= 16) Constants.RICOCHET
            else Constants.FLAMETHROWER
        }
    }

    override fun detach() {
        dropLoot()
        super.detach()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(10, 15)
        for (i in 0 until num1) parent.addActor(
            GoldNugget(
                manager,
                body.world,
                body.position,
                Utils.getRandomInt(nuggetValue-10, nuggetValue+10)
            )
        )
    }

    init {
        fixture.filterData.maskBits =
            (Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ITEM)
        lastSpawn = TimeUtils.millis()
        interval = 1000
        maxType = getMaxType(level)
        minType = getMinType(level)
        nuggetValue = getNuggetValue(level)
    }
}