package com.alexpi.awesometanks.widget

import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.badlogic.gdx.assets.AssetManager

class AmmoBar(assetManager: AssetManager, private val tank: PlayerTank): GameProgressBar(assetManager, 100f, tank.currentWeapon.ammo) {

    override fun act(delta: Float) {
        super.act(delta)
        value = tank.currentWeapon.ammo
    }
}