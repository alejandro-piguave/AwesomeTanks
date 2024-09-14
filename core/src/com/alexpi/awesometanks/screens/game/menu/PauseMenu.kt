package com.alexpi.awesometanks.screens.game.menu

import com.alexpi.awesometanks.screens.widget.Styles
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import ktx.actors.onClick

class PauseMenu(assetManager: AssetManager, onBackClick: () -> Unit, onResumeClick: () -> Unit): Table() {

    private val background = NinePatchDrawable(NinePatch(assetManager.get("sprites/progress_bar_background.9.png", Texture::class.java), 6, 6, 6, 6))

    init {
        setFillParent(true)
        val innerTable = Table()
        val back = TextButton("Back", Styles.getTextButtonStyle1(assetManager))
        back.onClick { onBackClick() }
        val resume = TextButton("Resume", Styles.getTextButtonStyle1(assetManager))
        resume.onClick {
            this@PauseMenu.remove()
            onResumeClick()
        }
        innerTable.add(back).fillX().spaceBottom(10f).row()
        innerTable.add(resume).fillX()
        innerTable.pad(30f)

        touchable = Touchable.enabled
        onClick { }
        innerTable.background(background)
        add(innerTable).expand().center()
    }
}