package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.utils.getBodyComponent
import com.alexpi.awesometanks.game.utils.getSpriteComponent
import com.artemis.World
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

fun World.createWall(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2): Int {

    val bodyComponent = getSquareBlockBodyComponent(physicsWorld, position, 1f)
    val spriteComponent = getSpriteComponent(assetManager, "sprites/wall.png", SpriteComponent.Layer.BLOCK, bodyComponent)

    val i = create()
    edit(i).add(bodyComponent)
    edit(i).add(spriteComponent)
    return i
}

fun World.createSquareHealthBlock(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2, texturePath: String, size: Float) {
    createHealthBlock(assetManager, physicsWorld, position, texturePath, BodyShape.Box(size, size))
}

fun World.createCircularHealthBlock(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2, texturePath: String, radius: Float) {
    createHealthBlock(assetManager, physicsWorld, position, texturePath, BodyShape.Circular(radius))
}

fun World.createHealthBlock(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2, texturePath: String, bodyShape: BodyShape): Int {
    val bodyComponent = getBlockBodyComponent(physicsWorld, position, bodyShape)
    val spriteComponent = getSpriteComponent(assetManager, texturePath, SpriteComponent.Layer.BLOCK, bodyComponent)

    val i = create()
    edit(i).add(bodyComponent)
    edit(i).add(spriteComponent)
    return i
}


private fun getBlockBodyComponent(world: PhysicsWorld, position: Vector2, bodyShape: BodyShape): BodyComponent {
    return getBodyComponent(world, position, BodyDef.BodyType.StaticBody, FixtureFilter.BLOCK, bodyShape)
}

private fun getSquareBlockBodyComponent(world: PhysicsWorld, position: Vector2, size: Float): BodyComponent {
    return getBlockBodyComponent(world, position, BodyShape.Box(size, size))
}