package com.alexpi.awesometanks.world.collision

import com.alexpi.awesometanks.entities.actors.DamageableActor
import com.alexpi.awesometanks.entities.blocks.Mine
import com.alexpi.awesometanks.entities.blocks.Spawner
import com.alexpi.awesometanks.entities.items.Item
import com.alexpi.awesometanks.entities.projectiles.CanonBall
import com.alexpi.awesometanks.entities.projectiles.Flame
import com.alexpi.awesometanks.entities.projectiles.Projectile
import com.alexpi.awesometanks.entities.projectiles.Rail
import com.alexpi.awesometanks.entities.projectiles.Rocket
import com.alexpi.awesometanks.entities.tanks.Player
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class ContactManager(private val contactListener: ContactListener) : ContactListener {
    interface ContactListener {
        fun onBulletCollision(x: Float, y: Float)
        fun onLandMineFound(x: Float, y: Float)
        fun onExplosiveProjectileCollided(x: Float, y: Float)
    }

    override fun beginContact(contact: Contact) {
        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB

        if ((fixtureA.userData is Projectile && (fixtureB.userData is DamageableActor))
            || ((fixtureB.userData is Projectile) && (fixtureA.userData is DamageableActor))
        ) {
            val projectile =
                if (fixtureA.userData is Projectile) fixtureA.userData as Projectile else (fixtureB.userData as Projectile)

            val damageableActor =
                (if (fixtureA.userData is DamageableActor) fixtureA.userData else fixtureB.userData) as DamageableActor

            if (projectile is Flame) damageableActor.burn(projectile.burnDuration)

            if (!(projectile.isEnemy && damageableActor is Spawner)) {
                damageableActor.takeDamage(projectile.damage)
            }
            if (projectile is CanonBall || projectile is Rocket || projectile is Rail) {
                contactListener.onExplosiveProjectileCollided(
                    projectile.x + projectile.bodyWidth * .5f,
                    projectile.y + projectile.bodyHeight * .5f
                )
            }
            projectile.collide()

            contactListener.onBulletCollision(
                projectile.x + projectile.bodyWidth * .5f,
                projectile.y + projectile.bodyHeight * .5f
            )

            if (!damageableActor.isAlive && (damageableActor is Mine)) {
                val mineFixture = if (fixtureA.userData is Mine) fixtureA else fixtureB
                val mineX = mineFixture.body.position.x
                val mineY = mineFixture.body.position.y
                contactListener.onLandMineFound(mineX, mineY)
            }
        }

        if ((fixtureA.userData is Item && fixtureB.userData is Player) || (contact.fixtureB.userData is Item && fixtureA.userData is Player)) {
            val item = if (fixtureA.userData is Item) fixtureA.userData as Item else (fixtureB.userData as Item)
            val player = if (fixtureA.userData is Player) fixtureA.userData as Player else (fixtureB.userData as Player)
            player.pickUp(item)
        }
    }

    override fun endContact(contact: Contact) {}

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
}
