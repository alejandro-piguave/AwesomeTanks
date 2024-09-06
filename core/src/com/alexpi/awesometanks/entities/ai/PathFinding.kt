package com.alexpi.awesometanks.entities.ai

import com.alexpi.awesometanks.map.Cell
import com.alexpi.awesometanks.map.GameMap
import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.ai.pfa.PathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

class PathFinding(private val map: GameMap) {
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

    fun findPath(source: Cell, target: Cell): GraphPath<Connection<Cell>> {
        val connectionPath: GraphPath<Connection<Cell>> = DefaultGraphPath()

        pathfinder.searchConnectionPath(source, target, heuristic, connectionPath)
        return connectionPath

    }
}

