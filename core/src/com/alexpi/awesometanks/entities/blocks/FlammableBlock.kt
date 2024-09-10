package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.actors.FlammableActor
import com.alexpi.awesometanks.entities.components.FlammableComponent
import com.alexpi.awesometanks.entities.components.HealthComponent
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

class FlammableBlock(assetManager: AssetManager, texturePath: String, world: World,
                     bodyShape: BodyShape,
                     position: Vector2, maxHealth: Float): Block(assetManager, texturePath, world, bodyShape, position), FlammableActor {
    private val _healthComponent: HealthComponent = HealthComponent(this, maxHealth)
    private val _flammableComponent = FlammableComponent(assetManager, this, _healthComponent)
    override val flammableComponent: FlammableComponent
        get() = _flammableComponent
    override val healthComponent: HealthComponent
        get() = _healthComponent


}