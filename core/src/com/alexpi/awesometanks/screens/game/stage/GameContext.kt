package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.entities.actors.RumbleController
import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.map.MapTable
import com.alexpi.awesometanks.world.ExplosionManager
import com.badlogic.gdx.Preferences
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
    fun getRumbleController(): RumbleController = gameStage.rumbleController
    fun getStage(): GameStage = gameStage
    fun getPlayer(): Player = gameStage.player
    fun getGameValues(): Preferences = gameStage.gameValues
}