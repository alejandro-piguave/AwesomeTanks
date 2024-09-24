package com.alexpi.awesometanks.game.components

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class BodyComponent: Component() {
    lateinit var body: Body
    var width = 0f
    var height = 0f

    fun left() = body.position.x - width/2
    fun bottom() = body.position.y - height/2
}