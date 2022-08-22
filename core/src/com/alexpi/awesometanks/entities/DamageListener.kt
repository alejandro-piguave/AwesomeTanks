package com.alexpi.awesometanks.entities

import com.alexpi.awesometanks.entities.actors.DamageableActor

interface DamageListener {
    fun onDamage(actor: DamageableActor)
    fun onDeath(actor: DamageableActor)
}