package com.alexpi.awesometanks.screens.game.menu

import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.actors.onClick

class WeaponMenu(assetManager: AssetManager): Table() {
    private val background = NinePatchDrawable(NinePatch(assetManager.get("sprites/progress_bar_background.9.png", Texture::class.java), 6, 6, 6, 6))

    private val buttons: List<ImageButton> = (0 until Weapon.Type.values().size).map {
        val texture = assetManager.get<Texture>("icons/icon_$it.png")
        val disabled = assetManager.get<Texture>("icons/icon_disabled_$it.png")
        val style = ImageButtonStyle(assetManager.get<Skin>("uiskin/uiskin.json").get(
            ButtonStyle::class.java))
        style.imageUp = TextureRegionDrawable(TextureRegion(texture))
        style.imageDisabled = TextureRegionDrawable(TextureRegion(disabled))
        ImageButton(style)
    }

    var buttonsEnabled: List<Boolean> = buttons.map { true }
        set(value) {
            field = value
            buttons.forEachIndexed { index, button ->
                button.setEnabled(buttonsEnabled[index], index)
            }
        }

    private var currentWeaponIndex = 0
    var onWeaponClick: ((Int) -> Unit)? = null

    init {
        buttons.forEachIndexed { index, button ->
            button.setEnabled(buttonsEnabled[index], index)
            button.setTransparency(currentWeaponIndex == index)
            add(button).width(96f).height(96f)
        }
        pad(10f)
        touchable = Touchable.enabled
        addListener { true }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        background.draw(batch, x, y, width, height)
        super.draw(batch, parentAlpha)
    }

    fun selectWeapon(index: Int) {
        if(index == currentWeaponIndex || buttons[index].isDisabled) return
        currentWeaponIndex = index
        updateButtons()
        onWeaponClick?.invoke(index)
    }

    private fun ImageButton.setTransparency(selected: Boolean) {
        setColor(color.r, color.g, color.b, if(selected) 1f else .5f)
    }

    private fun ImageButton.setEnabled(enabled: Boolean, index: Int) {
        isDisabled = !enabled
        if(enabled){
            onClick {
                currentWeaponIndex = index
                updateButtons()
                onWeaponClick?.invoke(index)
            }
        } else clearListeners()
    }

    private fun updateButtons() {
        buttons.forEachIndexed { index, button ->
            button.setTransparency(currentWeaponIndex == index)
        }
    }

}