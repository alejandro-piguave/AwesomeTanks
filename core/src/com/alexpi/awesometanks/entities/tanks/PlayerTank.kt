package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.map.GameMap
import com.alexpi.awesometanks.screens.UpgradeType
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.weapons.RocketLauncher
import com.alexpi.awesometanks.weapons.RocketListener
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.experimental.or
import kotlin.math.abs

class PlayerTank : Tank(Vector2(-1f,-1f), .75f,
    .07f + GameModule.getGameValues().getInteger(UpgradeType.ROTATION.name) / 40f,
    150 + GameModule.getGameValues().getInteger(UpgradeType.SPEED.name) * 10f,
    Constants.CAT_PLAYER,
    (Constants.CAT_BLOCK or Constants.CAT_ITEM or Constants.CAT_ENEMY or Constants.CAT_ENEMY_BULLET),
    500f, false, Color.WHITE), RocketListener {

    private val map: GameMap = GameModule.gameMap
    private val visibilityRadius = 2 + GameModule.getGameValues().getInteger(UpgradeType.VISIBILITY.name)
    private val armor = GameModule.getGameValues().getInteger(UpgradeType.ARMOR.name)
    val position: Vector2
        get() = body.position

    var money: Int = 0
    var currentWeaponIndex = 0
    private val weapons: List<Weapon> = Weapon.Type.values().map {
        val weaponAmmo =  GameModule.getGameValues().getFloat("ammo${it.ordinal}")
        val weaponPower = GameModule.getGameValues().getInteger("power${it.ordinal}")
        Weapon.getWeaponAt(it,weaponAmmo, weaponPower, true, this)
    }

    //Used for keys

    val centerX: Float
        get() = if(isRocketActive) rocketPosition.x * Constants.TILE_SIZE else x + width*.5f

    val centerY: Float
        get() = if(isRocketActive) rocketPosition.y * Constants.TILE_SIZE else y + height*.5f

    private var horizontalMovement = 0f
    private var verticalMovement = 0f

    private var isRocketActive = false
    private var rocketPosition = Vector2()
    override val currentWeapon: Weapon
        get() = weapons[currentWeaponIndex]

    init {
        map.visualRange = visibilityRadius
    }

    override fun onAlive(delta: Float) {
        super.onAlive(delta)
        if(isMoving || isRocketActive) updateVisibleArea()
    }

    override fun takeDamage(damage: Float) {
        super.takeDamage(damage * (1f - armor*.1f))
    }

    fun setRotationInput(x: Float, y: Float){
        if(isRocketActive){
            val rocketLauncher = weapons[Weapon.Type.ROCKETS.ordinal] as RocketLauncher
            rocketLauncher.rocket?.updateOrientation(x,y)
        } else currentWeapon.setDesiredRotationAngleFrom(x,y)
    }

    fun setPos(cell: Cell){
        body.setTransform(map.toWorldPos(cell).add(.5f,.5f), body.angle)
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

    fun onKeyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> {
                moveUp()
                return true
            }
            Input.Keys.A -> {
                moveLeft()
                return true
            }
            Input.Keys.S -> {
                moveDown()
                return true
            }
            Input.Keys.D -> {
                moveRight()
                return true
            }
            else -> return false
        }
    }

    fun onKeyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W ->{
                stopUp()
                return true
            }
            Input.Keys.A -> {
                stopLeft()
                return true
            }
            Input.Keys.S -> {
                stopDown()
                return true
            }
            Input.Keys.D -> {
                stopRight()
                return true
            }
        }
        return false
    }

    fun onKnobTouch(x: Float, y: Float): Boolean {
        if (abs(x) > .2f || abs(y) > .2f) {
            setMovementDirection(x, y)
        } else stopMovement()
        return true
    }

    private fun moveUp(){
        verticalMovement += 1f
        updateMovement()
    }

    private fun moveDown(){
        verticalMovement -= 1f
        updateMovement()
    }

    private fun moveLeft(){
        horizontalMovement -= 1f
        updateMovement()
    }

    private fun moveRight(){
        horizontalMovement += 1f
        updateMovement()
    }

    private fun stopUp(){
        verticalMovement -= 1f
        updateMovement()
    }

    private fun stopDown(){
        verticalMovement += 1f
        updateMovement()
    }

    private fun stopLeft(){
        horizontalMovement += 1f
        updateMovement()
    }

    private fun stopRight(){
        horizontalMovement -= 1f
        updateMovement()
    }

    private fun updateMovement(){
        setMovementDirection(horizontalMovement, verticalMovement)
    }

}