package com.alexpi.awesometanks.entities.components.health

sealed class HealthState {
    object Normal: HealthState()
    data class Burning(val startTime: Long, val duration: Float, val damage: Float): HealthState()
    data class Frozen(val startTime: Long, val duration: Float): HealthState()
}