package com.alexpi.awesometanks.game.tanks.player

import com.alexpi.awesometanks.data.GameRepository
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.components.health.HealthComponent
import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.game.items.FreezingBall
import com.alexpi.awesometanks.game.items.GoldNugget
import com.alexpi.awesometanks.game.items.HealthPack
import com.alexpi.awesometanks.game.items.Item
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.tanks.Tank
import com.alexpi.awesometanks.game.weapons.Cannon
import com.alexpi.awesometanks.game.weapons.Flamethrower
import com.alexpi.awesometanks.game.weapons.LaserGun
import com.alexpi.awesometanks.game.weapons.MiniGun
import com.alexpi.awesometanks.game.weapons.RailGun
import com.alexpi.awesometanks.game.weapons.Ricochet
import com.alexpi.awesometanks.game.weapons.RocketLauncher
import com.alexpi.awesometanks.game.weapons.RocketListener
import com.alexpi.awesometanks.game.weapons.ShotGun
import com.alexpi.awesometanks.game.weapons.Weapon
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.alexpi.awesometanks.screens.game.stage.GameStage
import com.alexpi.awesometanks.screens.upgrades.PerformanceUpgrade
import com.alexpi.awesometanks.screens.upgrades.WeaponUpgrade
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.abs

class PlayerTank(gameContext: GameContext) : Tank(
    gameContext, Vector2(-1f, -1f), FixtureFilter.PLAYER, .75f,
    150 + gameContext.getGameRepository().getUpgradeLevel(PerformanceUpgrade.SPEED) * 10f,
    Color.WHITE
), RocketListener {

    private val map: MapTable = gameContext.getMapTable()
    private val gameStage: GameStage = gameContext.getStage()
    private val entityGroup = gameContext.getEntityGroup()
    private val blockGroup = gameContext.getBlockGroup()
    private val visibilityRadius =
        2 + gameContext.getGameRepository().getUpgradeLevel(PerformanceUpgrade.VISIBILITY)
    private val armor = gameContext.getGameRepository().getUpgradeLevel(PerformanceUpgrade.ARMOR)

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

    private val weapons: List<Weapon> = WeaponUpgrade.values().map {
        val weaponValues = gameContext.getGameRepository().getWeaponValues(it)
        val rotationSpeed = 4.2f + gameContext.getGameRepository()
            .getUpgradeLevel(PerformanceUpgrade.ROTATION) * 1.5f

        getWeaponAt(it, gameContext, weaponValues.ammo, weaponValues.power, rotationSpeed)
    }

    override val healthBarComponent =
        HealthBarComponent(gameContext, 500f, 500f, null)
    override val healthComponent =
        HealthComponent(
            gameContext,
            500f,
            isFlammable = true,
            isFreezable = false,
            onDamageTaken = { healthBarComponent.updateHealth(it) }
        )

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
            val rocketLauncher = weapons[WeaponUpgrade.ROCKETS.ordinal] as RocketLauncher
            rocketLauncher.rocket?.updateOrientation(x, y)
        } else {
            currentWeapon.desiredRotationAngle = MathUtils.atan2(y, x)
        }
    }

    fun setPosition(position: Vector2) {
        bodyComponent.body.setTransform(position.add(.5f, .5f), bodyComponent.body.angle)
    }

    fun saveProgress(gameRepository: GameRepository) {
        WeaponUpgrade.values()
            .forEachIndexed { i, weapon -> gameRepository.saveAmmo(weapon, weapons[i].ammo) }
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
    }

    private fun getWeaponAt(
        type: WeaponUpgrade,
        gameContext: GameContext,
        ammo: Float,
        power: Int,
        rotationSpeed: Float
    ): Weapon {
        return when (type) {
            WeaponUpgrade.MINIGUN -> MiniGun(gameContext, ammo, power, true, rotationSpeed)
            WeaponUpgrade.SHOTGUN -> ShotGun(gameContext, ammo, power, true, rotationSpeed)
            WeaponUpgrade.RICOCHET -> Ricochet(gameContext, ammo, power, true, rotationSpeed)
            WeaponUpgrade.FLAMETHROWER -> Flamethrower(
                gameContext,
                ammo,
                power,
                true,
                rotationSpeed
            )

            WeaponUpgrade.CANNON -> Cannon(gameContext, ammo, power, true, rotationSpeed)
            WeaponUpgrade.ROCKETS -> RocketLauncher(
                gameContext,
                ammo,
                power,
                true,
                rotationSpeed,
                this
            )

            WeaponUpgrade.LASERGUN -> LaserGun(gameContext, ammo, power, true, rotationSpeed)
            WeaponUpgrade.RAILGUN -> RailGun(gameContext, ammo, power, true, rotationSpeed)
        }
    }

}