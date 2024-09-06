package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class Floor(pos: Vector2): Image(GameModule.assetManager.get("sprites/sand.png", Texture::class.java)) {
    init {
        setBounds(
            pos.x * Constants.TILE_SIZE,
            pos.y * Constants.TILE_SIZE,
            Constants.TILE_SIZE,
            Constants.TILE_SIZE
        )
    }
}