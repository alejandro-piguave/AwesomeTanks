package com.alexpi.awesometanks.game.module

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.physics.box2d.World


//Object that holds references to common game objects to avoid passing them in the constructor every time we need them.
object GameModule {
    private var _assetManager: AssetManager? = null
    private var _world: World? = null

    var assetManager: AssetManager get() = _assetManager!!
        set(value) { _assetManager = value }

    var world: World get() = _world!!
        set(value) { _world = value }

    //Calling this method when disposing the GameRenderer is very important to avoid memory leaks
    fun dispose(){
        _assetManager = null
        _world = null
    }
}