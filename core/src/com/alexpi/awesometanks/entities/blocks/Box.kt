package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.AStartPathFinding
import com.alexpi.awesometanks.entities.blocks.Spawner.Companion.getMaxType
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World

/**
 * Created by Alex on 19/01/2016.
 */
class Box(
    listener: DamageListener,
    manager: AssetManager,
    world: World,
    private val pathFinding: AStartPathFinding,
    private val target: PlayerTank,
    pos: Vector2,
    level: Int
) : Block(
    manager, "sprites/box.png", world, PolygonShape(), 50f, pos, .8f, true, listener
) {
    private val maxType: Int
    private val nuggetValue: Int
    private fun drop() {
        when (Utils.getRandomInt(4)) {
            0 -> {
                val num1 = Utils.getRandomInt(10, 16)
                var i = 0
                while (i < num1) {
                    parent.addActor(
                        GoldNugget(
                            manager,
                            body.world,
                            body.position,
                            Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
                        )
                    )
                    i++
                }
            }
            1 -> parent.addActor(FreezingBall(manager, body.world, body.position))
            2 -> parent.addActor(HealthPack(manager, body.world, body.position))
            3 -> parent.addActor(
                EnemyTank(
                    manager,
                    body.world,
                    pathFinding,
                    body.position,
                    target,
                    EnemyTank.Tier.MINI,
                    Utils.getRandomInt(maxType + 1),
                    damageListener
                )
            )
        }
    }

    override fun detach() {
        drop()
        super.detach()
    }

    companion object {
        private fun getNuggetValue(level: Int): Int {
            return (20 + level.toFloat() / (Constants.LEVEL_COUNT - 1) * 50).toInt()
        }
    }

    init {
        maxType = getMaxType(level)
        nuggetValue = getNuggetValue(level)
    }
}