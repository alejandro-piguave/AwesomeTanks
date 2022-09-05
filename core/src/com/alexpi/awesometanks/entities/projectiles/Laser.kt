package com.alexpi.awesometanks.entities.projectiles

import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 17/01/2016.
 */
class Laser(
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile( pos, angle, 50f, .2f, 20 + power * 5, filter)