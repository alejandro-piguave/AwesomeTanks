package com.alexpi.awesometanks.world

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.PathFinding
import com.alexpi.awesometanks.entities.tanks.PlayerTank
import com.alexpi.awesometanks.map.GameMap
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.World


//Object that holds references to common game objects to avoid passing them in the constructor every time we need them.
object GameModule {
    private var _assetManager: AssetManager? = null
    private var _world: World? = null
    private var _gameMap: GameMap? = null
    private var _pathFinding: PathFinding? = null
    private var gameValues: Preferences? = null
    private var damageListener: DamageListener? = null
    private var _player: PlayerTank? = null

    var gameMap: GameMap
        get() = _gameMap!!
        set(value) { _gameMap = value}

    var player: PlayerTank get() = _player!!
        set(value) { _player = value }

    var pathFinding: PathFinding get() = _pathFinding!!
        set(value) { _pathFinding = value }

    var assetManager: AssetManager get() = _assetManager!!
        set(value) { _assetManager = value }

    var world: World get() = _world!!
        set(value) { _world = value }

    fun getGameValues(): Preferences = gameValues!!
    fun getDamageListener(): DamageListener? = damageListener

    fun set(gameValues: Preferences, damageListener: DamageListener){
        this.gameValues = gameValues
        this.damageListener = damageListener
    }

    //Calling this method when disposing the GameRenderer is very important to avoid memory leaks
    fun dispose(){
        _assetManager = null
        _world = null
        _gameMap = null
        _pathFinding = null
        gameValues = null
        damageListener = null
        _player = null
    }
}