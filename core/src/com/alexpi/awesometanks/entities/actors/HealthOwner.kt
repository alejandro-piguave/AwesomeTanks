package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.entities.components.health.HealthComponent

interface HealthOwner {
    val healthComponent: HealthComponent
}