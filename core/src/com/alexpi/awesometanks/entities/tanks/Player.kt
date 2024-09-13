package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.blocks.Turret
import com.alexpi.awesometanks.entities.components.body.CAT_BLOCK
import com.alexpi.awesometanks.entities.components.body.CAT_ENEMY
import com.alexpi.awesometanks.entities.components.body.CAT_ENEMY_BULLET
import com.alexpi.awesometanks.entities.components.body.CAT_ITEM
import com.alexpi.awesometanks.entities.components.body.CAT_PLAYER
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.items.Item
import com.alexpi.awesometanks.map.MapTable
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.upgrades.UpgradeType
import com.alexpi.awesometanks.weapons.RocketLauncher
import com.alexpi.awesometanks.weapons.RocketListener
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.ExplosionManager
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.experimental.or
import kotlin.math.abs

class Player(private val explosionManager: ExplosionManager, private val entityGroup: Group, private val blockGroup: Group) : Tank(Vector2(-1f,-1f), .75f,
    .07f + GameModule.getGameValues().getInteger(UpgradeType.ROTATION.name) / 40f,
    150 + GameModule.getGameValues().getInteger(UpgradeType.SPEED.name) * 10f,
    CAT_PLAYER,
    (CAT_BLOCK or CAT_ITEM or CAT_ENEMY or CAT_ENEMY_BULLET),
    500f, false, Color.WHITE), RocketListener {

    private val map: MapTable = GameModule.mapTable
    private val visibilityRadius = 2 + GameModule.getGameValues().getInteger(UpgradeType.VISIBILITY.name)
    private val armor = GameModule.getGameValues().getInteger(UpgradeType.ARMOR.name)

    val position: Vector2
        get() = body.position

    var onMoneyUpdated: ((Int) -> Unit)? = null
    var money: Int = 0
        set(value) {
            field = value
            onMoneyUpdated?.invoke(value)
        }

    var onWeaponAmmoUpdated: ((Float) -> Unit)? = null
    var currentWeaponIndex = 0
        set(value) {
            field = value
            onWeaponAmmoUpdated?.invoke(currentWeapon.ammo)
            currentWeapon.onAmmoUpdated = onWeaponAmmoUpdated
        }

    private val weapons: List<Weapon> = Weapon.Type.values().map {
        val weaponAmmo =  GameModule.getGameValues().getFloat("ammo${it.ordinal}")
        val weaponPower = GameModule.getGameValues().getInteger("power${it.ordinal}")
        Weapon.getWeaponAt(it, explosionManager, weaponAmmo, weaponPower, true, this)
    }

    //Used for keys

    private val centerX: Float
        get() = if(isRocketActive) rocketPosition.x * TILE_SIZE else x + width*.5f

    private val centerY: Float
        get() = if(isRocketActive) rocketPosition.y * TILE_SIZE else y + height*.5f

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
        stage.camera.position.set(centerX, centerY, 0f)
        if(isMoving || isRocketActive) {
            updateVisibleArea()
        }
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

    fun setPosition(position: Vector2) {
        body.setTransform(position.add(.5f,.5f), body.angle)
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

    fun pickUp(item: Item) {
        when (item) {
            is GoldNugget -> {
                money += item.value
            }

            is HealthPack -> heal(item.health)

            is FreezingBall -> freezeEnemies()
        }
        item.pickUp()
    }

    private fun freezeEnemies() {
        for (a: Actor in entityGroup.children) if (a is EnemyTank || a is Spawner) {
            (a as DamageableActor).freeze()
        }
        for (a: Actor in blockGroup.children) if (a is Turret) a.freeze()
    }

}