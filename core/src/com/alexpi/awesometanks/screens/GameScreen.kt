package com.alexpi.awesometanks.screens


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Settings
import com.alexpi.awesometanks.utils.Styles
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.widget.AmmoBar
import com.alexpi.awesometanks.widget.GameButton
import com.alexpi.awesometanks.widget.ProfitLabel
import com.alexpi.awesometanks.world.GameListener
import com.alexpi.awesometanks.world.GameRenderer
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.alpha
import ktx.actors.onClick
import kotlin.math.abs

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor, GameListener {
    private val uiStage: Stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
    private val weaponMenuTable: Table = Table()
    private val gunChangeSound: Sound = game.manager.get("sounds/gun_change.ogg")
    private lateinit var gameRenderer: GameRenderer
    private lateinit var ammoBar: AmmoBar
    private lateinit var pauseButton: GameButton
    private val gunName = Label(Weapon.Type.MINIGUN.name, Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 4).toInt()))
    private val buttons: List<ImageButton> = (0 until Weapon.Type.values().size).map {
        val texture = game.manager.get<Texture>("icons/icon_$it.png")
        val disabled = game.manager.get<Texture>("icons/icon_disabled_$it.png")
        val style = ImageButtonStyle(game.manager.get<Skin>("uiskin/uiskin.json").get(ButtonStyle::class.java))
        style.imageUp = TextureRegionDrawable(TextureRegion(texture))
        style.imageDisabled = TextureRegionDrawable(TextureRegion(disabled))
        ImageButton(style)
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
        gunName.setAlignment(Align.center)
        ammoBar.setSize(300f, 30f)
        ammoBar.isVisible = false

        val money = ProfitLabel(game.manager, gameRenderer.player)

        val joystickSize = Constants.SCREEN_HEIGHT / 2.25f
        val movementTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager)).apply {
            alpha = .5f
            addListener {
                val x = knobPercentX
                val y = knobPercentY
                if (isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                    gameRenderer.player.setOrientation(x, y)
                    gameRenderer.player.isMoving = true
                } else gameRenderer.player.isMoving = false
                true
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
                    uiStage.addActor(weaponMenuTable)
                    true
                } else {
                    weaponMenuTable.remove()
                    false
                }
            }
        }

        pauseButton = GameButton(game.manager, {
                showPauseMenu()
        }, "Pause")


        buttons.forEach { button ->
            val index = buttons.indexOf(button)

            if(index > 0) {
                button.isDisabled = game.gameValues.getBoolean("weapon$index",true)
                button.setColor(button.color.r,button.color.g,button.color.b,.5f)
            }
            if(!button.isDisabled) {
                button.onClick {
                   changeGun(index)
                }
            }
            weaponMenuTable.add(button).width(96f).height(96f)
        }


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

            weaponMenuTable.setFillParent(true)
        } else {
            uiBottomTable.add(weaponMenuTable).expandX().center().row()
        }
        uiTable.add(uiBottomTable).growX().row()
        uiStage.addActor(uiTable)

        Gdx.input.inputProcessor = inputProcessor
        Gdx.input.isCatchBackKey = true

        Timer.schedule(object : Timer.Task() {
            override fun run() {
                gunName.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
            }
        }, 2f)
    }

    private fun changeGun(index: Int){
        if(index == gameRenderer.player.currentWeaponIndex || buttons[index].isDisabled) return

        if (Settings.soundsOn) gunChangeSound.play()
        gameRenderer.player.currentWeaponIndex = index
        buttons.forEach { ib ->
            ib.setColor(ib.color.r, ib.color.g, ib.color.b,
                if(gameRenderer.player.currentWeaponIndex == buttons.indexOf(ib)) 1f else .5f)
        }

        ammoBar.isVisible = index != 0

        val gunNameText = Weapon.Type.values()[gameRenderer.player.currentWeaponIndex].name
        gunName.setText(gunNameText)
        gunName.addAction(Actions.alpha(1f))
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                gunName.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
            }

        }, 2f)
    }

    private val inputProcessor: InputProcessor
        get() {
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

    private fun showLevelFailedButton() {
        val table = Table()
        table.setFillParent(true)
        val continueButton = TextButton(
            "Menu",
            Styles.getTextButtonStyle1(game.manager)
        )
        continueButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                saveProgress()
                uiStage.addAction(Actions.sequence(Actions.fadeOut(Constants.TRANSITION_DURATION), Actions.run { game.screen = game.upgradesScreen }))
            }
        })
        table.add(continueButton).expand().top().right().pad(24f)
        uiStage.addActor(table)

        pauseButton.isVisible = false

    }

    private fun showLevelCompletedButton() {
        val table = Table()
        table.setFillParent(true)
        val continueButton = TextButton(
            "Continue",
            Styles.getTextButtonStyle1(game.manager)
        )
        continueButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                uiStage.addAction(Actions.sequence(Actions.fadeOut(Constants.TRANSITION_DURATION), Actions.run { game.screen = game.levelScreen }))
            }
        })
        table.add(continueButton).expand().top().right().pad(24f)
        uiStage.addActor(table)
        saveProgress(true)

        pauseButton.isVisible = false
    }

    private fun showPauseMenu(){
        gameRenderer.isPaused = true

        val table = Table()
        table.setFillParent(true)
        val back = TextButton("Back", Styles.getTextButtonStyle1(game.manager))
        back.onClick {
            gameRenderer.isPaused = false
            uiStage.addAction(Actions.sequence(Actions.fadeOut(Constants.TRANSITION_DURATION), Actions.run { game.screen = game.levelScreen }))
        }
        val resume = TextButton("Resume", Styles.getTextButtonStyle1(game.manager))
        resume.onClick {
            gameRenderer.isPaused = false
            table.remove()
            pauseButton.isVisible = true
        }
        table.add(back).top().right().pad(24f)
        table.add(resume).top().right().pad(24f)

        uiStage.addActor(table)
        pauseButton.isVisible = false

    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.BACK, Input.Keys.ESCAPE -> {
                if(Gdx.app.type != Application.ApplicationType.Desktop && weaponMenuTable.parent != null){
                    weaponMenuTable.remove()
                    gameRenderer.isPaused = false
                } else if(!gameRenderer.isLevelCompleted){
                    showPauseMenu()
                }
                return true
            }
            //EVENTS FOR DESKTOP
            Input.Keys.NUM_1 -> {
                changeGun(0)
                return true
            }
            Input.Keys.NUM_2 -> {
                changeGun(1)
                return true
            }
            Input.Keys.NUM_3 -> {
                changeGun(2)
                return true
            }
            Input.Keys.NUM_4 -> {
                changeGun(3)
                return true
            }
            Input.Keys.NUM_5 -> {
                changeGun(4)
                return true
            }
            Input.Keys.NUM_6 -> {
                changeGun(5)
                return true
            }
            Input.Keys.NUM_7 -> {
                changeGun(6)
                return true
            }
            Input.Keys.NUM_8 -> {
                changeGun(7)
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

    override fun keyUp(keycode: Int): Boolean {
        return gameRenderer.onKeyUp(keycode)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        gameRenderer.setRotationInput(
            screenX - Gdx.graphics.width*.5f,
            (Gdx.graphics.height - screenY) - Gdx.graphics.height*.5f
        )
        return true
    }

    override fun keyTyped(character: Char): Boolean = false

    override fun scrolled(amountX: Float, amountY: Float): Boolean = false
    override fun onLevelFailed() {
        showLevelFailedButton()
    }

    override fun onLevelCompleted() {
        showLevelCompletedButton()
    }

}