package com.alexpi.awesometanks.screens


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.*
import com.alexpi.awesometanks.entities.actors.*
import com.alexpi.awesometanks.entities.blocks.*
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.*
import com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION
import com.alexpi.awesometanks.widget.GameProgressBar
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
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.actors.onClick
import kotlin.math.abs

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor, DamageListener {
    private lateinit var buttons: List<ImageButton>
    private val gameMap = GameMap(level)
    private val entityGroup: Group = Group()
    private val blockGroup: Group = Group()
    private val healthBarGroup: Group = Group()
    private val gameStage: Stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
    private val uiStage: Stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
    private val world = World(Vector2(0f, 0f), true)
    private lateinit var movementTouchpad: Touchpad
    private lateinit var aimTouchpad: Touchpad
    private lateinit var gunName: Label
    private lateinit var money: Label
    private lateinit var ammoBar: GameProgressBar
    private val weaponMenuTable: Table = Table()
    private var screenPointer = 0
    private var isPaused = false
    private var alreadyExecuted = false
    private var isLevelCompleted = false
    private val gunChangeSound: Sound = game.manager.get("sounds/gun_change.ogg")
    private val explosionSound: Sound = game.manager.get("sounds/explosion.ogg")
    private val tank: PlayerTank = PlayerTank(
        game.manager,
        world,
        game.gameValues,
        gameMap)

    override fun show() {
        addContactManager()
        createGameScene()
        createUIScene()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (!isPaused) {
            world.step(1 / 60f, 6, 2)
            gameStage.act(delta)
            ammoBar.value = tank.currentWeapon.ammo.toFloat()
            checkLevelState()
            gameStage.camera.position.set(tank.centerX, tank.centerY, 0f)
            if (Rumble.getRumbleTimeLeft() > 0){
                Rumble.tick(Gdx.graphics.deltaTime);
                gameStage.camera.translate(Rumble.getPos().x, Rumble.getPos().y,0f)
            }
        }
        gameStage.draw()
        uiStage.act(delta)
        uiStage.draw()
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
        for (actor: Actor in blockGroup.children) if (actor is Turret) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank || actor is Spawner) return false
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
                for (a: Actor in entityGroup.children) if (a is EnemyTank || a is Spawner) {
                    (a as DamageableActor).freeze(5f)
                }
                for (a: Actor in blockGroup.children) if (a is Turret) a.freeze(5f)
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
                createLandMineExplosion(x,y)
            }

            override fun onCanonBulletCollided(x: Float, y: Float) {
                createCanonBallExplosion(x,y)
            }
        })
        world.setContactListener(contactManager)
    }

    private fun createGameScene() {
        val shadeGroup = Group()
        gameMap.forCell { row, col, value, isVisible ->
            Gdx.app.debug("Cell", "row $row col $col")
            if (!isVisible) {
                shadeGroup.addActor(
                    Shade(
                        game.manager,
                        gameMap,
                        row, col
                    )
                )
            }
            if (value == Constants.wall) blockGroup.addActor(
                Wall(
                    game.manager,
                    world, gameMap.toWorldPos(row, col)
                )
            ) else {
                if(value == Constants.start){
                    tank.setPos(row,col)
                    entityGroup.addActor(tank)
                } else if (value == Constants.gate) blockGroup.addActor(
                    Gate(
                        this,
                        game.manager,
                        world, gameMap.toWorldPos(row,col)
                    )
                ) else if (value== Constants.bricks) blockGroup.addActor(
                    Bricks(
                        this,
                        game.manager,
                        world, gameMap.toWorldPos(row,col)
                    )
                ) else if (value == Constants.box) entityGroup.addActor(//ITS ADDED TO THE ENTITY GROUP BECAUSE THE ITEMS IT DROPS BELONG TO THIS GROUP AND NOT TO THE BLOCK GROUP
                    Box(
                        this,
                        game.manager,
                        world,
                        tank.body.position,
                        gameMap.toWorldPos(row,col),
                        level
                    )
                ) else if (value == Constants.spawner) entityGroup.addActor(
                    Spawner(
                        this,
                        game.manager,
                        world,
                        tank.body.position,
                        gameMap.toWorldPos(row,col),
                        level
                    )
                ) else if (value == Constants.bomb) blockGroup.addActor(
                    Mine(
                        this,
                        game.manager,
                        world,
                        gameMap.toWorldPos(row,col),
                    )
                ) else if (Character.isDigit(value)) {
                    val num = Character.getNumericValue(value)
                    blockGroup.addActor(
                        Turret(
                            this,
                            game.manager,
                            world,
                            tank.body.position,
                            gameMap.toWorldPos(row,col),
                            num
                        )
                    )
                } else if(value in Constants.MINIGUN_BOSS..Constants.RAILGUN_BOSS){
                    val type = value.code - Constants.MINIGUN_BOSS.code
                    entityGroup.addActor(EnemyTank(game.manager, world, gameMap.toWorldPos(row, col),tank.body.position,EnemyTank.Tier.BOSS,type,this ))
                }
                gameStage.addActor(Floor(game.manager, gameMap.toWorldPos(row, col)))
            }
        }

        healthBarGroup.addActor(HealthBar(game.manager, tank))
        gameStage.addActor(entityGroup)
        gameStage.addActor(blockGroup)
        gameStage.addActor(healthBarGroup)
        gameStage.addActor(shadeGroup)
    }

    private fun createUIScene() {
        val uiSkin = game.manager.get<Skin>("uiskin/uiskin.json")
        val uiTable = Table()
        uiTable.setFillParent(true)
        gunName = Label("Minigun", Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 4).toInt()))
        gunName.setAlignment(Align.center)
        ammoBar = GameProgressBar(game.manager,100f, tank.currentWeapon.ammo)
        ammoBar.setSize(300f, 30f)
        ammoBar.isVisible = false


        money = Label(
            tank.money.toString() + " $",
            Styles.getLabelStyle(game.manager, (Constants.TILE_SIZE / 3).toInt())
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
        movementTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager)).apply {
            setColor(color.r, color.g, color.b, 0.5f)
            addListener {
                val x = knobPercentX
                val y = knobPercentY
                if (isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                    tank.setOrientation(x, y)
                    tank.isMoving = true
                } else tank.isMoving = false
                true
            }
        }
        aimTouchpad = Touchpad(0f, Styles.getTouchPadStyle(game.manager)).apply {
            setColor(color.r, color.g, color.b, 0.5f)
            addListener {
                val x = knobPercentX
                val y = knobPercentY
                if (isTouched && (abs(x) > .2f || abs(y) > .2f)) {
                    tank.currentWeapon.setDesiredAngleRotation(x, y)
                    val distanceFromCenter = Utils.fastHypot(x.toDouble(), y.toDouble()).toFloat()
                    tank.isShooting = distanceFromCenter > 0.95f
                } else tank.isShooting = false
                true
            }
        }

        val weaponMenuButton = ImageButton(TextureRegionDrawable(game.manager.get("sprites/gun_menu_icon.png",Texture::class.java))).apply {
            setColor(color.r, color.g, color.b, 0.5f)
            onClick {
                isPaused = if(!isPaused){
                    uiStage.addActor(weaponMenuTable)
                    true
                } else {
                    weaponMenuTable.remove()
                    false
                }
            }
        }


        buttons.forEach { button ->
            val index = buttons.indexOf(button)

            if(index > 0) {
                button.isDisabled = game.gameValues.getBoolean("weapon$index",true);
                button.setColor(button.color.r,button.color.g,button.color.b,.5f);
            }
            if(!button.isDisabled) {
                button.onClick {
                    if (Settings.soundsOn) gunChangeSound.play()
                    tank.currentWeaponIndex = index
                    buttons.forEach { ib ->
                        ib.setColor(ib.color.r, ib.color.g, ib.color.b,
                            if(tank.currentWeaponIndex == buttons.indexOf(ib)) 1f else .5f)
                    }

                    button.setColor(
                        button.color.r,
                        button.color.g,
                        button.color.b,
                        1f
                    );

                    ammoBar.value = tank.currentWeapon.ammo
                    ammoBar.isVisible = index != 0;

                    gunName.setText(tank.currentWeapon.name)
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

        weaponMenuTable.setFillParent(true)
        val uiTopTable = Table()
        uiTopTable.add(ammoBar).expandX().uniformX().pad(10f)
        uiTopTable.add(money).expandX().uniform().pad(10f)
        uiTopTable.add().expandX().uniformX()
        uiTable.add(uiTopTable).colspan(3).growX().row()
        uiTable.add().colspan(3).expand().row()
        uiTable.add(movementTouchpad).size(joystickSize).left().pad(10f)
        uiTable.add(weaponMenuButton).expandX().right()
        uiTable.add(aimTouchpad).size(joystickSize).right().pad(10f).row()

        uiStage.addActor(uiTable)

        Gdx.input.inputProcessor = inputProcessor
        Gdx.input.isCatchBackKey = true

        Timer.schedule(object : Timer.Task() {
            override fun run() {
                gunName.addAction(Actions.fadeOut(TRANSITION_DURATION))
            }
        }, 2f)
    }

    private val inputProcessor: InputProcessor
        get() {
            val multiplexer = InputMultiplexer()
            multiplexer.addProcessor(uiStage)
            multiplexer.addProcessor(this)
            return multiplexer
        }

    private fun saveProgress() {
        if (isLevelCompleted) game.gameValues.putBoolean("unlocked" + (level+1), true)
        tank.saveProgress(game.gameValues)
        val savedMoney = game.gameValues.getInteger("money")
        game.gameValues.putInteger("money",savedMoney + tank.money)
        game.gameValues.flush()
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
                uiStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
                gameStage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(Constants.TRANSITION_DURATION),
                        Actions.run { game.screen = game.upgrades }
                    )
                )
            }
        })
        levelFailed.button(back)
        levelFailed.show(uiStage)
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
                uiStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
                gameStage.addAction(
                    Actions.sequence(
                        Actions.fadeOut(Constants.TRANSITION_DURATION),
                        Actions.run { game.screen = game.upgrades }
                    )
                )
            }
        })
        levelCompleted.button(continueButton)
        levelCompleted.show(uiStage)
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
                        uiStage.addAction(Actions.fadeOut(Constants.TRANSITION_DURATION))
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
                pauseMenu.show(uiStage)
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
        uiStage.dispose()
        gameStage.dispose()
        world.dispose()
        Gdx.input.inputProcessor = null
    }

    private fun createLandMineExplosion(x: Float, y: Float){
        createExplosion(x,y ,2.5f, 350f,1f, 40f, .65f)
    }

    private fun createCanonBallExplosion(x: Float, y: Float){
        createExplosion(x,y ,.25f, 35f,.05f, 15f, .45f)
    }

    private fun createExplosion(x: Float, y: Float, explosionRadius: Float, maxDamage: Float, volume: Float, rumblePower: Float, rumbleLength: Float){
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
                Actions.run { explosionShine.remove() }
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
                damageableActor.takeDamage(maxDamage * (explosionRadius - distanceFromMine) / explosionRadius)
            }
        }
        if (Settings.soundsOn) explosionSound.play(volume)
        Rumble.rumble(rumblePower, rumbleLength)
    }

    //EVENTS FOR DESKTOP
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            tank.currentWeapon.setDesiredAngleRotation(
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
                tank.currentWeapon.setDesiredAngleRotation(
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
                tank.currentWeapon.setDesiredAngleRotation(
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

    override fun onDeath(actor: DamageableActor) {
        if(actor is Block){
            val cell = gameMap.toCell(actor.body.position)
            gameMap.clear(cell)
        }
    }
}