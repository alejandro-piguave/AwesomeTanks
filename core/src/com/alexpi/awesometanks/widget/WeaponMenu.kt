package com.alexpi.awesometanks.widget

import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.actors.onClick

class WeaponMenu(manager: AssetManager, weaponsEnabled: List<Boolean>): Table() {
    private val buttons: List<ImageButton> = (0 until Weapon.Type.values().size).map {
        val texture = manager.get<Texture>("icons/icon_$it.png")
        val disabled = manager.get<Texture>("icons/icon_disabled_$it.png")
        val style = ImageButtonStyle(manager.get<Skin>("uiskin/uiskin.json").get(
            ButtonStyle::class.java))
        style.imageUp = TextureRegionDrawable(TextureRegion(texture))
        style.imageDisabled = TextureRegionDrawable(TextureRegion(disabled))
        ImageButton(style)
    }

    private var currentWeaponIndex = 0
    var onWeaponClick: ((Int) -> Unit)? = null

    init {
        buttons.forEach { button ->
            val index = buttons.indexOf(button)

            if(index > 0) {
                button.isDisabled = !weaponsEnabled[index]
                button.setColor(button.color.r,button.color.g,button.color.b,.5f)
            }
            if(!button.isDisabled) {
                button.onClick {
                    updateButtons()
                    onWeaponClick?.invoke(index)
                }
            }
            add(button).width(96f).height(96f)
        }

    }

    private fun updateButtons() {
        buttons.forEach { ib ->
            ib.setColor(ib.color.r, ib.color.g, ib.color.b,
                if(currentWeaponIndex == buttons.indexOf(ib)) 1f else .5f)
        }
    }


    fun selectWeapon(index: Int) {
        if(index == currentWeaponIndex || buttons[index].isDisabled) return
        currentWeaponIndex = index
        updateButtons()
        onWeaponClick?.invoke(index)
    }
}