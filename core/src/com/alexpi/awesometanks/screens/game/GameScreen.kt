package com.alexpi.awesometanks.screens.game


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.screens.BaseScreen
import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.SCREEN_WIDTH
import com.alexpi.awesometanks.screens.TRANSITION_DURATION
import com.alexpi.awesometanks.screens.game.menu.LevelCompletedMenu
import com.alexpi.awesometanks.screens.game.menu.LevelFailedMenu
import com.alexpi.awesometanks.screens.game.menu.PauseMenu
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.widget.GameButton
import com.alexpi.awesometanks.widget.Styles
import com.alexpi.awesometanks.world.GameRenderer
import com.alexpi.awesometanks.world.Settings
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.alpha
import ktx.actors.onClick
import kotlin.math.abs

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor, GameRenderer.GameListener {
    private val uiStage: Stage = Stage(ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT))
    private val weaponMenu = WeaponMenu(game.manager, Weapon.Type.values().indices.map { game.gameValues.getBoolean("isWeaponAvailable$it",false) })
    private val gunChangeSound: Sound = game.manager.get("sounds/gun_change.ogg")
    private lateinit var gameRenderer: GameRenderer
    private lateinit var ammoBar: AmmoBar
    private val pauseButton: GameButton = GameButton(game.manager, {
        showPauseMenu()
    }, "Pause")


    private val pauseMenu: Table = PauseMenu(game.manager, {
        gameRenderer.isPaused = false
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = game.levelScreen }))
    }, {
        gameRenderer.isPaused = false
        pauseButton.isVisible = true
    })

    private val levelFailedMenu = LevelFailedMenu(game.manager) {
        saveProgress()
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = game.upgradesScreen }))
    }

    private val levelCompletedMenu = LevelCompletedMenu(game.manager) {
        uiStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run { game.screen = game.levelScreen }))
    }

    override fun show() {
        gameRenderer = GameRenderer(game, this, level)
        ammoBar = AmmoBar(game.manager,gameRenderer.player)
        createUIScene()
    }

    override fun resize(width: Int, height: Int) {
        uiStage.viewport.update(width, height, true)
        gameRenderer.updateViewport(width, height)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        gameRenderer.render(delta)
        uiStage.act(delta)
        uiStage.draw()
    }

    private fun createUIScene() {
        val uiTable = Table()
        uiTable.setFillParent(true)
        ammoBar.setSize(300f, 30f)
        ammoBar.isVisible = false

        val money = ProfitLabel(game.manager, gameRenderer.player)

        val joystickSize = SCREEN_HEIGHT / 2.25f
        val movementTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager)).apply {
            alpha = .5f
            addListener {
                if(isTouched) gameRenderer.onKnobTouch(knobPercentX, knobPercentY) else false

            }
        }
        val aimTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager)).apply {
            alpha = .5f
            addListener {
                val x = knobPercentX
                val y = knobPercentY
                if (isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                    gameRenderer.setRotationInput(x,y)
                    val distanceFromCenter = Utils.fastHypot(x.toDouble(), y.toDouble()).toFloat()
                    gameRenderer.player.isShooting = distanceFromCenter > 0.95f && !gameRenderer.isLevelCompleted
                } else gameRenderer.player.isShooting = false
                true
            }
        }

        val weaponMenuButton = ImageButton(TextureRegionDrawable(game.manager.get("sprites/gun_menu_icon.png",Texture::class.java))).apply {
            alpha = .5f
            onClick {
                gameRenderer.isPaused = if(!gameRenderer.isPaused){
                    uiStage.addActor(weaponMenu)
                    true
                } else {
                    weaponMenu.remove()
                    false
                }
            }
        }

        weaponMenu.onWeaponClick = this::onWeaponUpdated

        val uiTopTable = Table()
        val uiBottomTable = Table()
        uiTopTable.add(ammoBar).expandX().uniformX().pad(10f).apply {
            if(Gdx.graphics.safeInsetLeft > 0)
                padLeft(Gdx.graphics.safeInsetLeft.toFloat())
        }

        uiTopTable.add(money).expandX().uniformX().pad(10f)
        uiTopTable.add(pauseButton).size(140f, 64f).expandX().uniformX().right().pad(10f).apply {
            if(Gdx.graphics.safeInsetRight > 0)
                padRight(Gdx.graphics.safeInsetRight.toFloat())
        }.row()
        uiTable.add(uiTopTable).growX().row()
        uiTable.add().expand().row()
        if(Gdx.app.type != Application.ApplicationType.Desktop){
            uiBottomTable.add(movementTouchpad).size(joystickSize).left().pad(10f).apply {
                if(Gdx.graphics.safeInsetLeft > 0)
                    padLeft(Gdx.graphics.safeInsetLeft.toFloat())
            }
            uiBottomTable.add(weaponMenuButton).expandX().right()
            uiBottomTable.add(aimTouchpad).size(joystickSize).right().apply {
                if(Gdx.graphics.safeInsetRight > 0)
                    padRight(Gdx.graphics.safeInsetRight.toFloat())
            }.row()

            weaponMenuButton.setFillParent(true)
        } else {
            uiBottomTable.add(weaponMenu).expandX().center().row()
        }
        uiTable.add(uiBottomTable).growX().row()
        uiStage.addActor(uiTable)

        Gdx.input.inputProcessor = createInputProcessor()
        Gdx.input.isCatchBackKey = true
    }

    private fun onWeaponUpdated(index: Int){
        if (Settings.soundsOn) gunChangeSound.play()
        gameRenderer.player.currentWeaponIndex = index

        ammoBar.isVisible = index != 0
    }

    private fun createInputProcessor(): InputProcessor {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(uiStage)
        multiplexer.addProcessor(this)
        return multiplexer
    }

    private fun saveProgress(unlockNextLevel: Boolean = false) {
        if (unlockNextLevel) game.gameValues.putBoolean("unlocked" + (level+1), true)
        gameRenderer.player.saveProgress(game.gameValues)
        val savedMoney = game.gameValues.getInteger("money")
        game.gameValues.putInteger("money",savedMoney + gameRenderer.player.money)
        game.gameValues.flush()
    }

    private fun showLevelFailedMenu() {
        uiStage.addActor(levelFailedMenu)
        pauseButton.isVisible = false
    }

    private fun showLevelCompletedMenu() {
        uiStage.addActor(levelCompletedMenu)
        saveProgress(true)
        pauseButton.isVisible = false
    }

    private fun showPauseMenu(){
        gameRenderer.isPaused = true
        uiStage.addActor(pauseMenu)
        pauseButton.isVisible = false

    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.BACK, Input.Keys.ESCAPE -> {
                if(Gdx.app.type != Application.ApplicationType.Desktop && weaponMenu.parent != null){
                    weaponMenu.remove()
                    gameRenderer.isPaused = false
                } else if(!gameRenderer.isLevelCompleted){
                    showPauseMenu()
                }
                return true
            }
            //EVENTS FOR DESKTOP
            Input.Keys.NUM_1 -> {
                weaponMenu.selectWeapon(0)
                return true
            }
            Input.Keys.NUM_2 -> {
                weaponMenu.selectWeapon(1)
                return true
            }
            Input.Keys.NUM_3 -> {
                weaponMenu.selectWeapon(2)
                return true
            }
            Input.Keys.NUM_4 -> {
                weaponMenu.selectWeapon(3)
                return true
            }
            Input.Keys.NUM_5 -> {
                weaponMenu.selectWeapon(4)
                return true
            }
            Input.Keys.NUM_6 -> {
                weaponMenu.selectWeapon(5)
                return true
            }
            Input.Keys.NUM_7 -> {
                weaponMenu.selectWeapon(6)
                return true
            }
            Input.Keys.NUM_8 -> {
                weaponMenu.selectWeapon(7)
                return true
            }
             else -> return gameRenderer.onKeyDown(keycode)
        }
    }

    override fun hide() {
        uiStage.dispose()
        gameRenderer.dispose()
        Gdx.input.inputProcessor = null
    }

    //EVENTS FOR DESKTOP
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameRenderer.player.isShooting = !gameRenderer.isLevelCompleted
            return true
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameRenderer.setRotationInput(
                screenX - Gdx.graphics.width*.5f,
                (Gdx.graphics.height - screenY) - Gdx.graphics.height*.5f
            )
            return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            gameRenderer.player.isShooting = false
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean = gameRenderer.onKeyUp(keycode)

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        gameRenderer.setRotationInput(
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