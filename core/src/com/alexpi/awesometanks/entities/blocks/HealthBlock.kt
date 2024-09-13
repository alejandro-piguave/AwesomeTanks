package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.actors.HealthActor
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.health.HealthComponent
import com.alexpi.awesometanks.entities.components.healthbar.HealthBarComponent
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

class HealthBlock(
    assetManager: AssetManager, texturePath: String, world: World,
    healthBarGroup: Group,
    bodyShape: BodyShape,
    position: Vector2, maxHealth: Float
) : Block(assetManager, texturePath, world, bodyShape, position), HealthActor {
    private val _healthComponent: HealthComponent =
        HealthComponent(assetManager, maxHealth, onDamageTaken = {
            healthBarComponent.updateHealth(it)
        })
    override val healthComponent: HealthComponent
        get() = _healthComponent
    private val healthBarComponent: HealthBarComponent = HealthBarComponent(
        healthBarGroup,
        _healthComponent.maxHealth,
        _healthComponent.currentHealth
    )

    override fun act(delta: Float) {
        super.act(delta)
        _healthComponent.update(this, delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        _healthComponent.draw(this, batch)
    }

}