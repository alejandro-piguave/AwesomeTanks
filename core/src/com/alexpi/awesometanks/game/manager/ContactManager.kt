package com.alexpi.awesometanks.game.manager

import com.alexpi.awesometanks.game.items.Item
import com.alexpi.awesometanks.game.projectiles.Projectile
import com.alexpi.awesometanks.game.tanks.Player
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.scenes.scene2d.Actor

class ContactManager: ContactListener {

    override fun beginContact(contact: Contact) {
        contact.isOfType(Projectile::class.java, Actor::class.java) { projectile, actor ->
            projectile.collide(actor)
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
