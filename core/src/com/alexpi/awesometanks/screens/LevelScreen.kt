package com.alexpi.awesometanks.screens

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Styles
import com.alexpi.awesometanks.widget.GameButton
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Created by Alex on 25/01/2016.
 */
class LevelScreen(game: MainGame?) : BaseScreen(game) {
    private lateinit var stage: Stage
    private lateinit var batch: SpriteBatch
    private lateinit var background: Texture
    override fun show() {
        stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
        batch = SpriteBatch()
        background = game.manager.get("sprites/background.png", Texture::class.java)
        val table = Table()
        table.setFillParent(true)
        val lockedLevelLabel =
            Label("Locked Level", Styles.getLabelStyle(game.manager, Constants.TILE_SIZE.toInt()))
        lockedLevelLabel.color = Color.RED
        lockedLevelLabel.setPosition(stage.width / 2, stage.height / 2, Align.center)
        lockedLevelLabel.setSize(0f, 0f)
        lockedLevelLabel.addAction(Actions.alpha(0f))
        val levelTable = Table()
        for (i in 0 until Constants.LEVEL_COUNT) {
            val isLevelUnlocked = game.gameValues.getBoolean("unlocked$i") || i == 0
            val levelButton = GameButton(game.manager, {
                if (isLevelUnlocked) stage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(Constants.TRANSITION_DURATION), Actions.run {
                            game.screen = GameScreen(game, i)
                        }
                    )
                )
            }, (i + 1).toString())
            if(!isLevelUnlocked){
                levelButton.color = Color.GRAY
                levelButton.touchable = Touchable.disabled
            }
            val gameButtonCell = levelTable.add(levelButton).size(80f).pad(16f)
            if ((i + 1) % LEVEL_TABLE_COLUMN_COUNT == 0) gameButtonCell.row()
        }

        val backButton = GameButton(game.manager, {
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run {
                game.screen = game.upgradesScreen
            }))
        }, "Back")
        table.add(Label("Select level", Styles.getLabelStyleBackground(game.manager))).padTop(32f).row()
        table.add(levelTable).expandX().fillX().padTop(64f).row()
        table.add(backButton).size(Constants.TILE_SIZE * 3, Constants.TILE_SIZE).padTop(12f).expandY().top().row()
        stage.addActor(table)
        stage.addActor(lockedLevelLabel)
        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
        stage.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(Constants.TRANSITION_DURATION)
            )
        )
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
        batch.dispose()
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        batch.begin()
        batch.draw(background, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()
        stage.act()
        stage.draw()
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run {
                game.screen = game.upgradesScreen
            }))
        }
    }

    companion object {
        private const val LEVEL_TABLE_COLUMN_COUNT = 10
    }
}