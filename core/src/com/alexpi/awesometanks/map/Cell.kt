package com.alexpi.awesometanks.map

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.math.Vector2

class Cell(val row: Int, val col: Int, var value: Char, var isVisible: Boolean){
    val connections: com.badlogic.gdx.utils.Array<Connection<Cell>> = com.badlogic.gdx.utils.Array()

    fun toWorldPosition(mapTable: MapTable): Vector2 {
        return Vector2( col.toFloat(), (mapTable.rows - row).toFloat())
    }
}
