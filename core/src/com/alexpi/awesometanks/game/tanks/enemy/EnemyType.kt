package com.alexpi.awesometanks.game.tanks.enemy

data class EnemyType(val tier: EnemyTier, val weapon: EnemyWeapon) {
    fun getNuggetValue(): Int{
        val tierMultiplier: Float = when(tier){
            EnemyTier.MINI -> .5f
            EnemyTier.NORMAL -> 1f
            EnemyTier.BOSS -> 1.5f
        }

        return 50 + (weapon.valueMultiplier * 75 * tierMultiplier).toInt()
    }

    fun getHealth(): Float{
        return when (tier){
            EnemyTier.MINI -> 75f + weapon.valueMultiplier * 100f
            EnemyTier.NORMAL -> 125f + weapon.valueMultiplier * 300f
            EnemyTier.BOSS -> 250f + weapon.valueMultiplier * 400f
        }
    }
}