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
import com.alexpi.awesometanks.listener.DamageListener
import com.alexpi.awesometanks.weapons.Weapon
import com.alexpi.awesometanks.world.ExplosionManager
import com.badlogic.gdx.scenes.scene2d.Group

class MapEntityCreator {
    fun create(
        mapTable: MapTable,
        level: Int,
        player: Player,
        shadeGroup: Group,
        blockGroup: Group,
        entityGroup: Group,
        floorGroup: Group,
        explosionManager: ExplosionManager,
        damageListener: DamageListener? = null
    ) {
        mapTable.forCell { cell ->
            if (!cell.isVisible)
                shadeGroup.addActor(Shade(cell))

            if (cell.value == MapTable.WALL)
                blockGroup.addActor(Wall(cell.toWorldPosition(mapTable)))
            else {
                when (cell.value) {
                    MapTable.START -> {
                        player.setPosition(cell.toWorldPosition(mapTable))
                        entityGroup.addActor(player)
                        player.damageListener = damageListener
                    }

                    MapTable.GATE -> blockGroup.addActor(Gate(cell.toWorldPosition(mapTable)).also { it.damageListener = damageListener })
                    MapTable.BRICKS -> blockGroup.addActor(Bricks(cell.toWorldPosition(mapTable)).also { it.damageListener = damageListener })
                    MapTable.BOX -> entityGroup.addActor(Box(explosionManager, level, cell.toWorldPosition(mapTable)).also { it.damageListener = damageListener })
                    MapTable.SPAWNER -> entityGroup.addActor(
                        Spawner(
                            explosionManager,
                            level,
                            cell.toWorldPosition(mapTable)
                        ).also { it.damageListener = damageListener }
                    )

                    MapTable.BOMB -> blockGroup.addActor(Mine(explosionManager, cell.toWorldPosition(mapTable)).also { it.damageListener = damageListener })

                    in MapTable.bosses -> {
                        val type = cell.value.code - MapTable.MINIGUN_BOSS.code
                        val weaponType = Weapon.Type.values()[type]
                        entityGroup.addActor(
                            EnemyTank(
                                explosionManager,
                                cell.toWorldPosition(mapTable),
                                EnemyTank.Tier.BOSS,
                                weaponType
                            ).also { it.damageListener = damageListener }
                        )
                    }

                    in MapTable.turrets -> {
                        val weaponType = Weapon.Type.values()[Character.getNumericValue(cell.value)]
                        blockGroup.addActor(Turret(explosionManager, cell.toWorldPosition(mapTable), weaponType).also { it.damageListener = damageListener })
                    }

                }

                floorGroup.addActor(Floor(cell.toWorldPosition(mapTable)))
            }
        }
    }



}