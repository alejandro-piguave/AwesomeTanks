package com.alexpi.awesometanks.game.map

import com.alexpi.awesometanks.screens.TILE_SIZE
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.math.Vector2

class Cell(val row: Int, val col: Int, var value: Char, var isVisible: Boolean){
    val connections: com.badlogic.gdx.utils.Array<Connection<Cell>> = com.badlogic.gdx.utils.Array()

    fun toWorldPosition(mapTable: MapTable): Vector2 {
        return Vector2( col + .5f, mapTable.rows - row - 1 + .5f)
    }

    fun toStagePosition(mapTable: MapTable): Vector2 {
        return Vector2( col * TILE_SIZE, (mapTable.rows - row - 1) * TILE_SIZE)
    }
}
