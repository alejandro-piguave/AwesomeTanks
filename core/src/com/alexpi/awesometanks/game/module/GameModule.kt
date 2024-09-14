package com.alexpi.awesometanks.game.module

import com.alexpi.awesometanks.game.ai.PathFinding
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.tanks.Player
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.World


//Object that holds references to common game objects to avoid passing them in the constructor every time we need them.
object GameModule {
    private var _assetManager: AssetManager? = null
    private var _world: World? = null
    private var _mapTable: MapTable? = null
    private var _pathFinding: PathFinding? = null
    private var gameValues: Preferences? = null
    private var _player: Player? = null

    var mapTable: MapTable
        get() = _mapTable!!
        set(value) { _mapTable = value}

    var player: Player get() = _player!!
        set(value) { _player = value }

    var pathFinding: PathFinding get() = _pathFinding!!
        set(value) { _pathFinding = value }

    var assetManager: AssetManager get() = _assetManager!!
        set(value) { _assetManager = value }

    var world: World get() = _world!!
        set(value) { _world = value }

    fun getGameValues(): Preferences = gameValues!!

    fun set(gameValues: Preferences){
        GameModule.gameValues = gameValues
    }

    //Calling this method when disposing the GameRenderer is very important to avoid memory leaks
    fun dispose(){
        _assetManager = null
        _world = null
        _mapTable = null
        _pathFinding = null
        gameValues = null
        _player = null
    }
}