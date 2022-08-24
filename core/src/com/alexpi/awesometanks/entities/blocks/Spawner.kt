package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.tanks.EnemyTank
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
    private val manager: AssetManager,
    world: World,
    private val targetPosition: Vector2,
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
    private var maxSpan = 20000
    private val maxType: Int
    private val minType: Int
    override fun act(delta: Float) {
        super.act(delta)

        if (lastSpawn + interval < TimeUtils.millis()) {
            lastSpawn = TimeUtils.millis()
            interval = Utils.getRandomInt(maxSpan - 5000, maxSpan).toLong()
            maxSpan += 5000
            parent.addActor(
                EnemyTank(
                    manager,
                    body.world,
                    body.position,
                    targetPosition,
                    EnemyTank.Tier.NORMAL,
                    Utils.getRandomInt(minType, maxType + 1),
                    damageListener
                )
            )
        }
    }

    companion object {
        private fun getHealth(level: Int): Int {
            return 200 + (600) / 30 * level
        }

        @JvmStatic
        fun getMaxType(level: Int): Int {
            return if (level <= 8) Constants.RICOCHET
            else if(level <= 16) Constants.CANON
            else if(level <= 25) Constants.LASERGUN
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
        val num1 = Utils.getRandomInt(5, 10)
        for (i in 0 until num1) parent.addActor(
            GoldNugget(
                manager,
                body.world,
                body.position
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
    }
}