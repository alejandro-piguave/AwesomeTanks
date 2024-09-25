package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.MultiSpriteComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.game.utils.setBounds
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem

@All(BodyComponent::class, MultiSpriteComponent::class)
class BodyMultiSpriteSystem: IteratingSystem() {
    lateinit var bodyMapper: ComponentMapper<BodyComponent>
    lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    lateinit var multiSpriteMapper: ComponentMapper<MultiSpriteComponent>

    override fun process(entityId: Int) {
        spriteMapper[multiSpriteMapper[entityId].bodySpriteId].sprite.setBounds(bodyMapper[entityId])
        spriteMapper[multiSpriteMapper[entityId].wheelsSpriteId].sprite.setBounds(bodyMapper[entityId])
    }
}