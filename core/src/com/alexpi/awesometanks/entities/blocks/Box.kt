package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.blocks.Spawner.Companion.getMaxType
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 19/01/2016.
 */
class Box(
    listener: DamageListener,
    pos: Vector2,
) : Block("sprites/box.png", Shape.Type.Polygon, 50f, pos, .8f, true, listener) {
    private val maxType: Int = getMaxType(GameModule.level)
    private val nuggetValue: Int = getNuggetValue(GameModule.level)
    private fun drop() {
        when (Utils.getRandomInt(4)) {
            0 -> {
                val num1 = Utils.getRandomInt(10, 16)
                var i = 0
                while (i < num1) {
                    parent.addActor(
                        GoldNugget(
                            body.position,
                            Utils.getRandomInt(nuggetValue - 5, nuggetValue + 5)
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

}