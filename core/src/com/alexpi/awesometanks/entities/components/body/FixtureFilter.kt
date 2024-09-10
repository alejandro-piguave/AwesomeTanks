package com.alexpi.awesometanks.entities.components.body

import kotlin.experimental.or

enum class FixtureFilter(val categoryBits: Short, val maskBits: Short) {
    PLAYER(CAT_PLAYER, CAT_BLOCK or CAT_ITEM or CAT_ENEMY or CAT_ENEMY_BULLET),
    ENEMY_TANK(CAT_ENEMY, CAT_BLOCK or CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ENEMY),
    BLOCK(CAT_BLOCK, CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ENEMY_BULLET or CAT_ITEM or CAT_ENEMY),
    SPAWNER(CAT_BLOCK, CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ITEM),
    TURRET(CAT_ENEMY, CAT_PLAYER or CAT_PLAYER_BULLET or CAT_ENEMY_BULLET or CAT_ITEM or CAT_ENEMY),
    PLAYER_BULLET(CAT_PLAYER_BULLET, CAT_ENEMY or CAT_BLOCK),
    ENEMY_BULLET(CAT_ENEMY_BULLET, CAT_PLAYER or CAT_BLOCK),
    ITEM(CAT_ITEM, CAT_PLAYER or CAT_BLOCK or CAT_ENEMY),
}