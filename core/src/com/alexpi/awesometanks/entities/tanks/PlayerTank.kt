package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.GameMap
import com.alexpi.awesometanks.weapons.RocketLauncher
import com.alexpi.awesometanks.weapons.RocketListener
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.experimental.or

class PlayerTank : Tank(Vector2(-1f,-1f), .75f,
    .07f + GameModule.getGameValues().getInteger(Constants.ROTATION_SPEED) / 40f,
    150 + GameModule.getGameValues().getInteger(Constants.MOVEMENT_SPEED) * 10f,
    Constants.CAT_PLAYER,
    (Constants.CAT_BLOCK or Constants.CAT_ITEM or Constants.CAT_ENEMY or Constants.CAT_ENEMY_BULLET),
    500f, false, null, Color.WHITE), RocketListener {

    private val map: GameMap = GameModule.getGameMap()
    private val visibilityRadius = 2 + GameModule.getGameValues().getInteger(Constants.VISIBILITY)
    private val armor = GameModule.getGameValues().getInteger(Constants.ARMOR)
    val position: Vector2
        get() = body.position

    var money: Int = 0
    var currentWeaponIndex = 0
    private val weapons: List<Weapon> = (0 until Constants.WEAPON_COUNT).map {
        Weapon.getWeaponAt(it, GameModule.getGameValues().getFloat("ammo$it"), GameModule.getGameValues().getInteger("power$it"), true, this)
    }

    val centerX: Float
        get() = if(isRocketActive) rocketPosition.x * Constants.TILE_SIZE else x + width*.5f

    val centerY: Float
        get() = if(isRocketActive) rocketPosition.y * Constants.TILE_SIZE else y + height*.5f

    private var isRocketActive = false
    private var rocketPosition = Vector2()
    override val currentWeapon: Weapon
        get() = weapons[currentWeaponIndex]

    init {
        map.visualRange = visibilityRadius
    }

    override fun act(delta: Float) {
        super.act(delta)
        if(isMoving || isRocketActive) updateVisibleArea()
    }

    override fun takeDamage(damage: Float) {
        super.takeDamage(damage * (1f - armor*.1f))
    }

    fun setRotationInput(x: Float, y: Float){
        if(isRocketActive){
            val rocketLauncher = weapons[Constants.ROCKET] as RocketLauncher
            rocketLauncher.rocket?.updateOrientation(x,y)
        } else currentWeapon.setDesiredAngleRotation(x,y)
    }

    fun setPos(row: Int, col: Int){
        body.setTransform(map.toWorldPos(row, col).add(.5f,.5f), body.angle)
    }

    fun saveProgress(gameValues: Preferences) {
        for (i in weapons.indices) gameValues.putFloat("ammo$i", weapons[i].ammo)
    }

    private fun updateVisibleArea(){
        val cell = map.toCell(if(isRocketActive) rocketPosition else body.position)
        map.setPlayerCell(cell.row, cell.col)
        map.updateVisibleArea()
    }

    override fun onRocketMoved(x: Float, y: Float) {
        isRocketActive = true
        rocketPosition.x = x
        rocketPosition.y = y
    }

    override fun onRocketCollided() {
        isRocketActive = false
    }
}