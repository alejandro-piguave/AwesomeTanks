package com.alexpi.awesometanks.entities.blocks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 20/02/2016.
 */
class Mine(pos: Vector2) : BaseBlock("sprites/mine.png", Shape.Type.Circle, 150f, pos, .5f, true, false, false)