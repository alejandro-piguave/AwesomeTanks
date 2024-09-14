package com.alexpi.awesometanks.game.tanks.enemy

enum class EnemyWeapon(val valueMultiplier: Float) {
    MINIGUN(0f),
    SHOTGUN(.2f),
    RICOCHET(.4f),
    FLAMETHROWER(.6f),
    CANNON(.6f),
    ROCKETS(.6f),
    LASERGUN(1f),
    RAILGUN(1f)
}