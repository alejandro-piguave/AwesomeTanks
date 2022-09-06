package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 20/01/2016.
 */
class Bricks(listener: DamageListener, pos: Vector2) : Block("sprites/bricks.png", Shape.Type.Polygon, 200f, pos, 1f, true, false,  listener)