package com.alexpi.awesometanks.entities.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

/**
 * Created by Alex on 19/02/2016.
 */
public class HealthPack extends Item {
    public int health;
    public HealthPack(AssetManager manager,World world, Vector2 position) {
        super(manager, "sprites/health_pack.png", world, position, .4f);
        health = new Random().nextInt(75)+50;
    }
}
