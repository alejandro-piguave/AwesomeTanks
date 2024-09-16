package com.alexpi.awesometanks.game.projectiles

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 14/01/2016.
 */
open class Bullet(
    gameContext: GameContext,
    pos: Vector2,
    angle: Float,
    speed: Float,
    radius: Float,
    damage: Float,
    isPlayer: Boolean
) : Projectile(gameContext, pos, BodyShape.Circular(radius), angle, speed, damage, isPlayer) {
    init {
        sprite = Sprite(gameContext.getAssetManager().get("sprites/bullet.png", Texture::class.java))
    }
}