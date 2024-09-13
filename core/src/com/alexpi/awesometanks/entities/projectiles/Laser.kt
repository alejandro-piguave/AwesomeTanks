package com.alexpi.awesometanks.entities.projectiles

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Laser(
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile( pos, BodyShape.Circular(.2f), angle, 50f, 20 + power * 5, filter)