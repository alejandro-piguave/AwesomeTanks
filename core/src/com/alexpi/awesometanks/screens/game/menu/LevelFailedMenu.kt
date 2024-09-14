package com.alexpi.awesometanks.screens.game.menu

import com.alexpi.awesometanks.screens.widget.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class LevelFailedMenu(assetManager: AssetManager, onContinueClick: () -> Unit): Table() {

    init {
        setFillParent(true)
        val continueButton = TextButton(
            "Menu",
            Styles.getTextButtonStyle1(assetManager)
        )
        continueButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                onContinueClick()
            }
        })

        add(continueButton).expand().top().right().pad(24f)

    }
}