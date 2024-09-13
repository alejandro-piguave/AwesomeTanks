package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyComponent
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Actor

open class Block(
    gameContext: GameContext,
    texturePath: String,
    bodyShape: BodyShape,
    position: Vector2,
    fixtureFilter: FixtureFilter,
) : Actor() {
    private val texture: Texture = gameContext.getAssetManager().get(texturePath, Texture::class.java)
    protected val bodyComponent = BodyComponent(this, gameContext.getWorld(), bodyShape, BodyDef.BodyType.StaticBody, fixtureFilter, position)

    init {
        this.setSize(bodyShape.width * TILE_SIZE, bodyShape.height * TILE_SIZE)
        this.setPosition(
            bodyComponent.left * TILE_SIZE,
            bodyComponent.bottom * TILE_SIZE
        )
    }

    override fun remove(): Boolean {
        bodyComponent.destroy()
        return super.remove()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawBlock(batch, parentAlpha)
    }

    open fun drawBlock(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width, height)
    }

}