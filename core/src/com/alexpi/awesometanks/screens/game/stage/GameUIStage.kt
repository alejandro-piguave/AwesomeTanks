package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.game.menu.WeaponMenu
import com.alexpi.awesometanks.screens.game.widget.AmmoBar
import com.alexpi.awesometanks.screens.game.widget.ProfitLabel
import com.alexpi.awesometanks.widget.GameButton
import com.alexpi.awesometanks.widget.Styles
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.alpha
import ktx.actors.onClick

class GameUIStage(viewport: Viewport, assetManager: AssetManager): Stage(viewport) {
    val ammoBar: AmmoBar = AmmoBar(assetManager)
    val money = ProfitLabel(assetManager)
    val weaponMenu = WeaponMenu(assetManager,)
    val pauseButton: GameButton = GameButton(assetManager, "Pause")

    var onWeaponMenuButtonClick: (() -> Unit)?=null

    var onMovementKnobTouch: ((Boolean, Float, Float) -> Boolean)?= null
    var onAimKnobTouch: ((Boolean, Float, Float) -> Boolean)?= null

    init {
        val uiTable = Table()
        uiTable.setFillParent(true)
        ammoBar.setSize(300f, 30f)
        ammoBar.isVisible = false

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
            val joystickSize = SCREEN_HEIGHT / 2.25f
            val movementTouchpad = Touchpad(0f, Styles.getTouchPadStyle(assetManager)).apply {
                alpha = .5f
                addListener { onMovementKnobTouch?.invoke(isTouched, knobPercentX, knobPercentY) ?: false }
            }
            val aimTouchpad = Touchpad(0f, Styles.getTouchPadStyle(assetManager)).apply {
                alpha = .5f
                addListener { onAimKnobTouch?.invoke(isTouched, knobPercentX, knobPercentY) ?: false}
            }

            val weaponMenuButton = ImageButton(
                TextureRegionDrawable(assetManager.get("sprites/gun_menu_icon.png",
                    Texture::class.java))
            ).apply {
                alpha = .5f
                onClick {
                    if(weaponMenu.hasParent()) weaponMenu.remove()
                    else addActor(weaponMenu)
                    onWeaponMenuButtonClick?.invoke()
                }
            }

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

        addActor(uiTable)
    }

}