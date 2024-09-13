package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 13/01/2016.
 */
class Wall(gameContext: GameContext, pos: Vector2) : Block(gameContext,"sprites/wall.png", BodyShape.Box(1f, 1f), pos, FixtureFilter.BLOCK)