package com.alexpi.awesometanks.entities.projectiles;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.entities.actors.ParticleActor;

/**
 * Created by Alex on 17/01/2016.
 */
public class Rail extends Projectile {

    ParticleActor particleActor;

    public Rail(AssetManager manager, World world, Vector2 pos, float angle, float power, boolean filter) {
        super( world, pos, angle, 50f, .25f, 180+power*40,filter);
        particleActor = new ParticleActor(manager,"particles/railgun.party",getX()+ getBodyWidth()/2,getY()+ getBodyHeight()/2,true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        particleActor.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        particleActor.setPosition(getX() + getBodyWidth() / 2, getY()+ getBodyHeight()/2);
        particleActor.act(delta);
    }

}
