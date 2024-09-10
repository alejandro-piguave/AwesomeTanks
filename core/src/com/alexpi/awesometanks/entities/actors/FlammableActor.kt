package com.alexpi.awesometanks.entities.actors

import com.alexpi.awesometanks.entities.components.FlammableComponent

interface FlammableActor: HealthActor {
    val flammableComponent: FlammableComponent
}