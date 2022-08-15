package com.alexpi.awesometanks.entities.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 20/01/2016.
 */
public class Bricks extends Block {
    public Bricks(AssetManager manager,World world, int posX, int posY) {
        super(manager,world,new PolygonShape(),150,posX,posY,1f);
        sprite = new Sprite(manager.get("sprites/bricks.png",Texture.class));

    }
}
