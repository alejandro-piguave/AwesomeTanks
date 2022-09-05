package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.utils.Cell
import com.alexpi.awesometanks.utils.GameMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.*
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.ai.utils.Collision
import com.badlogic.gdx.ai.utils.Ray
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import kotlin.math.abs

class AStartPathFinding(private val map: GameMap) {
    private val pathfinder: PathFinder<Cell> = IndexedAStarPathFinder(GameMapGraph(map))
    private val heuristic: Heuristic<Cell> = Heuristic<Cell> { node, endNode -> // Manhattan distance
        abs(endNode.row - node.row) + abs(endNode.col - node.col).toFloat()
    }

    fun findNextPosition(source: Vector2, target: Vector2): Vector2? {
        val connectionPath: GraphPath<Connection<Cell>> = DefaultGraphPath()
        val sourceCell = map.toCell(source)
        val targetCell = map.toCell(target)

        pathfinder.searchConnectionPath(sourceCell, targetCell, heuristic, connectionPath)

        return if (connectionPath.count == 0) null else map.toWorldPos(connectionPath[0].toNode).add(.5f, .5f)
    }

}

class GameMapGraph(private val map: GameMap) : IndexedGraph<Cell> {
    override fun getIndex(cell: Cell): Int = map.getIndex(cell)

    override fun getConnections(fromCell: Cell): Array<Connection<Cell>> = fromCell.connections

    override fun getNodeCount(): Int = map.cellCount

}