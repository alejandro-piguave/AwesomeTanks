package com.alexpi.awesometanks.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import java.io.BufferedReader
import java.io.IOException
import java.util.*


/**
 * Created by Alex on 25/01/2016.
 */

class GameMap(level: Int){
    private val map: Array<Array<Cell>>
    private var playerCol: Int = -1
    private var playerRow: Int = -1
    var visualRange: Int = 3
    val cellCount: Int

    init {
        val file = Gdx.files.internal("levels/levels.txt")
        val reader = BufferedReader(file.reader())
        var line: String
        val ans = Vector<String>()
        try {
            line = reader.readLine()
            while (!line.contains(level.toString())) line = reader.readLine()
            while (reader.readLine().also { line = it } != null && !line.contains(".")) ans.add(
                line
            )
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        require(!ans.isEmpty()) { "No such level" }
        val cA = ans.firstElement()!!.length
        val rA = ans.size
        cellCount = cA * rA
        map = Array(rA) { row ->
            Array(cA){ col ->
                if(ans[row][col] == START) setPlayerCell(row, col)
                Cell(row, col, ans[row][col], row == 0 || col == 0|| row == rA -1 || col == cA -1 ) // Make the map bounds visible
            }
        }
        updateVisibleArea()

        forCell { cell ->
            if(cell.value in airBlocks){
                forValidNeighbors(cell){ neighbor ->
                    if(neighbor.value in airBlocks){
                        // Add connection to walkable neighbor
                        cell.connections.add(DefaultConnection(cell, neighbor))
                    }
                }
                cell.connections.shuffle()
            }
        }
    }

    fun getIndex(cell: Cell)  = cell.row * map[cell.row].size + cell.col

    fun clear(cell: Cell){
        if(cell.value == AIR) return

        cell.value = AIR
        forValidNeighbors(cell){ neighbor ->
            if(neighbor.value in airBlocks){
                // Add connection to walkable neighbor
                cell.connections.add(DefaultConnection(cell, neighbor))
                val inverseConnection = DefaultConnection(neighbor,cell)
                if(!neighbor.connections.contains(inverseConnection))
                    neighbor.connections.add(inverseConnection)
            }
        }
        cell.connections.shuffle()
        printCellConnections()
    }

    private fun printCellConnections(){
        forCell { cell ->
            println("Cell $cell")
            cell.connections.forEach {
                println("connection from ${it.fromNode} to ${it.toNode}")
            }
        }
    }

    private fun cellConnections(cell: Cell) = if(cell.connections.isEmpty) "" else cell.connections.items.fold("") { a, b->
        a + "${b.toNode?: "empty"}, " }

    fun setPlayerCell(row: Int, col: Int){
        playerRow = row
        playerCol = col
    }

    fun forCell(predicate: (Cell) -> Unit){
        for (row in map.indices)
            for (col in 0 until map[row].size)
                predicate(map[row][col])
    }

    private inline fun forValidNeighbors(cell: Cell, predicate: (Cell) -> Unit){
        for (offset in NEIGHBORHOOD.indices) {
            val neighborCol: Int = cell.col + NEIGHBORHOOD[offset][0]
            val neighborRow: Int = cell.row + NEIGHBORHOOD[offset][1]
            if (neighborRow in map.indices && neighborCol >= 0 && neighborCol < map[0].size) {
                predicate( map[neighborRow][neighborCol])
            }
        }
    }

    fun toWorldPos(row: Int, col: Int): Vector2 {
        return Vector2( col.toFloat(), (map.size - row).toFloat())
    }

    fun toWorldPos(cell: Cell): Vector2 {
        return Vector2( cell.col.toFloat(), (map.size - cell.row).toFloat())
    }

    fun toCell(pos: Vector2): Cell {
        val row = map.size - pos.y.toInt()
        val col = pos.x.toInt()
        return map[row][col]
    }

    fun isVisible(row: Int, col: Int) = map[row][col].isVisible

    fun updateVisibleArea(){
        map[playerRow][playerCol].isVisible = true
        for(i in 0 until 360 step 2){
            val x = MathUtils.cos(i*.01745f)
            val y = MathUtils.sin(i*.01745f)
            doFOV(x,y)
        }
    }

    private fun doFOV(x: Float, y: Float) {
        var ox: Float = playerCol + 0.5f
        var oy: Float = playerRow + 0.5f
        for (i in 0 until visualRange){
            val oxInt = ox.toInt()
            val oyInt = oy.toInt()
            if(oxInt < 0 || oxInt >= map[0].size || oyInt < 0 || oyInt >= map.size)
                return
            map[oy.toInt()][ox.toInt()].isVisible = true //Set the tile to visible.
            if (map[oy.toInt()][ox.toInt()].value in solidBlocks)
                return
            ox += x
            oy += y

        }
    }

    companion object{
        private val NEIGHBORHOOD = arrayOf(intArrayOf(-1, 0), intArrayOf(0, -1), intArrayOf(0, 1), intArrayOf(1, 0))
        const val WALL = 'X'
        const val AIR = ' '
        const val START = 'S'
        const val GATE = '@'
        const val BRICKS = '#'
        const val BOX = '*'
        const val BOMB = 'O'
        const val SPAWNER = '+'
        const val MINIGUN_BOSS = 'A'
        const val SHOTGUN_BOSS = 'B'
        const val RICOCHET_BOSS = 'C'
        const val FLAMETHROWER_BOSS = 'D'
        const val CANON_BOSS = 'E'
        const val ROCKET_BOSS = 'F'
        const val LASERGUN_BOSS = 'G'
        const val RAILGUN_BOSS = 'H'

        val solidBlocks = charArrayOf(WALL, GATE, BRICKS)
        val airBlocks = charArrayOf(AIR, START, SPAWNER)
    }

}

class Cell(val row: Int, val col: Int, var value: Char, var isVisible: Boolean){
    val connections: com.badlogic.gdx.utils.Array<Connection<Cell>> = com.badlogic.gdx.utils.Array()
    override fun toString(): String = "($row, $col)"
}
