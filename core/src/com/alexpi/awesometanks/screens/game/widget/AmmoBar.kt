package com.alexpi.awesometanks.screens.game.widget

import com.alexpi.awesometanks.screens.widget.GameProgressBar
import com.badlogic.gdx.assets.AssetManager

class AmmoBar(assetManager: AssetManager, initialValue: Float = 100f): GameProgressBar(assetManager, 100f, initialValue)