package com.alexpi.awesometanks.world.collision

import com.alexpi.awesometanks.entities.actors.DamageableActor
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
        fun onExplosiveProjectileCollided(x: Float, y: Float)
    }

    override fun beginContact(contact: Contact) {
        contact.isOfType(Projectile::class.java, DamageableActor::class.java) { projectile, damageableActor ->
            if (projectile is Flame) damageableActor.burn(projectile.burnDuration)

            if (!(projectile.isEnemy && damageableActor is Spawner)) {
                damageableActor.takeDamage(projectile.damage)
            }
            if (projectile is CanonBall || projectile is Rocket || projectile is Rail) {
                contactListener.onExplosiveProjectileCollided(
                    projectile.x + projectile.bodyShape.width * .5f,
                    projectile.y + projectile.bodyShape.height * .5f
                )
            }
            projectile.collide()
        }

        contact.isOfType(Player::class.java, Item::class.java) { player, item ->
            player.pickUp(item)
        }
    }

    private fun <A, B> Contact.isOfType(classA: Class<A>, classB: Class<B>, body: (a: A, b: B) -> Unit) {
        if(classA.isInstance(fixtureA.userData) && classB.isInstance(fixtureB.userData)) body(classA.cast(fixtureA.userData), classB.cast(fixtureB.userData))
        else if(classA.isInstance(fixtureB.userData) && classB.isInstance(fixtureA.userData)) body(classA.cast(fixtureB.userData), classB.cast(fixtureA.userData))
    }

    override fun endContact(contact: Contact) {}

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
}
