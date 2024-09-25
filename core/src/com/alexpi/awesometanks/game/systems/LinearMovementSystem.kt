package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.LinearMovementComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem


@All(LinearMovementComponent::class, BodyComponent::class)
class LinearMovementSystem: IteratingSystem() {
    lateinit var bodyMapper: ComponentMapper<BodyComponent>
    lateinit var linearMovementMapper: ComponentMapper<LinearMovementComponent>

    override fun process(entityId: Int) {
        val linearMovement = linearMovementMapper[entityId]
        bodyMapper[entityId].body.setLinearVelocity(linearMovement.vX * world.delta, linearMovement.vY * world.delta)
    }
}