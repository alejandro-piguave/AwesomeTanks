package com.alexpi.awesometanks.game.items

import com.alexpi.awesometanks.game.utils.RandomUtils
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2


/**
 * Created by Alex on 01/02/2016.
 */
class GoldNugget(gameContext: GameContext, position: Vector2, val value: Int) :
    BaseItem(gameContext,"sprites/nugget.png", position, RandomUtils.getRandomFloat(.15f, .25f)) {
    init {
        val angle = RandomUtils.getRandomFloat(Math.PI * 2)
        body.applyLinearImpulse(
            MathUtils.cos(angle) * .025f,
            MathUtils.sin(angle) * .025f,
            body.position.x,
            body.position.y,
            true
        )
    }
}
