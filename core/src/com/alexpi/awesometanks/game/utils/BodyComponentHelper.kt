package com.alexpi.awesometanks.game.utils

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World

fun getBodyComponent(world: World, position: Vector2, bodyType: BodyType, fixtureFilter: FixtureFilter, bodyShape: BodyShape): BodyComponent {
    val bodyDef = BodyDef()
    bodyDef.position.set(position)
    bodyDef.type = bodyType

    val fixtureDef = FixtureDef()
    fixtureDef.filter.categoryBits = fixtureFilter.categoryBits
    fixtureDef.filter.maskBits = fixtureFilter.maskBits

    val shape = bodyShape.createShape()
    fixtureDef.shape = shape

    val body = world.createBody(bodyDef)
    body.createFixture(fixtureDef)

    shape.dispose()

    val bodyComponent = BodyComponent()
    bodyComponent.body = body
    bodyComponent.width = bodyShape.width
    bodyComponent.height = bodyShape.height

    return bodyComponent
}