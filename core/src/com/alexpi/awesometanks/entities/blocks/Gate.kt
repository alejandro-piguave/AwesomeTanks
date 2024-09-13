package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 29/01/2016.
 */
class Gate(gameContext: GameContext, pos: Vector2) :
    HealthBlock(gameContext,"sprites/gate.png", BodyShape.Box(1f, 1f), pos, 100f, true, false, FixtureFilter.BLOCK) {

    override fun remove(): Boolean {
        destroyNeighboringGates()
        return super.remove()
    }

    private fun destroyNeighboringGates() {
        bodyComponent.body.world.QueryAABB({
            if (it.userData is Gate) {
                val gate = it.userData as Gate
                if (gate.healthComponent.isAlive) {
                    gate.healthComponent.kill()
                }
            }
            true
        }, bodyComponent.body.position.x - 1f, bodyComponent.body.position.y - 1f, bodyComponent.body.position.x + 1f, bodyComponent.body.position.y + 1f)
    }
}