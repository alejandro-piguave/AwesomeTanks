package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by Alex on 29/01/2016.
 */
public class Gate extends Block {
    public Gate(AssetManager manager, World world, DamageListener listener, int posX, int posY) {
        super(manager,world,new PolygonShape(), listener,100, posX, posY,1f);
        sprite = new Sprite(manager.get("sprites/gate.png",Texture.class));

    }
}
