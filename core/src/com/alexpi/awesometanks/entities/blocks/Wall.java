package com.alexpi.awesometanks.entities.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 13/01/2016.
 */
public class Wall extends Block {

    public Wall(AssetManager manager, World world, Vector2 pos){
        super(manager, "sprites/wall.png", world,new PolygonShape(),1,pos,1f, false, null);
    }


    //Makes it indestructible regardless of its health as long as its positive
    @Override
    public void takeDamage(float damage) { }
}
