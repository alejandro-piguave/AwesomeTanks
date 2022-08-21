package com.alexpi.awesometanks.screens


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.*
import com.alexpi.awesometanks.entities.blocks.*
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tank.EnemyTank
import com.alexpi.awesometanks.entities.tank.PlayerTank
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION
import com.alexpi.awesometanks.utils.MapGenerator
import com.alexpi.awesometanks.utils.Styles
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.world.ContactManager
import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.actors.onClick
import kotlin.math.abs

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor, DamageListener {
    private lateinit var buttons: List<ImageButton>
    private val entityGroup: Group = Group()
    private val blockGroup: Group = Group()
    private val spawnerGroup: Group = Group()
    private val healthBarGroup: Group = Group()
    private lateinit var gameStage: Stage
    private lateinit var UIStage: Stage
    private lateinit var world: World
    private lateinit var movementTouchpad: Touchpad
    private lateinit var aimTouchpad: Touchpad
    private lateinit var tank: PlayerTank
    private lateinit var gunName: Label
    private lateinit var money: Label
    private lateinit var ammoAmount: Label
    private lateinit var gameValues: Preferences
    private val weaponMenuTable: Table = Table()
    private var screenPointer = 0
    private var soundFX = false
    private var isPaused = false
    private var alreadyExecuted = false
    private var isLevelCompleted = false
    private lateinit var gunChangeSound: Sound
    private lateinit var explosionSound: Sound
    override fun show() {
        UIStage = Stage()
        gameStage = Stage(FillViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
        world = World(Vector2(0f, 0f), true)
        gunChangeSound = game.manager.get("sounds/gun_change.ogg")
        explosionSound = game.manager.get("sounds/explosion.ogg")
        gameValues = Gdx.app.getPreferences("values")
        soundFX = game.gameSettings.getBoolean("areSoundsActivated")
        addContactManager()
        createGameScene()
        createUIScene()
        Gdx.input.inputProcessor = inputProcessor
        Gdx.input.isCatchBackKey = true
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (!isPaused) {
            world.step(1 / 60f, 6, 2)
            gameStage.act(delta)
            //ammoAmount.setText(tank.getCurrentWeapon().getAmmo() + "/100");
            checkLevelState()
            gameStage.camera.position.set(tank.centerX, tank.centerY, 0f)
        }
        gameStage.draw()
        UIStage.act(delta)
        UIStage.draw()
    }


    private fun checkLevelState() {
        isLevelCompleted = isLevelCompleted()
        if (!tank.isAlive && !alreadyExecuted) {
            isPaused = true
            alreadyExecuted = true
            showLevelFailedDialog()
        } else if (isLevelCompleted && !alreadyExecuted) {
            isPaused = true
            alreadyExecuted = true
            showLevelCompletedDialog()
        }
    }

    private fun isLevelCompleted(): Boolean {
        for(actor: Actor in spawnerGroup.children) if(actor is Spawner) return false
        for (actor: Actor in blockGroup.children) if (actor is Turret) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank) return false
        return true
    }


    private fun addContactManager() {
        val contactManager = ContactManager(object : ContactManager.ContactListener {
            override fun onGoldNuggetFound(goldNugget: GoldNugget) {
                money.setText((goldNugget.value.let { tank.money += it; tank.money }).toString() + " $")
            }

            override fun onHealthPackFound(healthPack: HealthPack) {
                tank.heal(healthPack.health)
            }

            override fun onFreezingBallFound(freezingBall: FreezingBall) {
                for (a: Actor? in gameStage.actors) if (a is EnemyTank) a.freeze(5f)
            }

            override fun onBulletCollision(x: Float, y: Float) {
                gameStage.addActor(
                    ParticleActor(
                        game.manager,
                        "particles/collision.party",
                        x,
                        y,
                        false
                    )
                )
            }

            override fun onLandMineFound(x: Float, y: Float) {
                val explosionRadius = 2.5f
                val explosionSize = Constants.TILE_SIZE * explosionRadius * 2
                val explosionX = Constants.TILE_SIZE * x
                val explosionY = Constants.TILE_SIZE * y
                gameStage.addActor(
                    ParticleActor(
                        game.manager,
                        "particles/big-explosion.party",
                        explosionX,
                        explosionY,
                        false
                    )
                )
                val explosionShine =
                    Image(game.manager.get("sprites/explosion_shine.png", Texture::class.java))
                explosionShine.setBounds(
                    explosionX - explosionSize * .5f,
                    explosionY - explosionSize * .5f,
                    explosionSize,
                    explosionSize
                )
                explosionShine.setOrigin(explosionSize * .5f, explosionSize * .5f)
                explosionShine.addAction(
                    Actions.sequence(
                        Actions.parallel(
                            Actions.scaleTo(.01f, .01f, .75f),
                            Actions.alpha(0f, .75f)
                        ),
                        Actions.run(Runnable { explosionShine.remove() })
                    )
                )
                gameStage.addActor(explosionShine)
                val bodies = com.badlogic.gdx.utils.Array<Body>()
                world.getBodies(bodies)
                for (body: Body in bodies) {
                    val distanceFromMine = Utils.fastHypot(
                        (body.position.x - x).toDouble(),
                        (body.position.y - y).toDouble()
                    ).toFloat()
                    if (body.userData is DamageableActor && (distanceFromMine < explosionRadius)) {
                        val damageableActor = (body.userData as DamageableActor)
                        damageableActor.takeDamage(350 * (explosionRadius - distanceFromMine) / explosionRadius)
                    }
                }
                if (soundFX) explosionSound.play()
            }
        })
        world.setContactListener(contactManager)
    }

    private fun createGameScene() {
        val shadeGroup = Group()
        val map = MapGenerator.getLevelMap(level)
        var playerX = 0
        var playerY = 0
        for (y in map.indices) for (x in 0 until map[y].size) if (map[y][x] == Constants.start) {
            playerX = x
            playerY = y
            tank = PlayerTank(
                game.manager,
                entityGroup,
                world,
                Vector2(x.toFloat(), (map.size - y).toFloat()),
                gameValues,
                Constants.colors[game.gameSettings.getInteger("tankColor")],
                gameValues.getInteger("money", 0),
                soundFX
            )
            entityGroup.addActor(tank)
        }
        for (y in map.indices) {
            for (x in 0 until map[y].size) {

                //Put shadows everywhere except around the player AND on walls
                if (((y < playerY - 1) || (y > playerY + 1) || (x < playerX - 1) || (x > playerX + 1)) && (x > 0) && (y > 0) && (x < map.size - 1) && (y < map[y].size - 1)) {
                    /*for(int i = 0; i <2; i++)
                        for (int j = 0; j <2;j++)
                            shadeGroup.addActor(new Shade(game.getManager(), tank,x + i * .5f, (map.length - y) + j * .5f));*/
                    shadeGroup.addActor(
                        Shade(
                            game.manager,
                            tank,
                            x.toFloat(),
                            ((map.size - y).toFloat())
                        )
                    )
                }
                if (map[y][x] == Constants.wall) blockGroup.addActor(
                    Wall(
                        game.manager,
                        world,
                        x,
                        map.size - y
                    )
                ) else {
                    if (map[y][x] == Constants.gate) blockGroup.addActor(
                        Gate(this,
                            game.manager,
                            world,
                            x,
                            map.size - y
                        )
                    ) else if (map[y][x] == Constants.bricks) blockGroup.addActor(
                        Bricks(this,
                            game.manager,
                            world,
                            x,
                            map.size - y
                        )
                    ) else if (map[y][x] == Constants.box) blockGroup.addActor(
                        Box(this,
                            game.manager,
                            entityGroup,
                            world,
                            tank.body.position,
                            x,
                            map.size - y,
                            level
                        )
                    ) else if (map[y][x] == Constants.spawner) spawnerGroup.addActor(
                        Spawner(this,
                            game.manager,
                            entityGroup,
                            world,
                            tank.body.position,
                            x,
                            map.size - y,
                            level
                        )
                    ) else if (map[y][x] == Constants.bomb) blockGroup.addActor(
                        Mine(this,
                            game.manager,
                            world,
                            x,
                            map.size - y
                        )
                    ) else if (Character.isDigit(
                            map[y][x]
                        )
                    ) {
                        val num = Character.getNumericValue(map[y][x])
                        blockGroup.addActor(
                            Turret(this,
                                game.manager,
                                entityGroup,
                                world,
                                tank.body.position,
                                x,
                                map.size - y,
                                num,
                                soundFX
                            )
                        )
                    }
                    val space = Image(game.manager.get("sprites/sand.png", Texture::class.java))
                    space.setBounds(
                        x * Constants.TILE_SIZE,
                        (map.size - y) * Constants.TILE_SIZE,
                        Constants.TILE_SIZE,
                        Constants.TILE_SIZE
                    )
                    gameStage.addActor(space)
                }
            }
        }
        healthBarGroup.addActor(HealthBar(game.manager, tank))
        gameStage.addActor(spawnerGroup)
        gameStage.addActor(entityGroup)
        gameStage.addActor(blockGroup)
        gameStage.addActor(healthBarGroup)
        gameStage.addActor(shadeGroup)
    }

    private fun createUIScene() {
        val uiSkin = game.manager.get<Skin>("uiskin/uiskin.json")
        gunName =
            Label("Minigun", Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 4).toInt()))
        gunName.setPosition(UIStage.width / 2 - gunName.width / 2, 10f)
        gunName.setAlignment(Align.center)
        ammoAmount = Label(
            tank.getCurrentWeapon().ammo.toString() + "/100",
            Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 2).toInt())
        )
        val ammoAlignment =
            if (game.gameSettings.getBoolean("isAlignedToLeft")) 10f else Constants.SCREEN_WIDTH - ammoAmount.width - 10f
        ammoAmount.setPosition(ammoAlignment, Constants.SCREEN_HEIGHT - ammoAmount.height)
        ammoAmount.setAlignment(Align.center)
        ammoAmount.isVisible = false
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                gunName.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
            }
        }, 2f)
        money = Label(
            tank.money.toString() + " $",
            Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 3).toInt())
        )
        money.setPosition(
            Constants.CENTER_X,
            Constants.SCREEN_HEIGHT - money.height,
            Align.center
        )
        buttons = (0 until BUTTON_COUNT).map {
            val texture = game.manager.get<Texture>("icons/icon_$it.png")
            val disabled = game.manager.get<Texture>("icons/icon_disabled_$it.png")
            val style = ImageButtonStyle(uiSkin.get(ButtonStyle::class.java))
            style.imageUp = TextureRegionDrawable(TextureRegion(texture))
            style.imageDisabled = TextureRegionDrawable(TextureRegion(disabled))
            ImageButton(style)
        }

        val joystickSize = Constants.SCREEN_HEIGHT / 2.25f
        movementTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager))
        aimTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager))
        movementTouchpad.setColor(
            movementTouchpad.color.r,
            movementTouchpad.color.g,
            movementTouchpad.color.b,
            0.5f
        )
        aimTouchpad.setColor(
            movementTouchpad.color.r,
            movementTouchpad.color.g,
            movementTouchpad.color.b,
            0.5f
        )
        movementTouchpad.setBounds(10f, 10f, joystickSize, joystickSize)
        aimTouchpad.setBounds(
            Constants.SCREEN_WIDTH - joystickSize - 10,
            10f,
            joystickSize,
            joystickSize
        )
        movementTouchpad.addListener {
            val x = movementTouchpad.knobPercentX
            val y = movementTouchpad.knobPercentY
            if (movementTouchpad.isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                tank.setOrientation(x, y)
                tank.isMoving = true
            } else tank.isMoving = false
            true
        }
        aimTouchpad.addListener {
            val x = aimTouchpad.knobPercentX
            val y = aimTouchpad.knobPercentY
            if (aimTouchpad.isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                tank.getCurrentWeapon().setDesiredAngleRotation(x, y)
                val distanceFromCenter = Utils.fastHypot(x.toDouble(), y.toDouble()).toFloat()
                tank.isShooting = distanceFromCenter > 0.95f
            } else tank.isShooting = false
            true
        }

        val weaponMenuButtonSize = 96f
        val weaponMenuButton = ImageButton(TextureRegionDrawable(game.manager.get("sprites/gun_menu_icon.png",Texture::class.java)))
        weaponMenuButton.setColor(
            weaponMenuButton.color.r,
            weaponMenuButton.color.g,
            weaponMenuButton.color.b,
            0.5f
        )

        weaponMenuTable.setPosition(Constants.CENTER_X, Constants.CENTER_Y)
        buttons.forEach { button ->
            val index = buttons.indexOf(button)

            if(index > 0) {
                button.isDisabled = gameValues.getBoolean("weapon$index",true);
                button.setColor(button.color.r,button.color.g,button.color.b,.5f);
            }
            if(!button.isDisabled) {
                button.onClick {
                    if (soundFX) gunChangeSound.play()
                    tank.currentWeapon = index
                    buttons.forEach { ib ->
                        ib.setColor(ib.color.r, ib.color.g, ib.color.b,
                            if(tank.currentWeapon == buttons.indexOf(ib)) 1f else .5f)
                    }

                    button.setColor(
                        button.color.r,
                        button.color.g,
                        button.color.b,
                        1f
                    );

                    ammoAmount.setText("${tank.getCurrentWeapon().ammo} /100");
                    ammoAmount.isVisible = index != 0;

                    gunName.setText(tank.getCurrentWeapon().name)
                    gunName.addAction(Actions.alpha(1f))
                    Timer.schedule(object : Timer.Task() {
                        override fun run() {
                            gunName.addAction(Actions.fadeOut(TRANSITION_DURATION))
                        }

                    }, 2f)
                }
            }
            weaponMenuTable.add(button).width(96f).height(96f)
        }
        weaponMenuButton.onClick {
            isPaused = if(weaponMenuButton.hasParent()){
                UIStage.addActor(weaponMenuTable)
                true
            } else {
                weaponMenuButton.remove()
                false
            }
        }

        weaponMenuButton.setBounds(aimTouchpad.x - weaponMenuButtonSize - 10, aimTouchpad.y +10 , weaponMenuButtonSize, weaponMenuButtonSize)

        UIStage.addActor(movementTouchpad)
        UIStage.addActor(aimTouchpad)
        if(Gdx.app.type != Application.ApplicationType.Desktop)
            UIStage.addActor(weaponMenuButton)
        UIStage.addActor(gunName)
        UIStage.addActor(money)
        UIStage.addActor(ammoAmount)
    }

    private val inputProcessor: InputProcessor
        get() {
            val multiplexer = InputMultiplexer()
            multiplexer.addProcessor(UIStage)
            multiplexer.addProcessor(this)
            return multiplexer
        }

    private fun saveProgress() {
        if (isLevelCompleted) gameValues.putBoolean("unlocked" + (level+1), true)
        tank.saveProgress(gameValues)
        gameValues.putInteger("money", tank.money)
        gameValues.flush()
    }

    private fun showLevelFailedDialog() {
        val levelFailed = Dialog(
            "Level failed",
            Styles.getWindowStyle(game.manager, (Constants.TILE_SIZE / 3).toInt())
        )
        val back = TextButton(
            "Back",
            Styles.getTextButtonStyle(game.manager, (Constants.TILE_SIZE / 4).toInt())
        )
        back.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                isPaused = false
                saveProgress()
                UIStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
                gameStage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(Constants.TRANSITION_DURATION),
                        Actions.run { game.screen = game.upgrades }
                    )
                )
            }
        })
        levelFailed.button(back)
        levelFailed.show(UIStage)
    }

    private fun showLevelCompletedDialog() {
        val levelCompleted = Dialog(
            "Level completed",
            Styles.getWindowStyle(game.manager, (Constants.TILE_SIZE / 3).toInt())
        )
        val continueButton = TextButton(
            "Continue",
            Styles.getTextButtonStyle(game.manager, (Constants.TILE_SIZE / 4).toInt())
        )
        continueButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                isPaused = false
                saveProgress()
                UIStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
                gameStage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(Constants.TRANSITION_DURATION),
                        Actions.run { game.screen = game.upgrades }
                    )
                )
            }
        })
        levelCompleted.button(continueButton)
        levelCompleted.show(UIStage)
        saveProgress()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if(Gdx.app.type != Application.ApplicationType.Desktop && weaponMenuTable.parent != null){
                weaponMenuTable.remove()
                isPaused = false
            } else{
                isPaused = true
                val pauseMenu = Dialog(
                    "Pause Menu",
                    Styles.getWindowStyle(game.manager, (Constants.TILE_SIZE / 3).toInt())
                )
                val back = TextButton(
                    "Back",
                    Styles.getTextButtonStyle(game.manager, (Constants.TILE_SIZE / 4).toInt())
                )
                back.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        isPaused = false
                        UIStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
                        gameStage.addAction(
                            Actions.sequence(
                                Actions.fadeOut(Constants.TRANSITION_DURATION),
                                Actions.run { game.screen = game.levelScreen }
                            )
                        )
                    }
                })
                val resume = TextButton(
                    "Resume",
                    Styles.getTextButtonStyle(game.manager, (Constants.TILE_SIZE / 4).toInt())
                )
                resume.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        isPaused = false
                    }
                })
                pauseMenu.button(back)
                pauseMenu.button(resume)
                pauseMenu.show(UIStage)
            }
            return true
        } else if (keycode == Input.Keys.SPACE) {
            saveProgress()
            gameStage.addAction(
                Actions.sequence(
                    Actions.fadeOut(Constants.TRANSITION_DURATION),
                    Actions.run { game.screen = game.levelScreen }
                )
            )
            return true
        }
        return false
    }

    override fun hide() {
        UIStage.dispose()
        gameStage.dispose()
        world.dispose()
        Gdx.input.inputProcessor = null
    }

    //EVENTS FOR DESKTOP
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            tank.getCurrentWeapon().setDesiredAngleRotation(
                screenX - Constants.CENTER_X,
                (Constants.SCREEN_HEIGHT - screenY) - Constants.CENTER_Y
            )
            screenPointer = pointer
            return true
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            if (pointer == screenPointer) {
                tank.getCurrentWeapon().setDesiredAngleRotation(
                    screenX - Constants.CENTER_X,
                    (Constants.SCREEN_HEIGHT - screenY) - Constants.CENTER_Y
                )
                return true
            }
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            if (pointer == screenPointer) {
                tank.getCurrentWeapon().setDesiredAngleRotation(
                    screenX - Constants.CENTER_X,
                    (Constants.SCREEN_HEIGHT - screenY) - Constants.CENTER_Y
                )
                tank.isShooting = true
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    companion object {
        private const val BUTTON_COUNT = 7
    }

    override fun onDamage(actor: DamageableActor) {
        healthBarGroup.addActor(HealthBar(game.manager, actor, DamageableActor.HEALTH_BAR_DURATION_SECONDS))
    }
}