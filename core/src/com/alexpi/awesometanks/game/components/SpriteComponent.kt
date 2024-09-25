package com.alexpi.awesometanks.game.components

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.Sprite

class SpriteComponent: Component() {
    lateinit var sprite: Sprite
    lateinit var layer: Layer


    enum class Layer {
        FLOOR, ENTITY, BLOCK, HEALTH_BAR, SHADE
    }
}