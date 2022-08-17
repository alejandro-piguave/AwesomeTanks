package com.alexpi.awesometanks.entities.projectiles;

import com.alexpi.awesometanks.entities.DamageListener;
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

    public Bullet(AssetManager manager, World world, Vector2 pos, DamageListener listener, float angle, float speed, float size, float damage, short filter){
        super(world,pos,new CircleShape(), listener, angle,speed,size,damage,filter);
        sprite = new Sprite(manager.get("sprites/bullet.png",Texture.class));
    }

}
