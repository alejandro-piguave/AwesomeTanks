package com.alexpi.awesometanks.screens.game.widget

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.widget.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label

class ProfitLabel(assetManager: AssetManager, profitValue: Int = 0): Label("$profitValue $",
    Styles.getLabelStyle(assetManager, (TILE_SIZE / 3).toInt())
) {
    var profit: Int = profitValue
        set(value) {
            field = value
            setText("$profit $")
        }
}