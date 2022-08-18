package com.alexpi.awesometanks.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Alex on 19/02/2016.
 */
public class ParticleActor extends Actor {

    private ParticleEffect effect;
    private boolean loop;
    public ParticleActor(AssetManager manager,String effectFile, float x, float y, boolean loop){
        this.loop = loop;
        effect = new ParticleEffect(manager.get(effectFile,ParticleEffect.class));
        setPosition(x, y);
        effect.setPosition(getX(),getY());
        effect.start();
    }

    @Override
    public void setPosition(float x, float y) {super.setPosition(x, y);effect.setPosition(x, y);}

    @Override
    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(),getY());
        effect.update(delta);
        if(effect.isComplete() && !loop) remove();
        else if(effect.isComplete() && loop)effect.reset();
    }

}
