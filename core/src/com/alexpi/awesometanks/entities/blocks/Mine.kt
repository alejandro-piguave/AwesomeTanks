package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.components.body.BodyShape
import com.alexpi.awesometanks.entities.components.body.FixtureFilter
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.math.Vector2

/**
 * Created by Alex on 20/02/2016.
 */
class Mine(gameContext: GameContext, pos: Vector2) : HealthBlock(gameContext, "sprites/mine.png", BodyShape.Circular(.25f), pos, 150f, true, false, FixtureFilter.BLOCK, false) {
    private val explosionManager = gameContext.getExplosionManager()
    private fun explode() {
        val mineX = bodyComponent.body.position.x
        val mineY = bodyComponent.body.position.y
        explosionManager.createLandMineExplosion(mineX, mineY)
    }


    override fun remove(): Boolean {
        explode()
        return super.remove()
    }
}