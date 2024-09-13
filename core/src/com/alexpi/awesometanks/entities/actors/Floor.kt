package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

class Floor(gameContext: GameContext, cell: Cell): Image(gameContext.getAssetManager().get("sprites/sand.png", Texture::class.java)) {
    init {
        val position = cell.toStagePosition(gameContext.getMapTable())
        setBounds(
            position.x,
            position.y,
            TILE_SIZE,
            TILE_SIZE
        )
    }
}