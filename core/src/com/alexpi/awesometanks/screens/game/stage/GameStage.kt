package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.data.GameRepository
import com.alexpi.awesometanks.game.ai.PathFinding
import com.alexpi.awesometanks.game.blocks.Spawner
import com.alexpi.awesometanks.game.blocks.turret.Turret
import com.alexpi.awesometanks.game.manager.ContactManager
import com.alexpi.awesometanks.game.manager.ExplosionManager
import com.alexpi.awesometanks.game.manager.RumbleManager
import com.alexpi.awesometanks.game.map.MapLoader
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.map.createMap
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTank
import com.alexpi.awesometanks.game.tanks.player.PlayerTank
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

class GameStage(viewport: Viewport, val level: Int, val assetManager: AssetManager, val gameRepository: GameRepository, private val gameListener: GameListener): Stage(viewport) {

    val mapTable = MapTable(MapLoader.load(level))
    val pathFinding = PathFinding(mapTable)
    val entityGroup = Group()
    val blockGroup = Group()
    val shadeGroup = Group()
    val floorGroup = Group()
    val healthBarGroup: Group = Group()
    val world = World(Vector2(0f, 0f), true)
    val rumbleManager = RumbleManager()
    val gameContext = GameContext(this)
    val explosionManager = ExplosionManager(gameContext)

    var isPaused = false
    var isLevelCompleted = false
        private set
    val playerTank: PlayerTank = PlayerTank(gameContext)

    init {

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
        playerTank.setRotationInput(x, y)
    }

    fun onKeyDown(keycode: Int): Boolean {
        return playerTank.onKeyDown(keycode)
    }

    fun onKeyUp(keycode: Int): Boolean {
        return playerTank.onKeyUp(keycode)
    }

    fun onKnobTouch(x: Float, y: Float): Boolean {
        return playerTank.onKnobTouch(x, y)
    }

    private fun isLevelCleared(): Boolean {
        for (actor: Actor in blockGroup.children) if (actor is Turret && actor.healthComponent.isAlive) return false
        for (actor: Actor in entityGroup.children) if (actor is EnemyTank && actor.healthComponent.isAlive || actor is Spawner && actor.healthComponent.isAlive) return false
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
    }
}