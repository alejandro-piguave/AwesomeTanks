package com.alexpi.awesometanks.game.tanks.enemy

import com.badlogic.gdx.graphics.Color

enum class EnemyTier(val size: Float, val color: Color, val power: Int) {
    MINI(.6f, Color.SKY, 0), NORMAL(.75f, Color.GOLD, 1), BOSS(.87f, Color.RED, 3)
}