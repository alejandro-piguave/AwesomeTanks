package com.alexpi.awesometanks.screens.upgrades

enum class WeaponInfo(val powerKey: String, val ammoKey: String, val availabilityKey: String, val enabledIconPath: String, val disabledIconPath: String, val price: Int, val ammoPrice: Int, val upgradePrices: List<Int>){
    MINIGUN("power0", "ammo", "isWeaponAvailable0", "icons/icon_0.png", "icons/icon_disabled_0.png",0, 0, listOf(200, 300, 400, 500, 600)),
    SHOTGUN( "power1", "ammo1", "isWeaponAvailable1", "icons/icon_1.png", "icons/icon_disabled_1.png",2750, 100, listOf(500, 900, 1300, 1700, 2100)),
    RICOCHET("power2", "ammo2", "isWeaponAvailable2", "icons/icon_2.png", "icons/icon_disabled_2.png",8000, 200, listOf(2500, 3000, 3500, 4000, 4500)),
    FLAMETHROWER("power3", "ammo3", "isWeaponAvailable3", "icons/icon_3.png", "icons/icon_disabled_3.png",10000, 300, listOf(3000, 4000, 5000, 6000, 7000)),
    CANNON("power4", "ammo4", "isWeaponAvailable4", "icons/icon_4.png", "icons/icon_disabled_4.png",10000, 300, listOf(3000, 4000, 5000, 6000, 7000)),
    ROCKETS("power5", "ammo5", "isWeaponAvailable5", "icons/icon_5.png", "icons/icon_disabled_5.png",10000, 300, listOf(3000, 4000, 5000, 6000, 7000)),
    LASERGUN("power6", "ammo6", "isWeaponAvailable6", "icons/icon_6.png", "icons/icon_disabled_6.png",28000, 400, listOf(11000, 12000, 13000, 14000, 15000)),
    RAILGUN("power7", "ammo7", "isWeaponAvailable7", "icons/icon_7.png", "icons/icon_disabled_7.png",28000, 400, listOf(11000, 12000, 13000, 14000, 15000))
}