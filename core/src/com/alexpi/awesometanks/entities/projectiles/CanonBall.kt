package com.alexpi.awesometanks.entities.projectiles

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

class CanonBall(manager: AssetManager, world: World, pos: Vector2, angle: Float, power: Float, isPlayer: Boolean) :
    Bullet(manager, world, pos, angle, 35f, .15f, 80f + power*16f, isPlayer)