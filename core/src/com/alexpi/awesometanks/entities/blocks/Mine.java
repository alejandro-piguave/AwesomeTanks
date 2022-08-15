package com.alexpi.awesometanks.entities.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 20/02/2016.
 */
public class Mine extends Block {
    public Mine(AssetManager manager, World world,int posX, int posY) {
        super(manager, world,new CircleShape(), 150, posX, posY, .5f);
        sprite = new Sprite(manager.get("sprites/mine.png",Texture.class));
    }
}
