package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.SmoothRotationComponent
import com.alexpi.awesometanks.game.components.SmoothSpriteRotationComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils

@All(SpriteComponent::class, SmoothRotationComponent::class, SmoothSpriteRotationComponent::class)
class SmoothSpriteRotationSystem: IteratingSystem() {
    lateinit var smoothRotationMapper: ComponentMapper<SmoothRotationComponent>
    lateinit var spriteMapper: ComponentMapper<SpriteComponent>

    override fun process(entityId: Int) {
        spriteMapper[entityId].sprite.rotation = smoothRotationMapper[entityId].currentAngle * MathUtils.radiansToDegrees
    }
}