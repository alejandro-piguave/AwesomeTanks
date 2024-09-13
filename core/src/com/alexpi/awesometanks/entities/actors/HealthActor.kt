package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.entities.components.health.HealthComponent

interface HealthActor {
    val healthComponent: HealthComponent
}