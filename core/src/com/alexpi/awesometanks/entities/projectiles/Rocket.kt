package com.alexpi.awesometanks.entities.projectiles

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World

class Rocket(
    manager: AssetManager,
    world: World,
    pos: Vector2,
    angle: Float,
    power: Int,
    filter: Boolean
) : Projectile(world, pos, PolygonShape(), angle, 5f, .2f, 20F + power * 5, filter){

    private val flameSprite: Sprite

    init {
        sprite = Sprite(manager.get<Texture>("sprites/rocket.png"))
        flameSprite = Sprite(manager.get<Texture>("sprites/rocket_flame.png"))
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        super.draw(batch, parentAlpha)
    }
}