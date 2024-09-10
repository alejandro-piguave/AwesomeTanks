package com.alexpi.awesometanks.entities.components.body

import kotlin.experimental.or

/**
 * Created by Alex on 14/01/2016.
 */

const val CAT_PLAYER_BULLET: Short = 1
const val CAT_ITEM: Short = 2
const val CAT_PLAYER: Short = 4
const val CAT_BLOCK: Short = 8
const val CAT_ENEMY: Short = 16
const val CAT_ENEMY_BULLET: Short = 32
val ENEMY_BULLET_MASK: Short = CAT_PLAYER or CAT_BLOCK
val PLAYER_BULLET_MASK: Short = CAT_ENEMY or CAT_BLOCK

