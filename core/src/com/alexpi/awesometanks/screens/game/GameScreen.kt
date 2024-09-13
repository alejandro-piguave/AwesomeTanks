package com.alexpi.awesometanks.screens.game


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.screens.BaseScreen
import com.alexpi.awesometanks.screens.LevelScreen
import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.SCREEN_WIDTH
import com.alexpi.awesometanks.screens.TRANSITION_DURATION
import com.alexpi.awesometanks.screens.game.menu.LevelCompletedMenu
import com.alexpi.awesometanks.screens.game.menu.LevelFailedMenu
import com.alexpi.awesometanks.screens.game.menu.PauseMenu
import com.alexpi.awesometanks.screens.game.stage.GameUIStage
import com.alexpi.awesometanks.screens.upgrades.UpgradesScreen
import com.alexpi.awesometanks.utils.fastHypot
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.GameContainer
import com.alexpi.awesometanks.world.Settings
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import kotlin.math.abs

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor, GameContainer.GameListener {
    private lateinit var uiStage: GameUIStage
    private lateinit var gameContainer: GameContainer
    private val gunChangeSound: Sound = game.manager.get("sounds/gun_change.ogg")

    private val pauseMenu: Table = PauseMenu(game.manager, {
        gameContainer.isPaused = false
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = LevelScreen(game) }))
    }, {
        gameContainer.isPaused = false
        uiStage.pauseButton.isVisible = true
    })

    private val levelFailedMenu = LevelFailedMenu(game.manager) {
        saveProgress()
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = UpgradesScreen(game) }))
    }

    private val levelCompletedMenu = LevelCompletedMenu(game.manager) {
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = LevelScreen(game) }))
    }

    override fun show() {
        gameContainer = GameContainer(game, this, level)
        gameContainer.player.onMoneyUpdated = { money ->
            uiStage.money.profit = money
        }
        gameContainer.player.onWeaponAmmoUpdated = { ammo ->
            uiStage.ammoBar.value = ammo
        }
        createUIStage()
        Gdx.input.inputProcessor = createInputProcessor()
        Gdx.input.isCatchBackKey = true
    }

    private fun createUIStage() {
        uiStage = GameUIStage(ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT), game.manager)
        uiStage.pauseButton.onClickListener = { showPauseMenu() }
        uiStage.weaponMenu.buttonsEnabled =  Weapon.Type.values().indices.map { it == 0 || game.gameValues.getBoolean("isWeaponAvailable$it",false) }
        uiStage.weaponMenu.onWeaponClick = this::onWeaponUpdated
        uiStage.onWeaponMenuButtonClick = { gameContainer.isPaused = !gameContainer.isPaused }
        uiStage.onMovementKnobTouch = { isTouched, knobPercentX, knobPercentY ->
            if(isTouched) gameContainer.onKnobTouch(knobPercentX, knobPercentY) else false
        }
        uiStage.onAimKnobTouch = { isTouched, knobPercentX, knobPercentY ->
            if (isTouched && (abs(knobPercentX) > .2f || abs(knobPercentY) > .2f)) {
                gameContainer.setRotationInput(knobPercentY,knobPercentY)
                val distanceFromCenter = fastHypot(knobPercentY.toDouble(), knobPercentY.toDouble()).toFloat()
                gameContainer.player.isShooting = distanceFromCenter > 0.95f && !gameContainer.isLevelCompleted
            } else gameContainer.player.isShooting = false
            true
        }
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        gameContainer.updateViewport(width, height)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        gameContainer.render(delta)
        uiStage.act(delta)
        uiStage.draw()
    }

    private fun onWeaponUpdated(index: Int){
        if (Settings.soundsOn) gunChangeSound.play()
        gameContainer.player.currentWeaponIndex = index
        uiStage.ammoBar.isVisible = index != 0
    }

    private fun createInputProcessor(): InputProcessor {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(uiStage)
        multiplexer.addProcessor(this)
        return multiplexer
    }

    private fun saveProgress(unlockNextLevel: Boolean = false) {
        if (unlockNextLevel) game.gameValues.putBoolean("unlocked" + (level+1), true)
        gameContainer.player.saveProgress(game.gameValues)
        val savedMoney = game.gameValues.getInteger("money")
        game.gameValues.putInteger("money",savedMoney + gameContainer.player.money)
        game.gameValues.flush()
    }

    private fun showLevelFailedMenu() {
        uiStage.addActor(levelFailedMenu)
        uiStage.pauseButton.isVisible = false
    }

    private fun showLevelCompletedMenu() {
        uiStage.addActor(levelCompletedMenu)
        saveProgress(true)
        uiStage.pauseButton.isVisible = false
    }

    private fun showPauseMenu(){
        gameContainer.isPaused = true
        uiStage.addActor(pauseMenu)
        uiStage.pauseButton.isVisible = false

    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.BACK, Input.Keys.ESCAPE -> {
                if(Gdx.app.type != Application.ApplicationType.Desktop && uiStage.weaponMenu.parent != null){
                    uiStage.weaponMenu.remove()
                    gameContainer.isPaused = false
                } else if(!gameContainer.isLevelCompleted){
                    showPauseMenu()
                }
                return true
            }
            //EVENTS FOR DESKTOP
            Input.Keys.NUM_1 -> {
                uiStage.weaponMenu.selectWeapon(0)
                return true
            }
            Input.Keys.NUM_2 -> {
                uiStage.weaponMenu.selectWeapon(1)
                return true
            }
            Input.Keys.NUM_3 -> {
                uiStage.weaponMenu.selectWeapon(2)
                return true
            }
            Input.Keys.NUM_4 -> {
                uiStage.weaponMenu.selectWeapon(3)
                return true
            }
            Input.Keys.NUM_5 -> {
                uiStage.weaponMenu.selectWeapon(4)
                return true
            }
            Input.Keys.NUM_6 -> {
                uiStage.weaponMenu.selectWeapon(5)
                return true
            }
            Input.Keys.NUM_7 -> {
                uiStage.weaponMenu.selectWeapon(6)
                return true
            }
            Input.Keys.NUM_8 -> {
                uiStage.weaponMenu.selectWeapon(7)
                return true
            }
             else -> return gameContainer.onKeyDown(keycode)
        }
    }

    override fun hide() {
        uiStage.dispose()
        gameContainer.dispose()
        Gdx.input.inputProcessor = null
    }

    //EVENTS FOR DESKTOP
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameContainer.player.isShooting = !gameContainer.isLevelCompleted
            return true
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameContainer.setRotationInput(
                screenX - Gdx.graphics.width*.5f,
                (Gdx.graphics.height - screenY) - Gdx.graphics.height*.5f
            )
            return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameContainer.player.isShooting = false
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean = gameContainer.onKeyUp(keycode)

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        gameContainer.setRotationInput(
            screenX - Gdx.graphics.width*.5f,
            (Gdx.graphics.height - screenY) - Gdx.graphics.height*.5f
        )
        return true
    }

    override fun keyTyped(character: Char): Boolean = false
    override fun scrolled(amountX: Float, amountY: Float): Boolean = false
    override fun onLevelFailed() { showLevelFailedMenu() }
    override fun onLevelCompleted() { showLevelCompletedMenu() }

}