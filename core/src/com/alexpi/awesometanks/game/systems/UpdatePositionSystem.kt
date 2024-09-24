package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem

@All(SpriteComponent::class, BodyComponent::class)
class UpdatePositionSystem: IteratingSystem() {

    lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    lateinit var bodyMapper: ComponentMapper<BodyComponent>

    override fun process(entityId: Int) {
        val bodyComponent = bodyMapper[entityId]
        val sprite = spriteMapper[entityId].sprite

        sprite.setPosition(bodyComponent.left() * TILE_SIZE, bodyComponent.bottom() * TILE_SIZE)
    }
}