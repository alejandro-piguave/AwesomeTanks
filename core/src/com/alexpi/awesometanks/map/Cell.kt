package com.alexpi.awesometanks.map

import com.badlogic.gdx.ai.pfa.Connection

class Cell(val row: Int, val col: Int, var value: Char, var isVisible: Boolean){
    val connections: com.badlogic.gdx.utils.Array<Connection<Cell>> = com.badlogic.gdx.utils.Array()
    override fun toString(): String = "($row, $col)"
}
