package com.alexpi.awesometanks.game.projectiles.components.physics

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.game.weapons.RocketListener
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.cos
import kotlin.math.sin

class RocketPhysicsComponent(
    parent: Projectile,
    world: World,
    private val bodyShape: BodyShape.Box,
    angle: Float,
    force: Float,
    position: Vector2,
    isPlayer: Boolean,
    private val rocketListener: RocketListener
): ProjectilePhysicsComponent {

    companion object{
        private const val MAX_VELOCITY = 3f
        private const val MAX_VELOCITY2 = MAX_VELOCITY * MAX_VELOCITY
    }

    val body: Body

    private var forceX: Float = 0f
    private var forceY: Float = 0f

    init {
        val bodyDef = BodyDef()
        val fixtureDef = FixtureDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(position)
        bodyDef.bullet = true
        bodyDef.angle = angle

        val shape = bodyShape.createShape()
        fixtureDef.density = 1f
        fixtureDef.shape = shape

        val fixtureFilter = if (isPlayer) FixtureFilter.PLAYER_BULLET else FixtureFilter.ENEMY_BULLET

        fixtureDef.filter.categoryBits = fixtureFilter.categoryBits
        fixtureDef.filter.maskBits = fixtureFilter.maskBits
        body = world.createBody(bodyDef)
        val fixture = body.createFixture(fixtureDef)
        fixture.userData = parent
        body.userData = parent

        shape.dispose()

        forceX = force * cos(angle)
        forceY = force * sin(angle)
    }

    override fun setUp(projectile: Projectile) {
        projectile.setSize(bodyShape.width * TILE_SIZE, bodyShape.height * TILE_SIZE)
        projectile.setOrigin(projectile.width/2, projectile.height/2)
    }

    override fun getPosition(): Vector2 = body.position

    override fun update(projectile: Projectile) {
        body.applyForceToCenter(forceX, forceY,true)
        if(body.linearVelocity.len2() > MAX_VELOCITY2)
            body.linearVelocity.setLength2(MAX_VELOCITY2)

        rocketListener.onRocketMoved(body.position.x, body.position.y)

        projectile.setPosition(getLeft() * TILE_SIZE, getBottom() * TILE_SIZE)
        projectile.rotation = body.angle * MathUtils.radiansToDegrees
    }

    private fun getLeft(): Float = body.position.x - bodyShape.width/2
    private fun getBottom(): Float = body.position.x - bodyShape.width/2

    override fun dispose() {
        rocketListener.onRocketCollided()
        body.world.destroyBody(body)
    }
}