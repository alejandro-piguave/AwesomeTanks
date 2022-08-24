package com.alexpi.awesometanks.entities.items;


import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Alex on 01/02/2016.
 */
public class GoldNugget extends Item {

    public final int value;

    public GoldNugget(AssetManager manager, World world, Vector2 position, int value){
        super(manager,"sprites/nugget.png",world,position, Utils.getRandomFloat(.15f,.25f));
        this.value = value;
        float angle =  Utils.getRandomFloat(Math.PI*2);
        body.applyLinearImpulse(MathUtils.cos(angle)*.025f, MathUtils.sin(angle)*.025f, body.getPosition().x, body.getPosition().y, true);
    }
}
