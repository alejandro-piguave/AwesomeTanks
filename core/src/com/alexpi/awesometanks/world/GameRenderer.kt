package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.actors.*
import com.alexpi.awesometanks.entities.ai.AStartPathFinding
import com.alexpi.awesometanks.entities.blocks.*
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.*
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport

class GameRenderer(private val game: MainGame,
                   private val gameListener: GameListener,
                   level: Int) : DamageListener {

    private val gameMap = GameMap(level)
    private val aStartPathFinding = AStartPathFinding(gameMap)
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
    val player: PlayerTank

    init {
        GameModule.set(game.manager, world, gameMap, aStartPathFinding, game.gameValues, this)
        GameModule.level = level
        player = PlayerTank()
        GameModule.setPlayer(player)
        setWorldContactListener()
        val shadeGroup = Group()
        gameMap.forCell { cell ->
            if (!cell.isVisible)
                shadeGroup.addActor(Shade(cell))

            if (cell.value == GameMap.WALL)
                blockGroup.addActor(Wall(gameMap.toWorldPos(cell)))
            else {
                when(cell.value){
                    GameMap.START -> {
                        player.setPos(cell)
                        entityGroup.addActor(player)
                    }

                    GameMap.GATE -> blockGroup.addActor( Gate(gameMap.toWorldPos(cell)))
                    GameMap.BRICKS -> blockGroup.addActor(Bricks(gameMap.toWorldPos(cell)))
                    GameMap.BOX -> entityGroup.addActor(Box(gameMap.toWorldPos(cell)))
                    GameMap.SPAWNER -> entityGroup.addActor(Spawner(gameMap.toWorldPos(cell)))
                    GameMap.BOMB -> blockGroup.addActor( Mine(gameMap.toWorldPos(cell)))

                    in GameMap.bosses -> {
                        val type = cell.value.code - GameMap.MINIGUN_BOSS.code
                        val weaponType = Weapon.Type.values()[type]
                        entityGroup.addActor(EnemyTank( gameMap.toWorldPos(cell), EnemyTank.Tier.BOSS, weaponType))
                    }

                    in GameMap.turrets -> {
                        val weaponType = Weapon.Type.values()[Character.getNumericValue(cell.value)]
                        blockGroup.addActor(Turret(gameMap.toWorldPos(cell), weaponType))
                    }

                }

                gameStage.addActor(Floor(game.manager, gameMap.toWorldPos(cell)))
            }
        }

        healthBarGroup.addActor(HealthBar(player))
        gameStage.addActor(entityGroup)
        gameStage.addActor(blockGroup)
        gameStage.addActor(healthBarGroup)
        gameStage.addActor(shadeGroup)
    }


    fun render(delta: Float){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (!isPaused) {
            gameStage.act(delta)
            checkLevelState()
            gameStage.camera.position.set(player.centerX, player.centerY, 0f)
            updateRumble(delta)
        }
        gameStage.draw()
        if(!isPaused) world.step(1 / 60f, 6, 2)
    }

    private fun checkLevelState() {
        isLevelCompleted = isLevelCleared()
        if (!player.isAlive && !alreadyExecuted) {
            alreadyExecuted = true
            gameListener.onLevelFailed()
        } else if (isLevelCompleted && !alreadyExecuted) {
            alreadyExecuted = true
            gameListener.onLevelCompleted()
        }
    }

    fun updateViewport(width: Int, height: Int){
        gameStage.viewport.update(width, height, true)
    }

    fun setRotationInput(x: Float, y: Float){
        player.setRotationInput(x,y)
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
        player.isMoving = true
        if(horizontalMovement.isEmpty() && verticalMovement.isEmpty()){
            player.isMoving = false
        } else if(horizontalMovement.isEmpty() && verticalMovement.isNotEmpty()){
            when(verticalMovement.last()){
                Movement.POSITIVE -> player.setOrientation(0f, 1f) //MOVING UP
                Movement.NEGATIVE -> player.setOrientation(0f, -1f) // MOVING DOWN
            }
        } else if(horizontalMovement.isNotEmpty() && verticalMovement.isEmpty()){
            when(horizontalMovement.last()){
                Movement.POSITIVE -> player.setOrientation(1f, 0f) //MOVING RIGHT
                Movement.NEGATIVE -> player.setOrientation(-1f, 0f) // MOVING LEFT
            }
        } else {
            if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.POSITIVE)
                player.setOrientation(Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.POSITIVE)
                player.setOrientation(-Constants.SQRT2_2, Constants.SQRT2_2) // MOVING NORTH WEST
            else if(horizontalMovement.last() == Movement.POSITIVE && verticalMovement.last() == Movement.NEGATIVE)
                player.setOrientation(Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH EAST
            else if(horizontalMovement.last() == Movement.NEGATIVE && verticalMovement.last() == Movement.NEGATIVE)
                player.setOrientation(-Constants.SQRT2_2, -Constants.SQRT2_2) // MOVING SOUTH WEST
        }
    }

    private fun updateRumble(delta: Float){
        if (Rumble.rumbleTimeLeft > 0){
            Rumble.tick(delta)
            gameStage.camera.translate(Rumble.pos.x, Rumble.pos.y,0f)
        }
    }
    private fun isLevelCleared(): Boolean {
        for (actor: Actor in blockGroup.children) if (actor is Turret) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank || actor is Spawner) return false
        return true
    }

    private fun setWorldContactListener(){
        world.setContactListener(ContactManager(object: ContactManager.ContactListener{

            override fun onGoldNuggetFound(goldNugget: GoldNugget) {
                player.money += goldNugget.value
            }

            override fun onHealthPackFound(healthPack: HealthPack) {
                player.heal(healthPack.health)
            }

            override fun onFreezingBallFound(freezingBall: FreezingBall) {
                for (a: Actor in entityGroup.children) if (a is EnemyTank || a is Spawner) {
                    (a as DamageableActor).freeze()
                }
                for (a: Actor in blockGroup.children) if (a is Turret) a.freeze()
            }

            override fun onBulletCollision(x: Float, y: Float) {
                gameStage.addActor(ParticleActor("particles/collision.party", x, y, false))
            }

            override fun onLandMineFound(x: Float, y: Float) {
                createLandMineExplosion(x,y)
            }

            override fun onExplosiveProjectileCollided(x: Float, y: Float) {
                createCanonBallExplosion(x,y)
            }
        }))
    }

    fun dispose(){
        gameStage.dispose()
        world.dispose()
        GameModule.dispose()
    }

    override fun onDamage(actor: DamageableActor) {
        healthBarGroup.addActor(HealthBar(actor, DamageableActor.HEALTH_BAR_DURATION))
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
                "particles/big-explosion.party",
                explosionX,
                explosionY,
                false
            )
        )
        val explosionShine = Image(game.manager.get("sprites/explosion_shine.png", Texture::class.java))
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