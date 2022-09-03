package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by Alex on 29/01/2016.
 */
public class Gate extends Block {
    public Gate(DamageListener listener, AssetManager manager, World world, Vector2 pos) {
        super(manager,"sprites/gate.png",world,new PolygonShape(),100, pos,1f, true, listener);
    }
}
