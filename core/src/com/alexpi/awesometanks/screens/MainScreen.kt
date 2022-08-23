package com.alexpi.awesometanks.screens

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Settings
import com.alexpi.awesometanks.utils.Styles
import com.alexpi.awesometanks.widget.GameButton
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.actors.onClick

/**
 * Created by Alex on 09/01/2016.
 */
class MainScreen(game: MainGame) : BaseScreen(game) {
    private lateinit var stage: Stage
    override fun show() {
        stage =  Stage(FillViewport(
            Constants.SCREEN_WIDTH,
            Constants.SCREEN_HEIGHT
        ))
        val background = Image(game.manager.get("sprites/background.png", Texture::class.java))
        background.setBounds(0f, 0f, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)
        val table = Table()
        val title1 = Label("Awesome", Styles.getGameTitleStyle1(game.manager))
        val title2 = Label("Tanks", Styles.getGameTitleStyle2(game.manager))
        title1.setAlignment(Align.center)
        title2.setAlignment(Align.center)
        val playButton = GameButton(game.manager, {
            stage.addAction(
                Actions.sequence(
                    Actions.fadeOut(Constants.TRANSITION_DURATION),
                    Actions.run { game.screen = game.upgrades }
                )
            )
        }, "Play")
        val soundButton = ImageButton(TextureRegionDrawable(game.manager.get<Texture>(
            if(Settings.soundsOn)"sprites/sound_on.png" else "sprites/sound_off.png")))
        soundButton.setPosition(Constants.SCREEN_WIDTH - 92f, Constants.SCREEN_HEIGHT - 92f)
        soundButton.onClick {
            if (Settings.soundsOn){
                game.gameSettings.putBoolean("areSoundsActivated",false)
                Settings.soundsOn = false
                soundButton.style.imageUp = TextureRegionDrawable(game.manager.get<Texture>("sprites/sound_off.png"))
            }else{
                game.gameSettings.putBoolean("areSoundsActivated",true)
                Settings.soundsOn = true
                soundButton.style.imageUp = TextureRegionDrawable(game.manager.get<Texture>("sprites/sound_on.png"))
            }
        }
        table.setFillParent(true)
        table.center()
        table.add(title1).width(Constants.TILE_SIZE * 8).row()
        table.add(title2).width(Constants.TILE_SIZE * 8).padBottom(Constants.TILE_SIZE / 3).row()
        table.add(playButton).width(Constants.TILE_SIZE * 4f).height(Constants.TILE_SIZE*1.25f).pad(
            Constants.TILE_SIZE / 3
        )
        table.row()
        stage.addActor(background)
        stage.addActor(table)
        stage.addActor(soundButton)
        Gdx.input.inputProcessor = stage
        stage.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(Constants.TRANSITION_DURATION)
            )
        )
        Gdx.input.isCatchBackKey = false
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        stage.act()
        stage.draw()
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        stage.dispose()
    }
}