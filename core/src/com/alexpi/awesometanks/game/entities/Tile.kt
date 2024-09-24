package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.artemis.World
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

fun World.createTile(assetManager: AssetManager, texturePath: String, position: Vector2): Int {
    val i = create()
    edit(i).add(SpriteComponent().apply {
        sprite = Sprite(assetManager.get<Texture>(texturePath)).apply {
            setPosition(position.x, position.y)
            setSize(TILE_SIZE, TILE_SIZE)
        }
    })

    return i
}