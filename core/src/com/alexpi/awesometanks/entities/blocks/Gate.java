package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by Alex on 29/01/2016.
 */
public class Gate extends Block {
    public Gate(DamageListener listener, AssetManager manager, World world, int posX, int posY) {
        super(manager,"sprites/gate.png",world,new PolygonShape(),100, posX, posY,1f, true, listener);
    }
}
