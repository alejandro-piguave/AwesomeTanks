package com.alexpi.awesometanks.screens.widget

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

class IntProgressBar(
    assetManager: AssetManager,
    initialValue: Int,
    val maxValue: Int,
    val orientation: BarOrientation
) : Actor() {

    var currentValue: Int = initialValue
        set(value) {
            require(value <= maxValue) { "initialValue can't be greater tha maxValue" }
            field = value
        }
    private val background: NinePatchDrawable = NinePatchDrawable(
        NinePatch(
            assetManager.get(
                "sprites/progress_bar_background.9.png",
                Texture::class.java
            ), 6, 6, 6, 6
        )
    )
    private val foreground: NinePatchDrawable = NinePatchDrawable(
        NinePatch(
            assetManager.get(
                "sprites/progress_bar_foreground.9.png",
                Texture::class.java
            ), 6, 6, 6, 6
        )
    )

    init {
        require(maxValue > 0) { "max value must be positive" }
        require(currentValue <= maxValue) { "initialValue can't be greater tha max" }
        when(orientation) {
            BarOrientation.VERTICAL -> {
                width = 20f
            }
            BarOrientation.HORIZONTAL -> {
                height = 20f
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        background.draw(batch, x, y, width, height)
        repeat(currentValue) { index ->

            when(orientation) {
                BarOrientation.VERTICAL -> {
                    foreground.draw(
                        batch,
                        x + BORDER_WIDTH,
                        y + BORDER_WIDTH + (fractionSize + BORDER_WIDTH) * index,
                        width - 2 * BORDER_WIDTH,
                        fractionSize
                    )
                }
                BarOrientation.HORIZONTAL -> {
                    foreground.draw(
                        batch,
                        x + BORDER_WIDTH + (fractionSize + BORDER_WIDTH) * index,
                        y + BORDER_WIDTH,
                        fractionSize,
                        height - 2 * BORDER_WIDTH
                    )
                }
            }
        }

    }

    private val fractionSize: Float
        get() = (width - BORDER_WIDTH * (currentValue + 1)) / maxValue

    companion object {
        private const val BORDER_WIDTH = 4f
    }
}


