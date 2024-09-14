package com.alexpi.awesometanks.screens.game.stage

import com.alexpi.awesometanks.game.tiles.Floor
import com.alexpi.awesometanks.game.tiles.Shade
import com.alexpi.awesometanks.game.blocks.Box
import com.alexpi.awesometanks.game.blocks.Bricks
import com.alexpi.awesometanks.game.blocks.Gate
import com.alexpi.awesometanks.game.blocks.Mine
import com.alexpi.awesometanks.game.blocks.Spawner
import com.alexpi.awesometanks.game.blocks.Turret
import com.alexpi.awesometanks.game.blocks.Wall
import com.alexpi.awesometanks.game.tanks.EnemyTank
import com.alexpi.awesometanks.game.map.MapTable
import com.alexpi.awesometanks.game.weapons.Weapon


fun GameStage.createMap() {
    mapTable.forCell { cell ->
        if (!cell.isVisible)
            shadeGroup.addActor(Shade(gameContext, cell))

        if (cell.value == MapTable.WALL)
            blockGroup.addActor(Wall(gameContext, cell.toWorldPosition(mapTable)))
        else {
            when (cell.value) {
                MapTable.START -> {
                    player.setPosition(cell.toWorldPosition(mapTable))
                    entityGroup.addActor(player)
                }

                MapTable.GATE -> blockGroup.addActor(Gate(gameContext, cell.toWorldPosition(mapTable)))
                MapTable.BRICKS -> blockGroup.addActor(Bricks(gameContext, cell.toWorldPosition(mapTable)))
                MapTable.BOX -> entityGroup.addActor(Box(gameContext, level, cell.toWorldPosition(mapTable)))
                MapTable.SPAWNER -> entityGroup.addActor(
                    Spawner(
                        gameContext,
                        level,
                        cell.toWorldPosition(mapTable)
                    )
                )

                MapTable.BOMB -> blockGroup.addActor(Mine(gameContext, cell.toWorldPosition(mapTable)))

                in MapTable.bosses -> {
                    val type = cell.value.code - MapTable.MINIGUN_BOSS.code
                    val weaponType = Weapon.Type.values()[type]
                    entityGroup.addActor(
                        EnemyTank(
                            gameContext,
                            cell.toWorldPosition(mapTable),
                            EnemyTank.Tier.BOSS,
                            weaponType
                        )
                    )
                }

                in MapTable.turrets -> {
                    val weaponType = Weapon.Type.values()[Character.getNumericValue(cell.value)]
                    blockGroup.addActor(Turret(gameContext, cell.toWorldPosition(mapTable), weaponType))
                }

            }

            floorGroup.addActor(Floor(gameContext, cell))
        }
    }

    addActor(floorGroup)
    addActor(entityGroup)
    addActor(blockGroup)
    addActor(shadeGroup)
    addActor(healthBarGroup)
    addActor(rumbleManager)
    addActor(explosionManager)
}