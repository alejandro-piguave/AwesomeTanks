package com.alexpi.awesometanks.game.tanks

import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.items.FreezingBall
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.items.HealthPack
import com.alexpi.awesometanks.game.items.Item
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.alexpi.awesometanks.screens.upgrades.UpgradeType
import com.alexpi.awesometanks.game.weapons.RocketLauncher
import com.alexpi.awesometanks.game.weapons.RocketListener
import com.alexpi.awesometanks.game.weapons.Weapon
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.abs

class Player(gameContext: GameContext) : Tank(
    gameContext, Vector2(-1f, -1f), FixtureFilter.PLAYER, .75f,
    500f,
    false,
    .07f + gameContext.getGameValues().getInteger(UpgradeType.ROTATION.name) / 40f,
    150 + gameContext.getGameValues().getInteger(UpgradeType.SPEED.name) * 10f,
    Color.WHITE
), RocketListener {

    private val map: MapTable = gameContext.getMapTable()
    private val gameStage: GameStage = gameContext.getStage()
    private val entityGroup = gameContext.getEntityGroup()
    private val blockGroup = gameContext.getBlockGroup()
    private val visibilityRadius =
        2 + gameContext.getGameValues().getInteger(UpgradeType.VISIBILITY.name)
    private val armor = gameContext.getGameValues().getInteger(UpgradeType.ARMOR.name)

    val position: Vector2
        get() = bodyComponent.body.position

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
        val weaponAmmo = gameContext.getGameValues().getFloat("ammo${it.ordinal}")
        val weaponPower = gameContext.getGameValues().getInteger("power${it.ordinal}")
        Weapon.getWeaponAt(it, gameContext, weaponAmmo, weaponPower, true, this)
    }

    //Used for keys

    private val centerX: Float
        get() = if (isRocketActive) rocketPosition.x * TILE_SIZE else x + width * .5f

    private val centerY: Float
        get() = if (isRocketActive) rocketPosition.y * TILE_SIZE else y + height * .5f

    private var horizontalMovement = 0f
    private var verticalMovement = 0f

    private var isRocketActive = false
    private var rocketPosition = Vector2()
    override val currentWeapon: Weapon
        get() = weapons[currentWeaponIndex]

    override fun act(delta: Float) {
        super.act(delta)
        stage.camera.position.set(centerX, centerY, 0f)
        if (isMoving || isRocketActive) {
            updateVisibleArea()
        }
    }

    override fun remove(): Boolean {
        gameStage.levelFailed()
        return super.remove()
    }

    fun setRotationInput(x: Float, y: Float) {
        if (isRocketActive) {
            val rocketLauncher = weapons[Weapon.Type.ROCKETS.ordinal] as RocketLauncher
            rocketLauncher.rocket?.updateOrientation(x, y)
        } else currentWeapon.setDesiredRotationAngleFrom(x, y)
    }

    fun setPosition(position: Vector2) {
        bodyComponent.body.setTransform(position.add(.5f, .5f), bodyComponent.body.angle)
    }

    fun saveProgress(gameValues: Preferences) {
        for (i in weapons.indices) gameValues.putFloat("ammo$i", weapons[i].ammo)
    }

    private fun updateVisibleArea() {
        val cell = map.toCell(if (isRocketActive) rocketPosition else bodyComponent.body.position)
        map.updateVisibleArea(cell.row, cell.col, visibilityRadius)
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
            Input.Keys.W -> {
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

    private fun moveUp() {
        verticalMovement += 1f
        updateMovement()
    }

    private fun moveDown() {
        verticalMovement -= 1f
        updateMovement()
    }

    private fun moveLeft() {
        horizontalMovement -= 1f
        updateMovement()
    }

    private fun moveRight() {
        horizontalMovement += 1f
        updateMovement()
    }

    private fun stopUp() {
        verticalMovement -= 1f
        updateMovement()
    }

    private fun stopDown() {
        verticalMovement += 1f
        updateMovement()
    }

    private fun stopLeft() {
        horizontalMovement += 1f
        updateMovement()
    }

    private fun stopRight() {
        horizontalMovement -= 1f
        updateMovement()
    }

    private fun updateMovement() {
        setMovementDirection(horizontalMovement, verticalMovement)
    }

    fun pickUp(item: Item) {
        when (item) {
            is GoldNugget -> {
                money += item.value
            }

            is HealthPack -> healthComponent.heal(item.health.toFloat())

            is FreezingBall -> freezeEnemies()
        }
        item.pickUp()
    }

    private fun freezeEnemies() {
        for (a: Actor in entityGroup.children) if (a is HealthOwner) {
            a.healthComponent.freeze(5f)
        }
        for (a: Actor in blockGroup.children) if (a is HealthOwner) a.healthComponent.freeze(5f)
    }

    init {
        healthComponent.damageReduction = armor * .1f
        healthBarComponent.showHealthBar()
    }

}