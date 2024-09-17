package com.alexpi.awesometanks.game.blocks

import com.alexpi.awesometanks.game.components.body.BodyShape
import com.alexpi.awesometanks.game.components.body.FixtureFilter
import com.alexpi.awesometanks.game.components.health.HealthComponent
import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.components.healthbar.HealthBarComponent
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions

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
    final override val healthComponent: HealthComponent = HealthComponent(this, gameContext, maxHealth, isFlammable, isFreezable)

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
        if (rumbleOnDeath) gameContext.getRumbleController().rumble(15f, .3f)

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

    override fun onTakeDamage(health: Float) {
        healthBarComponent.updateHealth(health)
    }

    override fun onHeal(health: Float) {
        healthBarComponent.updateHealth(health)
    }

    override fun onDeath() {
        addAction(Actions.removeActor())
    }
}