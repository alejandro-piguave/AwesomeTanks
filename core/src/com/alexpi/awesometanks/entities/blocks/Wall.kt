package com.alexpi.awesometanks.entities.blocks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 13/01/2016.
 */
class Wall(pos: Vector2) : Block("sprites/wall.png", Shape.Type.Polygon, pos, 1f)