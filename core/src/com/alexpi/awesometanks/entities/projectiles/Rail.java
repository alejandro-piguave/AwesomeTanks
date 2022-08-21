package com.alexpi.awesometanks.entities.projectiles;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alexpi.awesometanks.entities.ParticleActor;

/**
 * Created by Alex on 17/01/2016.
 */
public class Rail extends Projectile {

    ParticleActor particleActor;

    public Rail(AssetManager manager, World world, Vector2 pos, float angle, float power, boolean filter) {
        super( world, pos, new CircleShape(), angle, 3f, .25f, 80+power*10,filter);
        particleActor = new ParticleActor(manager,"particles/railgun.party",getX()+getWidth()/2,getY()+getHeight()/2,true);
    }

    @Override
    protected void setStage(Stage stage) {
        if(stage!=null)
            stage.addActor(particleActor);
        super.setStage(stage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        particleActor.setPosition(getX() + getWidth() / 2, getY()+getHeight()/2);
    }

    @Override
    public void detach() {
        super.detach();
        particleActor.remove();
    }

}
