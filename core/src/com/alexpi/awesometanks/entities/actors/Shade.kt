package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Created by Alex on 22/02/2016.
 */
class Shade(private val cell: Cell) : Image() {
    private var isFading = false
    private fun fadeOut() {
        addAction(Actions.fadeOut(.75f))
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isFading) {
            if(cell.isVisible){
                fadeOut()
                isFading = true
            }
        } else if (!isVisible) remove()
    }

    companion object {
        private const val SIZE = 1f
    }

    init {
        drawable = TextureRegionDrawable(
            GameModule.assetManager.get(
                "sprites/shade.png",
                Texture::class.java
            )
        )
        val position = cell.toWorldPosition(GameModule.mapTable)
        setBounds(
            position.x * TILE_SIZE * SIZE,
            position.y * TILE_SIZE * SIZE,
            TILE_SIZE * SIZE,
            TILE_SIZE * SIZE
        )
    }
}