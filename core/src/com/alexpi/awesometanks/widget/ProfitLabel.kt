package com.alexpi.awesometanks.widget

import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label

class ProfitLabel(assetManager: AssetManager, private val tank: PlayerTank): Label("${tank.money} $",
    Styles.getLabelStyle(assetManager, (Constants.TILE_SIZE / 3).toInt())
) {
    override fun act(delta: Float) {
        super.act(delta)
        setText("${tank.money} $")
    }
}