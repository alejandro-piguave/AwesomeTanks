package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.SmoothBodyRotationComponent
import com.alexpi.awesometanks.game.components.SmoothRotationComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem

@All(BodyComponent::class, SmoothRotationComponent::class, SmoothBodyRotationComponent::class)
class SmoothBodyRotationSystem: IteratingSystem() {
    lateinit var smoothRotationMapper: ComponentMapper<SmoothRotationComponent>
    lateinit var bodyMapper: ComponentMapper<BodyComponent>

    override fun process(entityId: Int) {
        with(bodyMapper[entityId]) {
            body.setTransform(body.position, smoothRotationMapper[entityId].currentAngle)
        }
    }
}