package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.RenderLayer
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.utils.setBounds
import com.artemis.World
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

fun World.createWall(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2): Int {
    val i = create()

    val bodyComponent = getBlockBodyComponent(physicsWorld, position, 1f)

    val spriteComponent = SpriteComponent()
    spriteComponent.renderLayer = RenderLayer.BLOCK
    spriteComponent.sprite = Sprite(assetManager.get<Texture>("sprites/wall.png"))
    spriteComponent.sprite.setBounds(bodyComponent)
    edit(i).add(bodyComponent)
    edit(i).add(spriteComponent)

    return i
}

fun World.createHealthBlock(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2, texturePath: String, size: Float = 1f): Int {
    val i = create()

    val bodyComponent = getBlockBodyComponent(physicsWorld, position, size)

    val spriteComponent = SpriteComponent()
    spriteComponent.renderLayer = RenderLayer.BLOCK
    spriteComponent.sprite = Sprite(assetManager.get<Texture>(texturePath))
    spriteComponent.sprite.setBounds(bodyComponent)
    edit(i).add(bodyComponent)
    edit(i).add(spriteComponent)

    return i
}




private fun getBlockBodyComponent(world: PhysicsWorld, position: Vector2, size: Float): BodyComponent {
    val bodyDef = BodyDef()
    bodyDef.position.set(position)
    bodyDef.type = BodyDef.BodyType.StaticBody

    val fixtureDef = FixtureDef()
    fixtureDef.filter.categoryBits = FixtureFilter.BLOCK.categoryBits
    fixtureDef.filter.maskBits = FixtureFilter.BLOCK.maskBits

    val shape = PolygonShape()
    shape.setAsBox(size/2f, size/2f)

    fixtureDef.shape = shape

    val body = world.createBody(bodyDef)
    body.createFixture(fixtureDef)

    shape.dispose()

    val bodyComponent = BodyComponent()
    bodyComponent.body = body
    bodyComponent.width = size
    bodyComponent.height = size

    return bodyComponent
}