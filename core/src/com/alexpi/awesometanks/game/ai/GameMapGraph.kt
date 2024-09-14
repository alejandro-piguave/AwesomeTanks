package com.alexpi.awesometanks.game.ai

import com.alexpi.awesometanks.game.map.Cell
import com.alexpi.awesometanks.game.map.MapTable
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Array

class GameMapGraph(private val map: MapTable) : IndexedGraph<Cell> {
    override fun getIndex(cell: Cell): Int = map.getIndex(cell)

    override fun getConnections(fromCell: Cell): Array<Connection<Cell>> = fromCell.connections

    override fun getNodeCount(): Int = map.cellCount

}