package com.alexpi.awesometanks.entities.projectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 14/01/2016.
 */
public class Bullet extends Projectile{

    public Bullet(AssetManager manager, World world, Vector2 pos, float angle, float speed, float radius, float damage, boolean isPlayer){
        super(world,pos, angle,speed,radius,damage,isPlayer);
        sprite = new Sprite(manager.get("sprites/bullet.png",Texture.class));
    }

}
