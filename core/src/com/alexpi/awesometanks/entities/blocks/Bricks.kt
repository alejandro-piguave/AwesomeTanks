package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 20/01/2016.
 */
class Bricks(gameContext: GameContext, pos: Vector2) : HealthBlock(gameContext,"sprites/bricks.png", BodyShape.Box(1f, 1f), pos,200f,  true, false, FixtureFilter.BLOCK)