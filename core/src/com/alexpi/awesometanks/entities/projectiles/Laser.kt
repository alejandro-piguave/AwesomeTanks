package com.alexpi.awesometanks.entities.projectiles

import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.math.Vector2
import com.alexpi.awesometanks.entities.projectiles.Projectile
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.graphics.g2d.Sprite

/**
 * Created by Alex on 17/01/2016.
 */
class Laser(
    manager: AssetManager,
    world: World,
    pos: Vector2,
    angle: Float,
    power: Float,
    filter: Boolean
) : Projectile(world, pos, PolygonShape(), angle, 35f, .2f, 20 + power * 5, filter) {
    init {
        //sprite = Sprite(manager.get("sprites/laser.png", Texture::class.java))
    }
}