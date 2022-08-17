package com.alexpi.awesometanks.entities.items;


import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 01/02/2016.
 */
public class GoldNugget extends Item {

    public int value;

    public GoldNugget(AssetManager manager, World world, Vector2 position, DamageListener listener){
        super(manager,"sprites/nugget.png",world,position, listener, Utils.getRandomFloat(.1f,.25f));
        value = (int) (size*500);
        float angle =  Utils.getRandomFloat(Math.PI*2);
        body.applyLinearImpulse(MathUtils.cos(angle), MathUtils.sin(angle), body.getPosition().x, body.getPosition().y, true);
    }
}
