package com.alexpi.awesometanks.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import java.io.BufferedReader
import java.io.IOException
import java.util.*


/**
 * Created by Alex on 25/01/2016.
 */

class GameMap(level: Int){
    private val map: Array<CharArray>
    private val visibleArea: Array<BooleanArray>
    private var playerCol: Int = -1
    private var playerRow: Int = -1
    var visualRange: Int = 5

    init {
        val file = Gdx.files.internal("levels/levels.txt")
        val reader = BufferedReader(file.reader())
        var line: String
        val ans = Vector<String>()
        try {
            line = reader.readLine()
            while (!line.contains(level.toString())) line = reader.readLine()
            while (reader.readLine().also { line = it } != null && !line.contains("#")) ans.add(
                line
            )
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        require(!ans.isEmpty()) { "No such level" }
        val cA = ans.firstElement()!!.length
        val rA = ans.size
        map = Array(rA) { CharArray(cA) }
        visibleArea = Array(rA) { BooleanArray(cA) }
        for (i in 0 until rA) {
            if(i == 0 || i == rA -1){
                visibleArea[i] = BooleanArray(cA) { true }
            }
            visibleArea[i][0] = true
            visibleArea[i][cA -1] = true

            if(ans[i].contains(Constants.start)){
                val startX = ans[i].indexOf(Constants.start)
                for(k in -1..1)
                    for(j in -1..1)
                        visibleArea[i + k][startX+j] = true

            }
            map[i] = ans[i].toCharArray()
        }
    }

    fun clear(cell: Cell){
        map[cell.row][cell.col] = Constants.space
    }

    fun setPlayerCell(cell: Cell){
        playerRow = cell.row
        playerCol = cell.col
    }

    fun forCell(predicate: (Int, Int, Char, Boolean) -> Unit){
        for (row in map.indices)
            for (col in 0 until map[row].size)
                predicate(row,col, map[row][col], visibleArea[row][col]) // (row, column)
    }

    fun toWorldPos(row: Int, col: Int): Vector2 {
        return Vector2( col.toFloat(), (map.size - row).toFloat())
    }

    fun toCell(pos: Vector2): Cell {
        return Cell(map.size - pos.y.toInt() -1, pos.x.toInt())
    }

    fun isVisible(row: Int, col: Int) = visibleArea[row][col]

    fun scanCircle(){
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
            visibleArea[oy.toInt()][ox.toInt()] = true //Set the tile to visible.
            if (Constants.solidBlocks.contains(map[oy.toInt()][ox.toInt()] ))
                return
            ox += x
            oy += y

        }
    }

    private fun getSlope(pX1: Double, pY1: Double, pX2: Double, pY2: Double, pInvert: Boolean): Double {
        return if (pInvert) (pY1 - pY2) / (pX1 - pX2) else (pX1 - pX2) / (pY1 - pY2)
    }

    private fun getSlope(pX1: Int, pY1: Int, pX2: Int, pY2: Int, pInvert: Boolean): Double {
        return getSlope(pX1.toDouble(), pY1.toDouble(), pX2.toDouble(), pY2.toDouble(), pInvert)
    }

    private fun getVisDistance(pX1: Int, pY1: Int, pX2: Int, pY2: Int): Int {
        return (pX1 - pX2) * (pX1 - pX2) + (pY1 - pY2) * (pY1 - pY2)
    }
}

data class Cell(val row: Int, val col: Int)
