package com.alexpi.awesometanks.game.components.health

interface HealthOwner {
    val healthComponent: HealthComponent

    fun onTakeDamage(health: Float) {}
    fun onHeal(health: Float) {}
    fun onDeath() {}
    fun onFreeze() {}

}