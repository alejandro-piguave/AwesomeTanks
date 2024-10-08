package com.alexpi.awesometanks.screens.upgrades

import com.alexpi.awesometanks.screens.widget.FloatProgressBar
import com.alexpi.awesometanks.screens.widget.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick

class UpgradeTable(
    assetManager: AssetManager,
    upgradeName: String,
    currentLevel: Float,
    maxLevel: Float,
    upgradePrice: Int,
    vertical: Boolean = false
) : Table() {
    private val bar: FloatProgressBar
    private val nameLabel = Label(upgradeName, Styles.getLabelStyleSmall(assetManager))
    private val buyButton: TextButton

    var upgradePrice: Int = upgradePrice
        set(value) {
            field = value
            buyButton.setText("Buy $value $")
        }

    var onBuyClick: (() -> Unit)? = null

    init {
        nameLabel.setAlignment(Align.center)
        buyButton = TextButton("Buy $upgradePrice $", Styles.getTextButtonStyleSmall(assetManager))
        bar = FloatProgressBar(
            assetManager,
            maxLevel,
            0f,
            vertical
        )
        bar.currentValue = currentLevel
        if(bar.currentValue == maxLevel) buyButton.isVisible = false
        val barCell = add(bar)
        if (vertical) barCell.expandY().fillY()
        else barCell.fillX()
        barCell.row()
        add(nameLabel).padTop(8f).padBottom(8f).row()
        add(buyButton).row()

        buyButton.onClick {
            onBuyClick?.invoke()
        }
    }

    fun canBuy(money: Int): Boolean {
        return (money >= upgradePrice && bar.currentValue < bar.maxValue)
    }

    override fun getName(): String {
        return nameLabel.text.toString()
    }

    var upgradeLevel: Float
        get() = bar.currentValue
        set(value) {
            if(value > bar.maxValue) {
                bar.currentValue = bar.maxValue
                buyButton.isVisible = false
            } else {
                bar.currentValue = value
                buyButton.isVisible = true
            }
        }

    val isMaxLevel: Boolean
        get() = bar.currentValue == bar.maxValue

}
