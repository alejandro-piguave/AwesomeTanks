package com.alexpi.awesometanks.screens.widget

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

open class FloatProgressBar(
    assetManager: AssetManager,
    maxValue: Float,
    initialValue: Float,
    vertical: Boolean = false
) : Actor() {
    val maxValue: Float
    var currentValue: Float = initialValue
        set(value) {
            require(!(value > maxValue)) { "initialValue can't be greater tha max" }
            field = value
        }

    private val vertical: Boolean
    private val background: NinePatchDrawable
    private val foreground: NinePatchDrawable

    init {
        require(!(maxValue <= 0)) { "max value must be positive" }
        require(!(initialValue > maxValue)) { "initialValue can't be greater tha max" }
        this.maxValue = maxValue
        this.vertical = vertical
        this.background = NinePatchDrawable(
            NinePatch(
                assetManager.get(
                    "ui/progress_bar_background.9.png",
                    Texture::class.java
                ), 6, 6, 6, 6
            )
        )
        this.foreground = NinePatchDrawable(
            NinePatch(
                assetManager.get(
                    "ui/progress_bar_foreground.9.png",
                    Texture::class.java
                ), 6, 6, 6, 6
            )
        )
        if (vertical) width = 20f
        else height = 20f
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        background.draw(batch, x, y, width, height)
        if (currentValue > 0) {
            if (vertical) foreground.draw(
                batch, x + BORDER_WIDTH, y + BORDER_WIDTH, (width - 2 * BORDER_WIDTH),
                (height - 2 * BORDER_WIDTH) * (currentValue / maxValue)
            )
            else foreground.draw(
                batch, x + BORDER_WIDTH, y + BORDER_WIDTH,
                (width - 2 * BORDER_WIDTH) * (currentValue / maxValue), height - 2 * BORDER_WIDTH
            )
        }
    }

    companion object {
        private const val BORDER_WIDTH = 4f
    }
}
