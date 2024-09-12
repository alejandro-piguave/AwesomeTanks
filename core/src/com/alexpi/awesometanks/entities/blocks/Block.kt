package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyComponent
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor

open class Block(
    assetManager: AssetManager,
    texturePath: String,
    world: World,
    bodyShape: BodyShape,
    position: Vector2
) : Actor() {
    private val texture: Texture = assetManager.get(texturePath, Texture::class.java)
    private val bodyComponent =
        BodyComponent(this, world, bodyShape, BodyDef.BodyType.StaticBody, FixtureFilter.BLOCK, position)

    init {
        this.setSize(bodyShape.width * TILE_SIZE, bodyShape.height * TILE_SIZE)
        this.setPosition(
            bodyComponent.getLeft() * TILE_SIZE,
            bodyComponent.getBottom() * TILE_SIZE
        )
    }

    override fun remove(): Boolean {
        bodyComponent.destroy()
        return super.remove()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width, height)
    }

}