package com.alexpi.awesometanks.screens.game

import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.widget.GameProgressBar
import com.badlogic.gdx.assets.AssetManager

class AmmoBar(assetManager: AssetManager, private val tank: Player): GameProgressBar(assetManager, 100f, tank.currentWeapon.ammo) {

    override fun act(delta: Float) {
        super.act(delta)
        value = tank.currentWeapon.ammo
    }
}