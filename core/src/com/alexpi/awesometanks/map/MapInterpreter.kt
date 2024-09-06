package com.alexpi.awesometanks.map

import com.alexpi.awesometanks.entities.actors.Floor
import com.alexpi.awesometanks.entities.actors.Shade
import com.alexpi.awesometanks.entities.blocks.Box
import com.alexpi.awesometanks.entities.blocks.Bricks
import com.alexpi.awesometanks.entities.blocks.Gate
import com.alexpi.awesometanks.entities.blocks.Mine
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.blocks.Turret
import com.alexpi.awesometanks.entities.blocks.Wall
import com.alexpi.awesometanks.entities.tanks.EnemyTank
import com.alexpi.awesometanks.entities.tanks.Player
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.scenes.scene2d.Group

class MapInterpreter {
    fun interpret(
        gameMap: GameMap,
        level: Int,
        player: Player,
        shadeGroup: Group,
        blockGroup: Group,
        entityGroup: Group,
        floorGroup: Group
    ) {
        gameMap.forCell { cell ->
            if (!cell.isVisible)
                shadeGroup.addActor(Shade(cell))

            if (cell.value == GameMap.WALL)
                blockGroup.addActor(Wall(gameMap.toWorldPos(cell)))
            else {
                when (cell.value) {
                    GameMap.START -> {
                        player.setPos(cell)
                        entityGroup.addActor(player)
                    }

                    GameMap.GATE -> blockGroup.addActor(Gate(gameMap.toWorldPos(cell)))
                    GameMap.BRICKS -> blockGroup.addActor(Bricks(gameMap.toWorldPos(cell)))
                    GameMap.BOX -> entityGroup.addActor(Box(level, gameMap.toWorldPos(cell)))
                    GameMap.SPAWNER -> entityGroup.addActor(
                        Spawner(
                            level,
                            gameMap.toWorldPos(cell)
                        )
                    )

                    GameMap.BOMB -> blockGroup.addActor(Mine(gameMap.toWorldPos(cell)))

                    in GameMap.bosses -> {
                        val type = cell.value.code - GameMap.MINIGUN_BOSS.code
                        val weaponType = Weapon.Type.values()[type]
                        entityGroup.addActor(
                            EnemyTank(
                                gameMap.toWorldPos(cell),
                                EnemyTank.Tier.BOSS,
                                weaponType
                            )
                        )
                    }

                    in GameMap.turrets -> {
                        val weaponType = Weapon.Type.values()[Character.getNumericValue(cell.value)]
                        blockGroup.addActor(Turret(gameMap.toWorldPos(cell), weaponType))
                    }

                }

                floorGroup.addActor(Floor(gameMap.toWorldPos(cell)))
            }
        }
    }
}