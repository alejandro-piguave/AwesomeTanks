package com.alexpi.awesometanks.entities.items;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 01/02/2016.
 */
public class GoldNugget extends Item {

    public int value;

    public GoldNugget(AssetManager manager,World world, Vector2 position, float size, float angle){
        super(manager,"sprites/nugget.png",world,position,size);
        value = (int) (size*1000);
        body.applyLinearImpulse(MathUtils.cos(angle) * 1f, MathUtils.sin(angle) * 1f, body.getPosition().x, body.getPosition().y, true);

    }
}
