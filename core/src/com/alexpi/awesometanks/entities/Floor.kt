package com.alexpi.awesometanks.entities

import com.alexpi.awesometanks.utils.Constants
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class Floor(assetManager: AssetManager, pos: Vector2): Image(assetManager.get("sprites/sand.png", Texture::class.java)) {
    init {
        setBounds(
            pos.x * Constants.TILE_SIZE,
            pos.y * Constants.TILE_SIZE,
            Constants.TILE_SIZE,
            Constants.TILE_SIZE
        )
    }
}