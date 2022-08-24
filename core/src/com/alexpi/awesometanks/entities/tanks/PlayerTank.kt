package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.GameMap
import com.alexpi.awesometanks.utils.Settings
import com.alexpi.awesometanks.utils.Utils
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
    world: World,
    gameValues: Preferences,
    private val map: GameMap)
    : Tank(manager, world, Vector2(-1f,-1f), .75f,
    .07f + gameValues.getInteger(Constants.ROTATION_SPEED) / 40f,
    150 + gameValues.getInteger(Constants.MOVEMENT_SPEED) * 10f,
    Constants.CAT_PLAYER,
    (Constants.CAT_BLOCK or Constants.CAT_ITEM or Constants.CAT_ENEMY or Constants.CAT_ENEMY_BULLET),
    600f, false, null, Color.WHITE){

    private val visibilityRadius = 2 + gameValues.getInteger(Constants.VISIBILITY)
    private val armor = gameValues.getInteger(Constants.ARMOR)
    init {
        map.visualRange = visibilityRadius
    }

    override fun takeDamage(damage: Float) {
        super.takeDamage(damage * (1f - armor*.1f))
    }

    fun setPos(row: Int, col: Int){
        body.setTransform(map.toWorldPos(row, col).add(.5f,.5f), body.angle)
    }

    var money: Int = 0
    var currentWeaponIndex = 0
    private val weapons: List<Weapon> = (0..6).map {
        Weapon.getWeaponAt(
            it, manager, gameValues.getInteger("ammo$it"), gameValues.getInteger(
                "power$it"
            ), true)
    }

    fun saveProgress(gameValues: Preferences) {
        for (i in weapons.indices) gameValues.putInteger("ammo$i", weapons[i].ammo)
    }
    val centerX: Float
    get() = x + width*.5f

    val centerY: Float
    get() = y + height*.5f
    override val currentWeapon: Weapon
        get() = weapons[currentWeaponIndex]

    override fun act(delta: Float) {
        super.act(delta)
        if(isMoving) updateVisibleArea()
    }

    private fun updateVisibleArea(){
        val cell = map.toCell(body.position)
        map.setPlayerCell(cell)
        map.scanCircle()
    }
}