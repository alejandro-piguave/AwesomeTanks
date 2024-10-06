package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.tags.Tags
import com.alexpi.awesometanks.screens.game.GameScreen

fun GameScreen.buildLevelMap(){
    mapTable.forCell { cell ->
        if(cell.value == MapTable.WALL) {
            gameWorld.createWall(game.manager, physicsWorld,  cell.toWorldPosition(mapTable))
        } else {
            when (cell.value) {
                MapTable.START -> {
                    val playerId = gameWorld.createPlayer(game.manager, physicsWorld, cell.toWorldPosition(mapTable), game.gameRepository)
                    tagManager.register(Tags.PLAYER, playerId)
                }

                MapTable.GATE -> gameWorld.createSquareHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/blocks/gate.png", 1f)
                MapTable.BRICKS -> gameWorld.createSquareHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/blocks/bricks.png",1f)
                MapTable.BOX -> gameWorld.createSquareHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/blocks/box.png", .8f)
                MapTable.SPAWNER -> gameWorld.createSquareHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/blocks/spawner.png", 1f)
                MapTable.BOMB -> gameWorld.createCircularHealthBlock(game.manager, physicsWorld, cell.toWorldPosition(mapTable), "sprites/blocks/mine.png", .25f)

            }
            gameWorld.createGroundTile(game.manager, cell.toStagePosition(mapTable))
        }
    }
}