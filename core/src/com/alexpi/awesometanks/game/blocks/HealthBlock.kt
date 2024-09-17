package com.alexpi.awesometanks.game.blocks

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.components.health.HealthComponent
import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.game.manager.RumbleManager
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2

abstract class HealthBlock(
    val gameContext: GameContext,
    texturePath: String,
    bodyShape: BodyShape,
    position: Vector2, maxHealth: Float,
    isFlammable: Boolean,
    isFreezable: Boolean,
    fixtureFilter: FixtureFilter,
    private val rumbleOnDeath: Boolean = true
) : Block(gameContext, texturePath, bodyShape, position, fixtureFilter), HealthOwner {
    private val mapTable: MapTable = gameContext.getMapTable()
    private val rumbleManager: RumbleManager = gameContext.getRumbleController()
    final override val healthComponent: HealthComponent = HealthComponent(gameContext, maxHealth, isFlammable, isFreezable, onHealthChanged = {
        healthBarComponent.updateHealth(it)
    }, onDeath = { remove() })

    private val healthBarComponent: HealthBarComponent = HealthBarComponent(
        gameContext,
        healthComponent.maxHealth,
        healthComponent.currentHealth,
        2f
    )

    override fun remove(): Boolean {
        healthBarComponent.hideHealthBar()
        stage.addActor(
            ParticleActor(
                gameContext,
                "particles/explosion.party",
                x + width / 2,
                y + height / 2,
                false
            )
        )
        if (rumbleOnDeath) rumbleManager.rumble(15f, .3f)

        val cell = mapTable.toCell(bodyComponent.body.position)
        mapTable.clear(cell)
        return super.remove()
    }

    override fun act(delta: Float) {
        super.act(delta)
        healthComponent.update(this, delta)
        if(!healthComponent.isAlive) return
        healthBarComponent.updatePosition(this)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        healthComponent.draw(this, batch)
    }

}