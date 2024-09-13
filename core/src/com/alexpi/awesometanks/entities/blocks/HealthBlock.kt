package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.actors.HealthActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.health.HealthComponent
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

class HealthBlock(
    assetManager: AssetManager, texturePath: String, world: World,
    bodyShape: BodyShape,
    position: Vector2, maxHealth: Float
) : Block(assetManager, texturePath, world, bodyShape, position), HealthActor {
    private val _healthComponent: HealthComponent = HealthComponent(assetManager, this, maxHealth)
    override val healthComponent: HealthComponent
        get() = _healthComponent


    override fun act(delta: Float) {
        super.act(delta)
        _healthComponent.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        _healthComponent.draw(batch)
    }

}