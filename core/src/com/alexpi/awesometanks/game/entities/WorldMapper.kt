package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.screens.game.GameScreen

fun GameScreen.buildLevelMap(){
    mapTable.forCell { cell ->
        if(cell.value == MapTable.WALL) {
            gameWorld.createWall(game.manager, physicsWorld,  cell.toWorldPosition(mapTable))
        } else {
            when (cell.value) {
                MapTable.START -> {
                }

                MapTable.GATE -> gameWorld.createHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/gate.png")
                MapTable.BRICKS -> gameWorld.createHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/bricks.png")
                MapTable.BOX -> gameWorld.createHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/box.png")
                MapTable.SPAWNER -> gameWorld.createHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/spawner.png")
                MapTable.BOMB -> gameWorld.createHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/mine.png", .5f)

            }
            gameWorld.createGroundTile(game.manager, cell.toStagePosition(mapTable))
        }
    }
}