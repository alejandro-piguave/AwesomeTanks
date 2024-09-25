package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.LinearMovementComponent
import com.alexpi.awesometanks.game.components.MultiSpriteComponent
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

fun World.createPlayer(assetManager: AssetManager, physicsWorld: PhysicsWorld, position: Vector2): Int {
    val bodyComponent = getTankBodyComponent(physicsWorld, position, .75f)
    val bodySpriteComponent = getSpriteComponent(assetManager, "sprites/tank_body.png", SpriteComponent.Layer.ENTITY, bodyComponent)
    val wheelsSpriteComponent = getSpriteComponent(assetManager, "sprites/tank_wheels.png", SpriteComponent.Layer.ENTITY, bodyComponent)

    val wheelsSpriteId = create()
    edit(wheelsSpriteId).add(wheelsSpriteComponent)

    val bodySpriteId = create()
    edit(bodySpriteId).add(bodySpriteComponent)

    val multiSpriteComponent = MultiSpriteComponent(bodySpriteId = bodySpriteId, wheelsSpriteId = wheelsSpriteId)

    val tankId = create()
    edit(tankId).add(bodyComponent)
    edit(tankId).add(LinearMovementComponent(speed = 150f))
    edit(tankId).add(multiSpriteComponent)

    return tankId
}

private fun getTankBodyComponent(world: PhysicsWorld, position: Vector2, size: Float): BodyComponent {
    return getBodyComponent(world, position, BodyDef.BodyType.DynamicBody, FixtureFilter.PLAYER, BodyShape.Box(size, size))
}