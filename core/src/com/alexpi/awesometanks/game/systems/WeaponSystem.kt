package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.alexpi.awesometanks.game.components.WeaponComponent
import com.alexpi.awesometanks.game.utils.setBounds
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem

@All(WeaponComponent::class, BodyComponent::class)
class WeaponSystem: IteratingSystem() {
    lateinit var spriteMapper: ComponentMapper<SpriteComponent>
    lateinit var weaponMapper: ComponentMapper<WeaponComponent>
    lateinit var bodyMapper: ComponentMapper<BodyComponent>

    override fun process(entityId: Int) {
        spriteMapper[weaponMapper[entityId].weaponId].sprite.setBounds(bodyMapper[entityId])
    }
}