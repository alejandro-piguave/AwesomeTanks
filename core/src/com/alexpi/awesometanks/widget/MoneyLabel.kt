package com.alexpi.awesometanks.widget

import com.alexpi.awesometanks.screens.MoneyValue
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label

class MoneyLabel(assetManager: AssetManager, private val moneyValue: MoneyValue):
    Label("\$${moneyValue.money}",
        Styles.getLabelStyleBackground(assetManager)
) {
    override fun act(delta: Float) {
        super.act(delta)
        setText("\$${moneyValue.money}")
    }
}