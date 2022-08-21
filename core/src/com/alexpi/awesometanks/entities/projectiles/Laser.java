package com.alexpi.awesometanks.entities.projectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by Alex on 17/01/2016.
 */
public class Laser extends Projectile{

    public Laser(AssetManager manager, World world, Vector2 pos, float angle, float power, boolean filter){
        super(world,pos,new PolygonShape(), angle,35f,.3f,50+power*5,filter);
        sprite = new Sprite(manager.get("sprites/laser.png", Texture.class));
    }
}
