package com.alexpi.awesometanks.utils

import com.badlogic.gdx.Gdx
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
        return Cell(map.size - pos.y.toInt(), pos.x.toInt())
    }

    fun isVisible(row: Int, col: Int) = visibleArea[row][col]
    //  Octant data
    //
    //    \ 1 | 2 /
    //   8 \  |  / 3
    //   -----+-----
    //   7 /  |  \ 4
    //    / 6 | 5 \
    //
    //  1 = NNW, 2 =NNE, 3=ENE, 4=ESE, 5=SSE, 6=SSW, 7=WSW, 8 = WNW
    fun scanOctant(pDepth: Int, pOctant: Int, pStartSlope: Double, pEndSlope: Double){
        val visualRange2 = visualRange * visualRange
        var pStartSlopeAux = pStartSlope
        var x = 0
        var y = 0
        when(pOctant){
            //nnw
            1 -> {
                y = playerRow - pDepth
                if(y < 0 ) return
                x = playerCol - (pStartSlopeAux * pDepth).toInt()
                if(x < 0 ) x = 0

                while(getSlope(x, y, playerCol, playerRow,false) >= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (y + 1 < map.size && !Constants.solidBlocks.contains(map[y + 1][x])){

                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x - 0.5, y + 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), false));
                            }
                            visibleArea[y][x] = true
                        } else{

                            if (x - 1 >= 0 && Constants.solidBlocks.contains(map[y][x-1])){

                                pStartSlopeAux = getSlope(x - 0.5, y - 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), false);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    x++
                }

                x--
            }
            //nne
            2 -> {
                y = playerRow - pDepth
                if(y < 0) return

                x = playerCol + (pStartSlopeAux * pDepth).toInt()
                if(x >= map[0].size ) x = map[0].size -1

                while(getSlope(x, y, playerCol, playerRow,false) <= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (x + 1 < map[0].size && !Constants.solidBlocks.contains(map[y][x+1])){

                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x + 0.5, y + 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), false));
                            }
                            visibleArea[y][x] = true

                        } else{

                            if (x + 1 < map[0].size && Constants.solidBlocks.contains(map[y][x+1])){

                                pStartSlopeAux = -getSlope(x + 0.5, y - 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), false);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    x--
                }

                x++
            }
            3 -> {
                x = playerCol + pDepth
                if(x >= map[0].size ) return

                y = playerRow - (pStartSlopeAux * pDepth).toInt()
                if(y < 0) y = 0

                while(getSlope(x, y, playerCol, playerRow,true) <= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){
                            if (y - 1 >= 0 && !Constants.solidBlocks.contains(map[y-1][x])){
                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x - 0.5, y - 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), true));
                            }
                            visibleArea[y][x] = true

                        } else{
                            if (y - 1 >= 0 && Constants.solidBlocks.contains(map[y-1][x])){
                                pStartSlopeAux = -getSlope(x + 0.5, y - 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), true);
                            }
                            visibleArea[y][x] = true
                        }
                    }
                    y++
                }
                y--
            }
            4 -> {
                x = playerCol + pDepth
                if(x >= map[0].size ) return

                y = playerRow + (pStartSlopeAux * pDepth).toInt()
                if(y >= map.size) y = map.size -1

                while(getSlope(x, y, playerCol, playerRow,true) >= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){
                            if (y + 1 < map.size && !Constants.solidBlocks.contains(map[y+1][x])){
                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x - 0.5, y + 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), true));
                            }
                            visibleArea[y][x] = true

                        } else{
                            if (y + 1 < map.size && Constants.solidBlocks.contains(map[y+1][x])){
                                pStartSlopeAux = getSlope(x + 0.5, y + 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), true);
                            }
                            visibleArea[y][x] = true
                        }
                    }
                    y--
                }
                y++
            }
            5 -> {
                y = playerRow + pDepth
                if(y >= map.size) return

                x = playerCol + (pStartSlopeAux * pDepth).toInt()
                if(x >= map[0].size ) x = map[0].size -1

                while(getSlope(x, y, playerCol, playerRow,false) >= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (x + 1 < map[0].size && !Constants.solidBlocks.contains(map[y][x+1])){

                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x + 0.5, y - 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), false));
                            }
                            visibleArea[y][x] = true

                        } else{

                            if (x + 1 < map[0].size && Constants.solidBlocks.contains(map[y][x+1])){

                                pStartSlopeAux = getSlope(x + 0.5, y + 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), false);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    x--
                }

                x++
            }
            6 -> {
                y = playerRow + pDepth
                if(y >= map.size) return

                x = playerCol - (pStartSlopeAux * pDepth).toInt()
                if(x < 0 ) x = 0

                while(getSlope(x, y, playerCol, playerRow,false) <= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (y + 1 < map.size && !Constants.solidBlocks.contains(map[y + 1][x])){
                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x - 0.5, y + 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), false));
                            }
                            visibleArea[y][x] = true

                        } else{

                            if (x - 1 >= 0 && Constants.solidBlocks.contains(map[y][x-1])){
                                pStartSlopeAux = -getSlope(x - 0.5, y + 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), false);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    x++
                }

                x--
            }
            7 -> {
                x = playerCol - pDepth
                if(x <0) return
                y = playerRow + (pStartSlope * pDepth).toInt()
                if(y >= map.size) y = map.size -1

                while(getSlope(x, y, playerCol, playerRow,true) <= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (y + 1 < map.size && !Constants.solidBlocks.contains(map[y+1][x])){
                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x + 0.5, y + 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), true));
                            }
                            visibleArea[y][x] = true

                        } else{

                            if (y + 1 < map.size && Constants.solidBlocks.contains(map[y+1][x])){

                                pStartSlopeAux = -getSlope(x - 0.5, y + 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), true);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    y--
                }

                y++
            }
            //wnw
            8 -> {
                x = playerCol - pDepth
                if(x < 0 ) return

                y = playerRow - (pStartSlope * pDepth).toInt()
                if(y < 0) y = 0

                while(getSlope(x.toDouble(), y.toDouble(), playerCol.toDouble(), playerRow.toDouble(),true) >= pEndSlope){

                    if(getVisDistance(x,y,playerCol,playerRow) <= visualRange2){

                        if(Constants.solidBlocks.contains(map[y][x])){

                            if (y - 1 >= 0 && !Constants.solidBlocks.contains(map[y-1][x])){

                                scanOctant(pDepth + 1, pOctant, pStartSlopeAux, getSlope(x + 0.5, y - 0.5,
                                    playerCol.toDouble(),
                                    playerRow.toDouble(), true));
                            }
                            visibleArea[y][x] = true

                        } else{

                            if (y - 1 >= 0 && Constants.solidBlocks.contains(map[y-1][x])){

                                pStartSlopeAux = getSlope(x - 0.5, y - 0.5,
                                    playerCol.toDouble(), playerRow.toDouble(), true);
                            }

                            visibleArea[y][x] = true
                        }
                    }

                    y++
                }

                y--
            }
        }


        if (x < 0)
            x = 0;
        else if (x >= map[0].size)
            x = map[0].size - 1

        if (y < 0)
            y = 0;
        else if (y >= map.size)
            y = map.size - 1

        if (pDepth < visualRange && !Constants.solidBlocks.contains(map[y][x]))
            scanOctant(pDepth + 1, pOctant, pStartSlopeAux, pEndSlope);
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
