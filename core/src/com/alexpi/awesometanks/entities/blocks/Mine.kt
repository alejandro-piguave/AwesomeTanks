package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 20/02/2016.
 */
public class Mine extends Block {
    public Mine(DamageListener listener, AssetManager manager, World world, Vector2 pos) {
        super(manager,"sprites/mine.png",  world,new CircleShape(),150, pos, .5f, false, listener, false);
    }
}
