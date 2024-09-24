package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.SCREEN_WIDTH
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport

@All(SpriteComponent::class)
class RenderSystem: IteratingSystem() {
    lateinit var mapper: ComponentMapper<SpriteComponent>

    private lateinit var viewport: Viewport
    private lateinit var camera: OrthographicCamera
    private lateinit var batch: Batch

    override fun begin() {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()
    }

    override fun process(entityId: Int) {
        val sprite = mapper[entityId].sprite
        sprite.draw(batch)
    }

    override fun end() {
        batch.end()
    }

    override fun initialize() {
        camera = OrthographicCamera()
        viewport = ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera)
        batch = SpriteBatch()
    }

    fun updateViewport(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        batch.dispose()
    }
}