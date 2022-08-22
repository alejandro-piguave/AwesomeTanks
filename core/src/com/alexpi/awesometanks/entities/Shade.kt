package com.alexpi.awesometanks.entities

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.GameMap
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Created by Alex on 22/02/2016.
 */
class Shade(manager: AssetManager,
            private val map: GameMap,
            private val row: Int, private val column: Int) : Image() {
    private var isFading = false
    private fun fadeOut() {
        addAction(Actions.fadeOut(.75f))
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isFading) {
            if(map.isVisible(row,column)){
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
            manager.get(
                "sprites/shade.png",
                Texture::class.java
            )
        )
        val position = map.toWorldPos(row,column)
        setBounds(
            position.x * Constants.TILE_SIZE * SIZE,
            position.y * Constants.TILE_SIZE * SIZE,
            Constants.TILE_SIZE * SIZE,
            Constants.TILE_SIZE * SIZE
        )
    }
}