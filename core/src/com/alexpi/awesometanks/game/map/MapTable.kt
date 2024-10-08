package com.alexpi.awesometanks.game.map

import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2


/**
 * Created by Alex on 25/01/2016.
 */

class MapTable(charMap: Array<CharArray>) {
    private val map: Array<Array<Cell>>
    val cellCount: Int


    val rows: Int get() = map.size
    val columns: Int get() = map.first().size

    init {
        val cA = charMap.first().size
        val rA = charMap.size
        cellCount = cA * rA

        var playerRow = -1
        var playerColumn = -1
        map = Array(rA) { row ->
            Array(cA){ col ->
                if(charMap[row][col] == START) {
                    playerRow = row
                    playerColumn = col
                }
                Cell(row, col, charMap[row][col], row == 0 || col == 0|| row == rA -1 || col == cA -1 ) // Make the map bounds visible
            }
        }

        if(playerRow != -1 && playerColumn != -1) updateVisibleArea(playerRow, playerColumn,3)

        forCell { cell ->
            if(cell.value in emptyBlocks){
                forValidNeighbors(cell){ neighbor ->
                    if(neighbor.value in emptyBlocks){
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
            if(neighbor.value in emptyBlocks){
                // Add connection to walkable neighbor
                cell.connections.add(DefaultConnection(cell, neighbor))
                val inverseConnection = DefaultConnection(neighbor,cell)
                if(!neighbor.connections.contains(inverseConnection))
                    neighbor.connections.add(inverseConnection)
            }
        }
        cell.connections.shuffle()
    }

    fun forCell(predicate: (Cell) -> Unit){
        for (row in map.indices)
            for (col in 0 until map[row].size)
                predicate(map[row][col])
    }

    fun getRandomEmptyAdjacentCell(cell: Cell, radius: Int = 1) : Cell {
        var col: Int = -1
        var row: Int = -1
        while (row < 0 || row >= map.size || col < 0 || col >= map[0].size
            || (col == cell.col && row == cell.row)
            || map[row][col].value in solidBlocks
        ){
            val colOffset = MathUtils.randomSign() * MathUtils.random(radius)
            val rowOffset = MathUtils.randomSign() * MathUtils.random(radius)
            col = cell.col + colOffset
            row = cell.row + rowOffset
        }
        return map[row][col]
    }

    private inline fun forValidNeighbors(cell: Cell, predicate: (Cell) -> Unit){
        for (offset in NEIGHBORHOOD) {
            val neighborCol: Int = cell.col + offset[0]
            val neighborRow: Int = cell.row + offset[1]
            if (neighborRow in map.indices && neighborCol >= 0 && neighborCol < map[0].size) {
                predicate( map[neighborRow][neighborCol])
            }
        }
    }

    fun toCell(pos: Vector2): Cell {
        val row = map.size - 1 - pos.y.toInt()
        val col = pos.x.toInt()
        return map[row][col]
    }

    fun updateVisibleArea(playerRow: Int, playerColumn: Int, visualRange: Int){
        map[playerRow][playerColumn].isVisible = true
        for(i in 0 until 360 step 2){
            val x = MathUtils.cos(i*.01745f)
            val y = MathUtils.sin(i*.01745f)
            doFOV(playerRow, playerColumn, x,y, visualRange)
        }
    }

    private fun doFOV(playerRow: Int, playerColumn: Int, x: Float, y: Float, visualRange: Int) {
        var ox: Float = playerColumn + 0.5f
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
        const val CANNON_BOSS = 'E'
        const val ROCKET_BOSS = 'F'
        const val LASERGUN_BOSS = 'G'
        const val RAILGUN_BOSS = 'H'

        const val MINGUN_TURRET = '1'
        const val SHOTGUN_TURRET = '2'
        const val RICOCHET_TURRET = '3'
        const val FLAMETHROWER_TURRET = '4'
        const val CANNON_TURRET = '5'
        const val ROCKET_TURRET = '6'
        const val LASERGUN_TURRET = '7'
        const val RAILGUN_TURRET = '8'



        val bosses = charArrayOf(MINIGUN_BOSS, SHOTGUN_BOSS, RICOCHET_BOSS, FLAMETHROWER_BOSS, CANNON_BOSS, ROCKET_BOSS, LASERGUN_BOSS, RAILGUN_BOSS)
        val turrets = charArrayOf(MINGUN_TURRET, SHOTGUN_TURRET, RICOCHET_TURRET, FLAMETHROWER_TURRET, CANNON_TURRET, ROCKET_TURRET, LASERGUN_TURRET, RAILGUN_TURRET)
        val solidBlocks = charArrayOf(WALL, GATE, BRICKS)
        val emptyBlocks = charArrayOf(AIR, START, SPAWNER, MINIGUN_BOSS, SHOTGUN_BOSS, RICOCHET_BOSS, FLAMETHROWER_BOSS, CANNON_BOSS, ROCKET_BOSS, LASERGUN_BOSS, RAILGUN_BOSS)
    }

}

