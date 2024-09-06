package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.map.GameMap
import com.alexpi.awesometanks.screens.UpgradeType
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.weapons.RocketLauncher
import com.alexpi.awesometanks.weapons.RocketListener
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.GameModule
import com.alexpi.awesometanks.world.Movement
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import kotlin.experimental.or

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
    private var horizontalMovement: MutableList<Movement> = mutableListOf()
    private var verticalMovement: MutableList<Movement> = mutableListOf()

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
            Input.Keys.A ->{
                stopLeft()
                return true
            }
            Input.Keys.S ->{
                stopDown()
                return true
            }
            Input.Keys.D ->{
                stopRight()
                return true
            }
        }
        return false
    }
    private fun moveUp(){
        verticalMovement.add(Movement.POSITIVE)
        updateMovement()
    }

    private fun moveDown(){
        verticalMovement.add(Movement.NEGATIVE)
        updateMovement()
    }

    private fun moveLeft(){
        horizontalMovement.add(Movement.NEGATIVE)
        updateMovement()
    }

    private fun moveRight(){
        horizontalMovement.add(Movement.POSITIVE)
        updateMovement()
    }

    private fun stopUp(){
        verticalMovement.remove(Movement.POSITIVE)
        updateMovement()
    }

    private fun stopDown(){
        verticalMovement.remove(Movement.NEGATIVE)
        updateMovement()
    }

    private fun stopLeft(){
        horizontalMovement.remove(Movement.NEGATIVE)
        updateMovement()
    }

    private fun stopRight(){
        horizontalMovement.remove(Movement.POSITIVE)
        updateMovement()
    }

    private fun updateMovement(){
        isMoving = true
        if(horizontalMovement.isEmpty() && verticalMovement.isEmpty()){
            isMoving = false
        } else if(horizontalMovement.isEmpty()){
            when(verticalMovement.last()){
                Movement.POSITIVE -> setOrientation(0f, 1f) //MOVING UP
                Movement.NEGATIVE -> setOrientation(0f, -1f) // MOVING DOWN
            }
        } else if(verticalMovement.isEmpty()){
            when(horizontalMovement.last()){
                Movement.POSITIVE -> setOrientation(1f, 0f) //MOVING RIGHT
                Movement.NEGATIVE -> setOrientation(-1f, 0f) // MOVING LEFT
            }
        } else {
            if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.POSITIVE)
                setOrientation(Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.POSITIVE)
                setOrientation(-Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH WEST
            else if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.NEGATIVE)
                setOrientation(Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.NEGATIVE)
                setOrientation(-Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH WEST
        }
    }

}