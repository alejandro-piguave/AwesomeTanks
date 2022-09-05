package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape

/**
 * Created by Alex on 29/01/2016.
 */
class Gate(listener: DamageListener, pos: Vector2) : Block(
    "sprites/gate.png", Shape.Type.Polygon, 100f, pos, 1f, true, listener
) {

    override fun detach() {
        destroyNeighboringGates()
        super.detach()
    }

    private fun destroyNeighboringGates(){
        body.world.QueryAABB({
            if(it.userData is Gate){
                val gate = it.userData as Gate
                if(gate.isAlive) {
                    gate.killInstantly()
                }
            }
            true
        }, body.position.x - 1f, body.position.y - 1f, body.position.x + 1f, body.position.y + 1f)
    }
}