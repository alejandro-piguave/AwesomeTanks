package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.actors.HealthBar
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.actors.RumbleController
import com.alexpi.awesometanks.entities.ai.PathFinding
import com.alexpi.awesometanks.entities.blocks.BaseBlock
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.blocks.Turret
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.listener.DamageListener
import com.alexpi.awesometanks.map.MapEntityCreator
import com.alexpi.awesometanks.map.MapLoader
import com.alexpi.awesometanks.map.MapTable
import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.SCREEN_WIDTH
import com.alexpi.awesometanks.world.collision.ContactManager
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

    private val mapTable: MapTable
    private val pathFinding: PathFinding
    private val entityGroup: Group = Group()
    private val blockGroup: Group = Group()
    private val healthBarGroup: Group = Group()
    private val gameStage: Stage = Stage(ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT))
    private val world = World(Vector2(0f, 0f), true)
    private val rumbleController = RumbleController()
    private val explosionManager = ExplosionManager(game.manager, gameStage, world, rumbleController)

    var isPaused = false
    var isLevelCompleted = false
        private set
    val player: Player

    init {
        GameModule.world = world
        GameModule.assetManager = game.manager
        GameModule.set(game.gameValues)

        val mapLoader = MapLoader()
        val map = mapLoader.load(level)
        mapTable = MapTable(map)
        GameModule.mapTable = mapTable

        pathFinding = PathFinding(mapTable)
        GameModule.pathFinding = pathFinding

        player = Player(explosionManager, entityGroup, blockGroup)
        GameModule.player = player

        setWorldContactListener()

        val shadeGroup = Group()
        val floorGroup = Group()

        val mapEntityCreator = MapEntityCreator()
        mapEntityCreator.create(mapTable, level, player, shadeGroup, blockGroup, entityGroup, floorGroup, explosionManager,this)

        healthBarGroup.addActor(HealthBar(player))

        gameStage.addActor(floorGroup)
        gameStage.addActor(entityGroup)
        gameStage.addActor(blockGroup)
        gameStage.addActor(healthBarGroup)
        gameStage.addActor(shadeGroup)
        gameStage.addActor(rumbleController)
    }


    fun render(delta: Float){
        if (!isPaused) {
            gameStage.act(delta)
        }
        gameStage.draw()
        if(!isPaused) world.step(1 / 60f, 6, 2)
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

    private fun isLevelCleared(): Boolean {
        for (actor: Actor in blockGroup.children) if (actor is Turret && actor.isAlive) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank && actor.isAlive || actor is Spawner && actor.isAlive) return false
        return true
    }

    private fun setWorldContactListener(){
        world.setContactListener(
            ContactManager(
                this
            )
        )
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
        gameStage.addActor(ParticleActor(
            "particles/explosion.party",
            actor.x + actor.width / 2,
            actor.y + actor.height / 2,
            false
        ))

        if(actor.rumble) {
            rumbleController.rumble(15f, .3f)
        }

        if(actor is BaseBlock){
            val cell = mapTable.toCell(actor.body.position)
            mapTable.clear(cell)
        } else if(actor is Player) {
            gameListener.onLevelFailed()
        }

        if((actor is EnemyTank || actor is Turret || actor is Spawner) && isLevelCleared().also { isLevelCompleted = it }) {
            gameListener.onLevelCompleted()
        }
    }

    override fun onExplosiveProjectileCollided(x: Float, y: Float) {
        explosionManager.createCanonBallExplosion(x,y)
    }

    interface GameListener {
        fun onLevelFailed()
        fun onLevelCompleted()
    }
}



