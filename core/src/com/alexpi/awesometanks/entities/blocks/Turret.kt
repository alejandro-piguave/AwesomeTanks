package com.alexpi.awesometanks.entities.blocks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.EnemyAI
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * Created by Alex on 18/02/2016.
 */
class Turret(
    listener: DamageListener,
    private val manager: AssetManager,
    private val entityGroup: Group,
    world: World,
    targetPosition: Vector2,
    pos: Vector2,
    type: Int
) : Block(
    manager, "sprites/turret_base.png", world, PolygonShape(), 500, pos, .8f, true, listener
), EnemyAI.Callback {
    private val weapon: Weapon
    private val enemyAI = EnemyAI(world, body.position, targetPosition, this)
    override fun draw(batch: Batch, parentAlpha: Float) {
        drawSprite(batch)
        weapon.draw(batch,color, x, parentAlpha, originX, originY, width, height, scaleX, scaleY, y)
        drawBurning(batch, parentAlpha)
        drawFrozen(batch)
    }

    override fun act(delta: Float) {
        if (isAlive && !isFrozen){
            enemyAI.update(delta)
        } else{
            await()
        }
        super.act(delta)
    }

    companion object {
        private const val ROTATION_SPEED = .035f
    }

    override fun detach() {
        super.detach()
        dropLoot()
    }

    private fun dropLoot() {
        val num1 = Utils.getRandomInt(5, 10)
        for (i in 0 until num1) entityGroup.addActor(
            GoldNugget(
                manager,
                body.world,
                body.position
            )
        )
    }

    init {
        val filter = Filter()
        filter.categoryBits = Constants.CAT_ENEMY
        fixture.filterData = filter
        weapon = Weapon.getWeaponAt(type, manager, 1, 3, false)
        weapon.setUnlimitedAmmo(true)
        setOrigin(width / 2, height / 2)
    }

    override fun attack(angle: Float) {
        weapon.setDesiredAngleRotation(angle)
        weapon.updateAngleRotation(ROTATION_SPEED)
        weapon.shoot(manager, entityGroup, body.world, body.position)
    }

    override fun await() {
        weapon.await()
    }
}