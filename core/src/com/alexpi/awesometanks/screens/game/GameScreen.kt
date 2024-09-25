package com.alexpi.awesometanks.screens.game


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.game.entities.buildLevelMap
import com.alexpi.awesometanks.game.map.MapLoader
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.systems.BodyMultiSpriteSystem
import com.alexpi.awesometanks.game.systems.BodySystem
import com.alexpi.awesometanks.game.systems.CameraFollowSystem
import com.alexpi.awesometanks.game.systems.InputSystem
import com.alexpi.awesometanks.game.systems.LinearMovementSystem
import com.alexpi.awesometanks.game.systems.RenderSystem
import com.alexpi.awesometanks.screens.BaseScreen
import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, level: Int) : BaseScreen(game) {

    val gameWorld: World
    val physicsWorld: PhysicsWorld = PhysicsWorld(Vector2.Zero, true)
    private val renderSystem = RenderSystem()
    private val inputSystem = InputSystem()
    val tagManager = TagManager()
    val mapTable = MapTable(MapLoader.load(level))


    init {
        val config = WorldConfigurationBuilder()
            .with(
                tagManager,
                BodySystem(),
                LinearMovementSystem(),
                CameraFollowSystem(),
                inputSystem,
                BodyMultiSpriteSystem(),
                renderSystem,
            )
            .build()
        gameWorld = World(config)

        buildLevelMap()
    }

    override fun show() {
        Gdx.input.inputProcessor = inputSystem
    }


    override fun resize(width: Int, height: Int) {
        renderSystem.updateViewport(width, height)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        physicsWorld.step(1 / 60f, 6, 2)
        gameWorld.delta = delta
        gameWorld.process()
    }


    override fun hide() {
        gameWorld.dispose()
        Gdx.input.inputProcessor = null
    }

}