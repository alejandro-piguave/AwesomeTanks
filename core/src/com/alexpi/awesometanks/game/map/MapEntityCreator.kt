package com.alexpi.awesometanks.game.map

import com.alexpi.awesometanks.game.blocks.Box
import com.alexpi.awesometanks.game.blocks.Bricks
import com.alexpi.awesometanks.game.blocks.Gate
import com.alexpi.awesometanks.game.blocks.Mine
import com.alexpi.awesometanks.game.blocks.Spawner
import com.alexpi.awesometanks.game.blocks.Wall
import com.alexpi.awesometanks.game.blocks.turret.Turret
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTank
import com.alexpi.awesometanks.game.tanks.enemy.EnemyTier
import com.alexpi.awesometanks.game.tanks.enemy.EnemyType
import com.alexpi.awesometanks.game.tanks.enemy.EnemyWeapon
import com.alexpi.awesometanks.game.tiles.Floor
import com.alexpi.awesometanks.game.tiles.Shade
import com.alexpi.awesometanks.screens.game.stage.GameStage


fun GameStage.createMap() {
    mapTable.forCell { cell ->
        if (!cell.isVisible)
            shadeGroup.addActor(Shade(gameContext, cell))

        if (cell.value == MapTable.WALL)
            blockGroup.addActor(Wall(gameContext, cell.toWorldPosition(mapTable)))
        else {
            when (cell.value) {
                MapTable.START -> {
                    playerTank.setPosition(cell.toWorldPosition(mapTable))
                    entityGroup.addActor(playerTank)
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
                    val weaponType = EnemyWeapon.values()[type]
                    entityGroup.addActor(
                        EnemyTank(
                            gameContext,
                            cell.toWorldPosition(mapTable),
                            EnemyType(EnemyTier.BOSS, weaponType)
                        )
                    )
                }

                in MapTable.turrets -> {
                    val weaponType = EnemyWeapon.values()[Character.getNumericValue(cell.value)]
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