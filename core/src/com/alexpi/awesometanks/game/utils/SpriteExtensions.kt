package com.alexpi.awesometanks.game.utils

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils

fun Sprite.setBounds(bodyComponent: BodyComponent) {
    setBounds(bodyComponent.left() * TILE_SIZE, bodyComponent.bottom() * TILE_SIZE, bodyComponent.width * TILE_SIZE, bodyComponent.height * TILE_SIZE)
}

fun Sprite.setRotation(bodyComponent: BodyComponent) {
    rotation = bodyComponent.body.angle * MathUtils.radiansToDegrees
}