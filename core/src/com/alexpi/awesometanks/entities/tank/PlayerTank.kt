package com.alexpi.awesometanks.entities.tank

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.experimental.or

class PlayerTank (
    manager: AssetManager,
    entityGroup: Group,
    world: World,
    position: Vector2,
    gameValues: Preferences,
    private val tankColor: Color,
    var money: Int,
    allowSounds: Boolean)
    : Tank(manager,entityGroup, world, position, .75f,
    .07f + gameValues.getInteger("rotation") / 40f,
    150 + gameValues.getInteger("speed") * 10f,
    Constants.CAT_PLAYER,
    (Constants.CAT_BLOCK or Constants.CAT_ITEM or Constants.CAT_ENEMY or Constants.CAT_ENEMY_BULLET),
    1200f + gameValues.getInteger("health") * 200, false, allowSounds){

    private var currentWeapon = 0
    private val weapons: List<Weapon> = (0..6).map {
        Weapon.getWeaponAt(
            it, manager, gameValues.getInteger("ammo$it"), gameValues.getInteger(
                "power$it"
            ), true, allowSounds)
    }
    @JvmField
    val visibilityRadius = 2f

    fun saveProgress(gameValues: Preferences) {
        for (i in weapons.indices) gameValues.putInteger("ammo$i", weapons[i].ammo)
    }
    val centerX: Float
    get() = x + width*.5f

    val centerY: Float
    get() = y + height*.5f

    override fun getCurrentWeapon(): Weapon  = weapons[currentWeapon]
}