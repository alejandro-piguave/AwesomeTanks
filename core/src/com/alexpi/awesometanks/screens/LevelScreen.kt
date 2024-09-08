package com.alexpi.awesometanks.screens

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.screens.game.GameScreen
import com.alexpi.awesometanks.widget.GameButton
import com.alexpi.awesometanks.widget.Styles
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Created by Alex on 25/01/2016.
 */
class LevelScreen(game: MainGame?) : BaseScreen(game) {
    private lateinit var stage: Stage
    private lateinit var background: Texture
    override fun show() {
        stage = Stage(ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT))
        background = game.manager.get("sprites/background.png", Texture::class.java)
        val table = Table()
        table.pad(32f)
        table.setFillParent(true)
        val levelTable = Table()
        addLevelButtons(levelTable)
        val backButton = GameButton(game.manager, "Back", {
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run {
                game.screen = game.upgradesScreen
            }))
        })
        table.add(Label("Select level", Styles.getLabelStyleBackground(game.manager))).row()
        table.add(levelTable).expand().fillX().row()
        table.add(backButton).size(TILE_SIZE * 3, TILE_SIZE).row()
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
        stage.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(TRANSITION_DURATION)
            )
        )
    }

    private fun addLevelButtons(table: Table) {
        for (i in 0 until LEVEL_COUNT) {
            val isLevelUnlocked = game.gameValues.getBoolean("unlocked$i") || i == 0
            val levelButton = GameButton(game.manager, (i + 1).toString(), {
                if (isLevelUnlocked) stage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(TRANSITION_DURATION), Actions.run {
                            game.screen = GameScreen(game, i + 1)
                        }
                    )
                )
            })
            if(!isLevelUnlocked){
                levelButton.color = Color.GRAY
                levelButton.touchable = Touchable.disabled
            }
            val gameButtonCell = table.add(levelButton).size(80f).pad(16f)
            if ((i + 1) % LEVEL_TABLE_COLUMN_COUNT == 0) gameButtonCell.row()
        }

    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

        stage.act()
        stage.batch.begin()
        stage.batch.draw(background, 0f, 0f, stage.width, stage.height)
        stage.batch.end()
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