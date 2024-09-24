package com.alexpi.awesometanks.game.entities

import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.screens.game.GameScreen

fun GameScreen.buildMap(){
    mapTable.forCell { cell ->
        if(cell.value == MapTable.WALL) {

        } else {
            gameWorld.createTile(game.manager, "sprites/sand.png", cell.toStagePosition(mapTable))
        }
    }
}