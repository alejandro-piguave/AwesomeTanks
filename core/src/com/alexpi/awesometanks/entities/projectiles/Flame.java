package com.alexpi.awesometanks.entities.projectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.alexpi.awesometanks.entities.actors.ParticleActor;

/**
 * Created by Alex on 16/01/2016.
 */
public class Flame extends Projectile {

    public float burnDuration;
    private final ParticleActor particleActor;

    public Flame(AssetManager manager, World world, Vector2 pos, float angle, float burnDuration, boolean filter) {
        super(world, pos, new CircleShape(), angle, 15f, .1f, 20f, filter);
        this.burnDuration = burnDuration;
        particleActor = new ParticleActor(manager,"particles/flame.party",getX()+getWidth()/2,getY()+getHeight()/2,true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        particleActor.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        particleActor.setPosition(getX() + getWidth() / 2, getY()+getHeight()/2);
        particleActor.act(delta);
    }

}
