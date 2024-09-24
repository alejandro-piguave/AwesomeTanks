package com.alexpi.awesometanks.screens.game


import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.game.entites.createTile
import com.alexpi.awesometanks.game.systems.RenderSystem
import com.alexpi.awesometanks.screens.BaseScreen
import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 30/12/2015.
 */
class GameScreen(game: MainGame, private val level: Int) : BaseScreen(game), InputProcessor {

    private val gameWorld: World
    private val renderSystem = RenderSystem()

    init {
        val config = WorldConfigurationBuilder().with(renderSystem).build()
        gameWorld = World(config)
    }

    override fun show() {

        val i = gameWorld.createTile(game.manager, "sprites/box.png", Vector2.Zero)
    }


    override fun resize(width: Int, height: Int) {
        renderSystem.updateViewport(width, height)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        gameWorld.delta = delta
        gameWorld.process()
    }



    override fun keyDown(keycode: Int): Boolean = false

    override fun hide() {
        gameWorld.dispose()
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