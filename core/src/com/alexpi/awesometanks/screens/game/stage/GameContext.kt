package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.data.GameRepository
import com.alexpi.awesometanks.game.manager.ExplosionManager
import com.alexpi.awesometanks.game.manager.RumbleManager
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.tanks.Player
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

class GameContext(private val gameStage: GameStage) {
    fun getWorld(): World = gameStage.world
    fun getAssetManager(): AssetManager = gameStage.assetManager
    fun getHealthBarGroup(): Group = gameStage.healthBarGroup
    fun getExplosionManager(): ExplosionManager = gameStage.explosionManager
    fun getMapTable(): MapTable = gameStage.mapTable
    fun getEntityGroup(): Group = gameStage.entityGroup
    fun getBlockGroup(): Group = gameStage.blockGroup
    fun getRumbleController(): RumbleManager = gameStage.rumbleManager
    fun getStage(): GameStage = gameStage
    fun getPlayer(): Player = gameStage.player
    fun getGameRepository(): GameRepository = gameStage.gameRepository
}