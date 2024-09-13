package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.actors.HealthOwner
import com.alexpi.awesometanks.entities.actors.ParticleActor
import com.alexpi.awesometanks.entities.actors.RumbleController
import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.entities.components.health.HealthComponent
import com.alexpi.awesometanks.entities.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.map.MapTable
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

abstract class HealthBlock(
    gameContext: GameContext,
    texturePath: String,
    bodyShape: BodyShape,
    position: Vector2, maxHealth: Float,
    isFlammable: Boolean,
    isFreezable: Boolean,
    fixtureFilter: FixtureFilter,
    private val rumbleOnDeath: Boolean = true
) : Block(gameContext, texturePath, bodyShape, position, fixtureFilter), HealthOwner {
    private val mapTable: MapTable = gameContext.getMapTable()
    private val rumbleController: RumbleController = gameContext.getRumbleController()
    private val _healthComponent: HealthComponent =
        HealthComponent(gameContext, maxHealth, isFlammable, isFreezable, onDamageTaken = {
            healthBarComponent.updateHealth(it, 2f)
        }, onDeath = { remove() })
    override val healthComponent: HealthComponent
        get() = _healthComponent
    private val healthBarComponent: HealthBarComponent = HealthBarComponent(
        gameContext,
        _healthComponent.maxHealth,
        _healthComponent.currentHealth
    )

    override fun remove(): Boolean {
        healthBarComponent.hideHealthBar()
        stage.addActor(
            ParticleActor(
                "particles/explosion.party",
                x + width / 2,
                y + height / 2,
                false
            )
        )
        if (rumbleOnDeath) rumbleController.rumble(15f, .3f)

        val cell = mapTable.toCell(bodyComponent.body.position)
        mapTable.clear(cell)
        return super.remove()
    }

    override fun act(delta: Float) {
        super.act(delta)
        _healthComponent.update(this, delta)
        healthBarComponent.updatePosition(this)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        _healthComponent.draw(this, batch)
    }

}