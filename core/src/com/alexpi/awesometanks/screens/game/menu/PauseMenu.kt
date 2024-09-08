package com.alexpi.awesometanks.screens.game.menu

import com.alexpi.awesometanks.widget.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick

class PauseMenu(assetManager: AssetManager, onBackClick: () -> Unit, onResumeClick: () -> Unit): Table() {
    init {
        setFillParent(true)
        val back = TextButton("Back", Styles.getTextButtonStyle1(assetManager))
        back.onClick { onBackClick() }
        val resume = TextButton("Resume", Styles.getTextButtonStyle1(assetManager))
        resume.onClick {
            remove()
            onResumeClick()
        }
        add(back).fillX().spaceBottom(10f).row()
        add(resume).fillX()

    }
}