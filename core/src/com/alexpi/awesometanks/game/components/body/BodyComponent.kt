package com.alexpi.awesometanks.game.components.body

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor

class BodyComponent(
    parent: Actor,
    world: World,
    private val bodyShape: BodyShape,
    bodyType: BodyType,
    fixtureFilter: FixtureFilter,
    position: Vector2 = Vector2.Zero,
    density: Float = 0f
) {
    val body: Body
    private val fixture: Fixture

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        bodyDef.type = bodyType
        bodyDef.position.set(position)
        val shape = when (bodyShape) {
            is BodyShape.Circular -> CircleShape().apply { radius = bodyShape.radius }
            is BodyShape.Box -> PolygonShape().apply {
                setAsBox(
                    bodyShape.width / 2,
                    bodyShape.height / 2
                )
            }
        }
        fixtureDef.density = density
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = fixtureFilter.categoryBits
        fixtureDef.filter.maskBits = fixtureFilter.maskBits
        body = world.createBody(bodyDef)
        fixture = body.createFixture(fixtureDef)
        fixture.userData = parent
        body.userData = parent

        shape.dispose()
    }

    val left: Float get() = body.position.x - bodyShape.width /2

    val bottom: Float get() = body.position.y - bodyShape.height / 2


    fun destroy() {
        body.destroyFixture(fixture)
        body.world.destroyBody(body)
    }
}