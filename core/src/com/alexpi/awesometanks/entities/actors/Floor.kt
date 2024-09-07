package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image

class Floor(pos: Vector2): Image(GameModule.assetManager.get("sprites/sand.png", Texture::class.java)) {
    init {
        setBounds(
            pos.x * TILE_SIZE,
            pos.y * TILE_SIZE,
            TILE_SIZE,
            TILE_SIZE
        )
    }
}