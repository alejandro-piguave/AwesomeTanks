package com.alexpi.awesometanks.entities.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 19/02/2016.
 */
public class FreezingBall extends Item {
    public FreezingBall(AssetManager manager, World world,  Vector2 position) {
        super(manager, "sprites/freezing_ball.png", world,position,.3f);
    }
}
