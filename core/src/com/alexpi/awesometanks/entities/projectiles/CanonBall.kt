package com.alexpi.awesometanks.entities.projectiles

import com.badlogic.gdx.math.Vector2

class CanonBall( pos: Vector2, angle: Float, power: Float, isPlayer: Boolean) :
    Bullet( pos, angle, 35f, .15f, 80f + power*16f, isPlayer)