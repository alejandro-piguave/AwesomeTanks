package com.alexpi.awesometanks.screens.game


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.game.entities.buildLevelMap
import com.alexpi.awesometanks.game.map.MapLoader
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.systems.BodySystem
import com.alexpi.awesometanks.game.systems.RenderSystem
import com.alexpi.awesometanks.game.systems.UpdatePositionSystem
import com.alexpi.awesometanks.screens.BaseScreen
import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, level: Int) : BaseScreen(game), InputProcessor {

    val gameWorld: World
    val physicsWorld: PhysicsWorld = PhysicsWorld(Vector2.Zero, true)
    private val renderSystem = RenderSystem()
    val mapTable = MapTable(MapLoader.load(level))


    init {
        val config = WorldConfigurationBuilder()
            .with(
                renderSystem,
                BodySystem(),
                UpdatePositionSystem()
            )
            .build()
        gameWorld = World(config)

        buildLevelMap()
    }

    override fun show() {
        Gdx.input.inputProcessor = this
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


    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> {
                renderSystem.updateCameraPosition(0f, 80f)
                return true
            }
            Input.Keys.A -> {
                renderSystem.updateCameraPosition(-80f, 0f)
                return true
            }
            Input.Keys.S -> {
                renderSystem.updateCameraPosition(0f, -80f)
                return true
            }
            Input.Keys.D -> {
                renderSystem.updateCameraPosition(80f, 0f)
                return true
            }
        }
        return false
    }

    override fun hide() {
        gameWorld.dispose()
        Gdx.input.inputProcessor = null
    }

    //EVENTS FOR DESKTOP
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {

        return true
    }

    override fun keyTyped(character: Char): Boolean = false
    override fun scrolled(amountX: Float, amountY: Float): Boolean = false

}