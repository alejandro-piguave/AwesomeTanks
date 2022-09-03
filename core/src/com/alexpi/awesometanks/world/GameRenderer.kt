package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.actors.*
import com.alexpi.awesometanks.entities.blocks.*
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport

class GameRenderer(private val game: MainGame,
                   private val gameListener: GameListener,
                   private val level: Int) : DamageListener {

    private val gameMap = GameMap(level)
    private val entityGroup: Group = Group()
    private val blockGroup: Group = Group()
    private val healthBarGroup: Group = Group()
    private val gameStage: Stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
    private val explosionSound: Sound = game.manager.get("sounds/explosion.ogg")
    private val world = World(Vector2(0f, 0f), true)

    //Used for keys
    private var horizontalMovement: MutableList<Movement> = mutableListOf()
    private var verticalMovement: MutableList<Movement> = mutableListOf()

    var isPaused = false
    private var alreadyExecuted = false
    var isLevelCompleted = false
        private set
    val tank: PlayerTank = PlayerTank(
        game.manager,
        world,
        game.gameValues,
        gameMap)

    init {
        world.setContactListener(ContactManager( object: ContactManager.ContactListener{
            override fun onGoldNuggetFound(goldNugget: GoldNugget) {
                tank.money += goldNugget.value
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

            override fun onExplosiveProjectileCollided(x: Float, y: Float) {
                createCanonBallExplosion(x,y)
            }
        }))

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
                        tank,
                        gameMap.toWorldPos(row,col),
                        level
                    )
                ) else if (value == Constants.spawner) entityGroup.addActor(
                    Spawner(
                        this,
                        game.manager,
                        world,
                        tank,
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
                            tank,
                            gameMap.toWorldPos(row,col),
                            num
                        )
                    )
                } else if(value in Constants.MINIGUN_BOSS..Constants.RAILGUN_BOSS){
                    val type = value.code - Constants.MINIGUN_BOSS.code
                    entityGroup.addActor(
                        EnemyTank(game.manager, world, gameMap.toWorldPos(row, col),tank,
                            EnemyTank.Tier.BOSS,type,this )
                    )
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


    fun render(delta: Float){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (!isPaused) {
            world.step(1 / 60f, 6, 2)
            gameStage.act(delta)
            checkLevelState()
            gameStage.camera.position.set(tank.centerX, tank.centerY, 0f)
            if (Rumble.rumbleTimeLeft > 0){
                Rumble.tick(delta)
                gameStage.camera.translate(Rumble.pos.x, Rumble.pos.y,0f)
            }
        }
        gameStage.draw()
    }

    private fun checkLevelState() {
        isLevelCompleted = isLevelCleared()
        if (!tank.isAlive && !alreadyExecuted) {
            alreadyExecuted = true
            gameListener.onLevelFailed()
        } else if (isLevelCompleted && !alreadyExecuted) {
            alreadyExecuted = true
            gameListener.onLevelCompleted()
        }
    }

    fun setRotationInput(x: Float, y: Float){
        tank.setRotationInput(x,y)
    }

    fun moveUp(){
        verticalMovement.add(Movement.POSITIVE)
        updateMovement()
    }

    fun moveDown(){
        verticalMovement.add(Movement.NEGATIVE)
        updateMovement()
    }

    fun moveLeft(){
        horizontalMovement.add(Movement.NEGATIVE)
        updateMovement()
    }

    fun moveRight(){
        horizontalMovement.add(Movement.POSITIVE)
        updateMovement()
    }

    fun stopUp(){
        verticalMovement.remove(Movement.POSITIVE)
        updateMovement()
    }

    fun stopDown(){
        verticalMovement.remove(Movement.NEGATIVE)
        updateMovement()
    }

    fun stopLeft(){
        horizontalMovement.remove(Movement.NEGATIVE)
        updateMovement()
    }

    fun stopRight(){
        horizontalMovement.remove(Movement.POSITIVE)
        updateMovement()
    }

    private fun updateMovement(){
        tank.isMoving = true
        if(horizontalMovement.isEmpty() && verticalMovement.isEmpty()){
            tank.isMoving = false
        } else if(horizontalMovement.isEmpty() && verticalMovement.isNotEmpty()){
            when(verticalMovement.last()){
                Movement.POSITIVE -> tank.setOrientation(0f, 1f) //MOVING UP
                Movement.NEGATIVE -> tank.setOrientation(0f, -1f) // MOVING DOWN
            }
        } else if(horizontalMovement.isNotEmpty() && verticalMovement.isEmpty()){
            when(horizontalMovement.last()){
                Movement.POSITIVE -> tank.setOrientation(1f, 0f) //MOVING RIGHT
                Movement.NEGATIVE -> tank.setOrientation(-1f, 0f) // MOVING LEFT
            }
        } else {
            if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.POSITIVE)
                tank.setOrientation(Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.POSITIVE)
                tank.setOrientation(-Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH WEST
            else if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.NEGATIVE)
                tank.setOrientation(Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.NEGATIVE)
                tank.setOrientation(-Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH WEST
        }
    }

    private fun isLevelCleared(): Boolean {
        for (actor: Actor in blockGroup.children) if (actor is Turret) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank || actor is Spawner) return false
        return true
    }

    fun fadeOut(runnable: Runnable){
        gameStage.addAction(
            Actions.sequence(
                Actions.fadeOut(Constants.TRANSITION_DURATION),
                Actions.run(runnable)
            )
        )
    }

    fun dispose(){
        gameStage.dispose()
        world.dispose()
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
        world.QueryAABB({
            val distanceFromMine = Utils.fastHypot(
                (it.body.position.x - x).toDouble(),
                (it.body.position.y - y).toDouble()
            ).toFloat()
            if (it.userData is DamageableActor && (distanceFromMine < explosionRadius)) {
                val damageableActor = (it.userData as DamageableActor)
                damageableActor.takeDamage(maxDamage * (explosionRadius - distanceFromMine) / explosionRadius)
            }
                        true
        },x-explosionRadius,y-explosionRadius,x+explosionRadius,y+explosionRadius)

        if (Settings.soundsOn) explosionSound.play(volume)
        Rumble.rumble(rumblePower, rumbleLength)
    }
}

enum class Movement{ POSITIVE, NEGATIVE}

interface GameListener{
    fun onLevelFailed()
    fun onLevelCompleted()
}