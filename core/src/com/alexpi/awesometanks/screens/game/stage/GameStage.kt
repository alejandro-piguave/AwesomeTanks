package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.entities.actors.RumbleController
import com.alexpi.awesometanks.entities.ai.PathFinding
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.blocks.Turret
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.map.MapLoader
import com.alexpi.awesometanks.map.MapTable
import com.alexpi.awesometanks.world.ContactManager
import com.alexpi.awesometanks.world.ExplosionManager
import com.alexpi.awesometanks.world.GameListener
import com.alexpi.awesometanks.world.GameModule
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

class GameStage(viewport: Viewport, val level: Int, val assetManager: AssetManager, val gameValues: Preferences, private val gameListener: GameListener): Stage(viewport) {

    val mapTable: MapTable = MapTable(MapLoader.load(level))
    private val pathFinding: PathFinding
    val entityGroup: Group = Group()
    val blockGroup: Group = Group()
    val shadeGroup = Group()
    val floorGroup = Group()
    val healthBarGroup: Group = Group()
    val world = World(Vector2(0f, 0f), true)
    val rumbleController = RumbleController()
    val explosionManager = ExplosionManager(assetManager, world, rumbleController)
    val gameContext = GameContext(this)

    var isPaused = false
    var isLevelCompleted = false
        private set
    val player: Player = Player(gameContext)

    init {
        GameModule.world = world
        GameModule.assetManager = assetManager
        GameModule.set(gameValues)
        GameModule.mapTable = mapTable

        pathFinding = PathFinding(mapTable)
        GameModule.pathFinding = pathFinding

        GameModule.player = player

        setWorldContactListener()
        createMap()
    }

    override fun act(delta: Float) {
        if(!isPaused) {
            super.act(delta)
            world.step(1 / 60f, 6, 2)
        }
    }

    fun updateViewport(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    fun setRotationInput(x: Float, y: Float) {
        player.setRotationInput(x, y)
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
        for (actor: Actor in blockGroup.children) if (actor is Turret && actor.healthComponent.isAlive) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank && actor.healthComponent.isAlive || actor is Spawner && actor.healthComponent.isFrozen) return false
        return true
    }

    fun checkLevelCompletion() {
        isLevelCompleted = isLevelCleared()
        if(isLevelCompleted) gameListener.onLevelCompleted()
    }

    fun levelFailed() {
        gameListener.onLevelFailed()
    }

    private fun setWorldContactListener() {
        world.setContactListener(ContactManager())
    }

    override fun dispose() {
        super.dispose()
        world.dispose()
        GameModule.dispose()
    }
}