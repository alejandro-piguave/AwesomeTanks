package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.screens.game.GameScreen

fun GameScreen.buildLevelMap(){
    mapTable.forCell { cell ->
        if(cell.value == MapTable.WALL) {
            gameWorld.createWall(game.manager, physicsWorld,  cell.toWorldPosition(mapTable))
        } else {
            gameWorld.createGroundTile(game.manager, cell.toStagePosition(mapTable))
        }
    }
}