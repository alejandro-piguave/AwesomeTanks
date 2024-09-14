package com.alexpi.awesometanks.screens.widget

import com.alexpi.awesometanks.game.module.Settings.soundsOn
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class GameButton(assetManager: AssetManager, text: String = "", var onClickListener: (() -> Unit)? = null) :
    TextButton(text, Styles.getTextButtonStyle1(assetManager)) {

    init {
        val clickSoundDown = assetManager.get("sounds/click_down.ogg", Sound::class.java)
        val clickSoundUp = assetManager.get("sounds/click_down.ogg", Sound::class.java)


        addListener(object : ClickListener() {
            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                if (soundsOn) clickSoundDown.play()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (soundsOn) clickSoundUp.play()
                onClickListener?.invoke()
            }
        })
    }
}
