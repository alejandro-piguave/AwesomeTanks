package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.actors.HealthBar
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.ai.PathFinding
import com.alexpi.awesometanks.entities.blocks.Block
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.blocks.Turret
import com.alexpi.awesometanks.entities.items.FreezingBall
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.entities.items.HealthPack
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.map.GameMap
import com.alexpi.awesometanks.map.MapInterpreter
import com.alexpi.awesometanks.map.MapLoader
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Rumble
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport

class GameRenderer(
    game: MainGame,
    private val gameListener: GameListener,
    level: Int) : DamageListener, ContactManager.ContactListener {

    private val gameMap: GameMap
    private val pathFinding: PathFinding
    private val entityGroup: Group = Group()
    private val blockGroup: Group = Group()
    private val healthBarGroup: Group = Group()
    private val gameStage: Stage = Stage(ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT))
    private val world = World(Vector2(0f, 0f), true)
    private val explosionManager = ExplosionManager(game.manager, gameStage, world)

    var isPaused = false
    private var alreadyExecuted = false
    var isLevelCompleted = false
        private set
    val player: Player

    init {
        GameModule.world = world
        GameModule.assetManager = game.manager
        GameModule.set(game.gameValues, this)

        val mapLoader = MapLoader()
        val map = mapLoader.load(level)
        gameMap = GameMap(map)
        GameModule.gameMap = gameMap

        pathFinding = PathFinding(gameMap)
        GameModule.pathFinding = pathFinding

        player = Player()
        GameModule.player = player

        setWorldContactListener()

        val shadeGroup = Group()
        val floorGroup = Group()

        val mapInterpreter = MapInterpreter()
        mapInterpreter.interpret(gameMap, level, player, shadeGroup, blockGroup, entityGroup, floorGroup)

        healthBarGroup.addActor(HealthBar(player))

        gameStage.addActor(floorGroup)
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

    fun onKeyDown(keycode: Int): Boolean {
        return player.onKeyDown(keycode)
    }

    fun onKeyUp(keycode: Int): Boolean {
        return player.onKeyUp(keycode)
    }

    fun onKnobTouch(x: Float, y: Float): Boolean {
        return player.onKnobTouch(x, y)
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
        world.setContactListener(ContactManager(this))
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
        explosionManager.createLandMineExplosion(x,y)
    }

    override fun onExplosiveProjectileCollided(x: Float, y: Float) {
        explosionManager.createCanonBallExplosion(x,y)
    }

    interface GameListener {
        fun onLevelFailed()
        fun onLevelCompleted()
    }
}



