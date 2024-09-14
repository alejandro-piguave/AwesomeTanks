package com.alexpi.awesometanks.game.tiles

import com.alexpi.awesometanks.game.map.Cell
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Created by Alex on 22/02/2016.
 */
class Shade(gameContext: GameContext, private val cell: Cell) : Image() {
    private var isFading = false
    private fun fadeOut() {
        addAction(Actions.sequence(Actions.fadeOut(.75f), Actions.removeActor()))
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!isFading) {
            if(cell.isVisible){
                fadeOut()
                isFading = true
            }
        }
    }

    init {
        drawable = TextureRegionDrawable(
            gameContext.getAssetManager().get(
                "sprites/shade.png",
                Texture::class.java
            )
        )
        val position = cell.toStagePosition(gameContext.getMapTable())
        setBounds(
            position.x,
            position.y,
            TILE_SIZE ,
            TILE_SIZE
        )
    }
}