package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.AStartPathFinding
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.utils.GameMap
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.World


//Object that holds references to common game objects to avoid passing them in the constructor every time we need them.
object GameModule{
    var level: Int = 0
    private var assetManager: AssetManager? = null
    private var world: World? = null
    private var gameMap: GameMap? = null
    private var pathFinding: AStartPathFinding? = null
    private var gameValues: Preferences? = null
    private var damageListener: DamageListener? = null
    private var _player: PlayerTank? = null

    fun getAssetManager(): AssetManager = assetManager!!
    fun getWorld(): World = world!!
    fun getGameMap(): GameMap = gameMap!!
    fun getPathFinding(): AStartPathFinding = pathFinding!!
    fun getGameValues(): Preferences = gameValues!!
    fun getDamageListener(): DamageListener? = damageListener
    fun getPlayer(): PlayerTank = _player!!

    fun set(assetManager: AssetManager, world: World, gameMap: GameMap, pathFinding: AStartPathFinding, gameValues: Preferences, damageListener: DamageListener){
        this.assetManager = assetManager
        this.world = world
        this.gameMap = gameMap
        this.pathFinding = pathFinding
        this.gameValues = gameValues
        this.damageListener = damageListener
    }
    fun setPlayer(player: PlayerTank){
        _player = player
    }

    //Calling this method when disposing the GameRenderer is very important to avoid memory leaks
    fun dispose(){
        assetManager = null
        world = null
        gameMap = null
        pathFinding = null
        gameValues = null
        damageListener = null
        _player = null
    }
}