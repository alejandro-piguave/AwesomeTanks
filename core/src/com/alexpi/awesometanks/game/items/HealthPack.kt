package com.alexpi.awesometanks.game.items

import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 19/02/2016.
 */
class HealthPack(gameContext: GameContext, position: Vector2) : Item(gameContext,"sprites/health_pack.png", position, .4f) {
    val health: Int = RandomUtils.getRandomInt(100, 200)
}
