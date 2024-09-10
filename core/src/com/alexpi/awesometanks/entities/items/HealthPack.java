package com.alexpi.awesometanks.entities.items;

import com.alexpi.awesometanks.utils.RandomUtils;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Alex on 19/02/2016.
 */
public class HealthPack extends Item {
    public int getHealth() {
        return health;
    }

    private final int health;
    public HealthPack( Vector2 position) {
        super("sprites/health_pack.png", position,.4f);
        health = RandomUtils.getRandomInt(100,200);
    }
}
