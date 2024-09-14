package com.alexpi.awesometanks.screens.upgrades

enum class PerformanceUpgrade(val prices: List<Int>){
    ARMOR(listOf(2000, 4000, 8000, 16000, 32000)),
    SPEED(listOf(500, 600, 700, 800, 900)),
    ROTATION(listOf(500, 600, 700, 800, 900)),
    VISIBILITY(listOf(500, 600, 700, 800, 900))
}