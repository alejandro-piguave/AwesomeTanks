package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Laser(
    gameContext: GameContext,
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile(gameContext, pos, BodyShape.Circular(.1f), angle, 50f, 20 + power * 5, filter)