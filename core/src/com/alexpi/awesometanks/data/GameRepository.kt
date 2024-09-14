package com.alexpi.awesometanks.data

import com.alexpi.awesometanks.screens.upgrades.PerformanceUpgrade
import com.alexpi.awesometanks.screens.upgrades.WeaponUpgrade
import com.alexpi.awesometanks.screens.upgrades.WeaponValues
import com.badlogic.gdx.Preferences

class GameRepository(private val preferences: Preferences) {
    fun getMoney() = preferences.getInteger("money", 0)

    fun getWeaponValues(weaponUpgrade: WeaponUpgrade): WeaponValues {
        val weaponPower = preferences.getInteger(weaponUpgrade.powerKey, 0)
        val weaponAmmo = preferences.getFloat(weaponUpgrade.ammoKey, 100f)
        val isWeaponAvailable = preferences.getBoolean(weaponUpgrade.availabilityKey, false)
        return WeaponValues(weaponPower, weaponAmmo, isWeaponAvailable)
    }

    fun getUpgradeLevel(performanceUpgrade: PerformanceUpgrade): Int = preferences.getInteger(performanceUpgrade.name)

    fun isLevelAvailable(level: Int) = preferences.getBoolean("unlocked$level")

    fun isWeaponAvailable(weaponUpgrade: WeaponUpgrade) = preferences.getBoolean(weaponUpgrade.availabilityKey, true)

    fun unlockLevel(level: Int) {
        preferences.putBoolean("unlocked$level", true).flush()
    }

    fun saveMoney(money: Int) {
        preferences.putInteger("money", money).flush()
    }

    fun updateMoney(money: Int){
        val savedMoney = preferences.getInteger("money")
        saveMoney(savedMoney + money)
    }

    fun saveUpgradeLevel(performanceUpgrade: PerformanceUpgrade, level: Int) {
        preferences.putInteger(performanceUpgrade.name, level).flush()
    }

    fun saveAmmo(weaponUpgrade: WeaponUpgrade, ammo: Float) {
        preferences.putFloat(weaponUpgrade.ammoKey, ammo).flush()
    }

    fun saveWeaponValues(weaponUpgrade: WeaponUpgrade, weaponValues: WeaponValues) {
        preferences.putInteger(weaponUpgrade.powerKey, weaponValues.power)
        preferences.putFloat(weaponUpgrade.ammoKey, weaponValues.ammo)
        preferences.putBoolean(weaponUpgrade.availabilityKey, weaponValues.isAvailable)
        preferences.flush()
    }
}