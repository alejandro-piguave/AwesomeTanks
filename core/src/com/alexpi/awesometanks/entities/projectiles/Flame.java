package com.alexpi.awesometanks.entities.projectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alexpi.awesometanks.ParticleActor;

/**
 * Created by Alex on 16/01/2016.
 */
public class Flame extends Projectile {

    public float burnDuration;
    ParticleActor particleActor;

    public Flame(AssetManager manager,Stage stage,World world, Vector2 pos, float angle, float burnDuration, short filter) {
        super(world, pos, new CircleShape(), angle, 15f, .1f, 20f, filter);
        this.burnDuration = burnDuration;
        particleActor = new ParticleActor(manager,"particles/flame.party",getX()+getWidth()/2,getY()+getHeight()/2,true);
        stage.addActor(particleActor);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        particleActor.setPosition(getX() + getWidth() / 2, getY()+getHeight()/2);
    }

    @Override
    public void detach() {
        super.detach();
        particleActor.detach();
    }

}
