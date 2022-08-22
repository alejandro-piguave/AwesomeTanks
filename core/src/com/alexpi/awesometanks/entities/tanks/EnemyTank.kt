package com.alexpi.awesometanks.entities.tanks

import com.alexpi.awesometanks.entities.DamageListener
import com.alexpi.awesometanks.entities.ai.EnemyAI
import com.alexpi.awesometanks.entities.items.GoldNugget
import com.alexpi.awesometanks.utils.Constants
import com.alexpi.awesometanks.utils.Utils
import com.alexpi.awesometanks.weapons.Weapon
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import kotlin.experimental.or

/**
 * Created by Alex on 17/02/2016.
 */
class EnemyTank(
    manager: AssetManager,
    entityGroup: Group,
    world: World,
    position: Vector2,
    targetPosition: Vector2,
    size: Float,
    type: Int, damageListener: DamageListener?) : Tank(manager, entityGroup, world, position, size,
    ROTATION_SPEED, MOVEMENT_SPEED,
    Constants.CAT_ENEMY,
    Constants.CAT_BLOCK or Constants.CAT_PLAYER or Constants.CAT_PLAYER_BULLET or Constants.CAT_ENEMY,
    200f,true, Gdx.app.getPreferences("settings").getBoolean("areSoundsActivated"), damageListener),
    EnemyAI.Callback {

    private val enemyAI = EnemyAI(world, body.position, targetPosition, this)
    private val weapon: Weapon

    override fun act(delta: Float) {
        if(isAlive && !isFrozen){
            enemyAI.update(delta)
        }else{
            await()
        }
        super.act(delta)
    }

    override fun detach() {
        super.detach()
        dropLoot()
    }

    override fun getCurrentWeapon(): Weapon = weapon

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

    companion object {
        private const val ROTATION_SPEED = .035f
        private const val MOVEMENT_SPEED = 60f
    }

    init {
        weapon = Weapon.getWeaponAt(type, manager, 1, 2, false, allowSounds)
        weapon.setUnlimitedAmmo(true)
    }

    override fun attack(angle: Float) {
        setOrientation(angle)
        weapon.setDesiredAngleRotation(angle)
        isMoving = true
        isShooting = true
    }

    override fun await() {
        isShooting = false
        isMoving = false
    }
}