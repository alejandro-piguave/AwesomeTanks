package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.artemis.World
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

fun World.createGroundTile(assetManager: AssetManager, position: Vector2): Int {
    val i = create()

    val spriteComponent = SpriteComponent()
    spriteComponent.layer = SpriteComponent.Layer.FLOOR
    spriteComponent.sprite = Sprite(assetManager.get<Texture>("sprites/sand.png"))
    spriteComponent.sprite.setPosition(position.x, position.y)
    spriteComponent.sprite.setSize(TILE_SIZE, TILE_SIZE)
    edit(i).add(spriteComponent)

    return i
}