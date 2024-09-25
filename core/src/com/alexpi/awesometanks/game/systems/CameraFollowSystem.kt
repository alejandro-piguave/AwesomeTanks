package com.alexpi.awesometanks.game.systems

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.tags.Tags
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.managers.TagManager


class CameraFollowSystem: BaseSystem() {
    lateinit var tagManager: TagManager
    lateinit var renderSystem: RenderSystem
    lateinit var bodyMapper: ComponentMapper<BodyComponent>
    override fun processSystem() {
        val playerId = tagManager.getEntityId(Tags.PLAYER)
        val playerPosition = bodyMapper[playerId].body.position
        renderSystem.setCameraPosition(playerPosition.x * TILE_SIZE, playerPosition.y * TILE_SIZE)
    }

}
