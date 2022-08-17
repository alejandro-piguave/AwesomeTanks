package com.alexpi.awesometanks.entities.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 13/01/2016.
 */
public class Wall extends Block {

    public Wall(AssetManager manager,World world, int posX, int posY){
        super(manager,world,new PolygonShape(), null,1,posX,posY,1f);
        sprite = new Sprite(manager.get("sprites/wall.png",Texture.class));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
    }


    @Override
    public void takeDamage(float damage) { }
}
