package com.alexpi.awesometanks.entities

interface DamageListener {
    fun onDamage(actor: DamageableActor)
    fun onDeath(actor: DamageableActor)
}