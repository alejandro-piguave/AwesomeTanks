package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem

@All(BodyComponent::class)
class BodySystem: IteratingSystem() {
    lateinit var bodyMapper: ComponentMapper<BodyComponent>

    override fun process(entityId: Int) { }

    override fun removed(entityId: Int) {
        val body = bodyMapper[entityId].body
        body.world.destroyBody(body)
    }
}