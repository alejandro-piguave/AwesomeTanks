package com.alexpi.awesometanks.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.ParticleActor;
import com.alexpi.awesometanks.utils.Constants;

/**
 * Created by Alex on 23/02/2016.
 */
public abstract class DamageableActor extends Actor {

    private float maxHealth, health;
    private boolean isAlive, isBurning, justHit;
    public ParticleActor flame;
    private Sprite healthBar;
    private AssetManager manager;

    public DamageableActor(AssetManager manager,int health){
        this.manager = manager;
        maxHealth = this.health = health;
        isAlive = true;
        flame = new ParticleActor(manager,"particles/flame.party", getX(), getY(), true);
        healthBar = new Sprite(manager.get("sprites/health_bar.png",Texture.class));
    }

    public void getHit(float damage) {
        if(health - damage > 0)
            health -= damage;
        else kill();
        if(!justHit){justHit = true;Timer.schedule(new Timer.Task() {@Override public void run() {justHit = false;}},1.5f);}
    }
    public float getCenterX(){return getX() + getWidth()/2;}
    public float getCenterY(){return getY() + getHeight()/2;}

    @Override
    public void act(float delta) {
        super.act(delta);
        if(!isAlive)detach();
        if(isBurning){flame.setPosition(getX()+getWidth()/2,getY()+getHeight()/2);flame.act(delta);getHit(.25f);}
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(isBurning)flame.draw(batch, parentAlpha);
        if(justHit)batch.draw(healthBar, (getX() + getWidth() / 2) - (Constants.tileSize / 2),
                (getY() + getHeight() / 2) + (Constants.tileSize / 2), Constants.tileSize * (health / maxHealth), Constants.tileSize / 8);
    }

    public void burn(float duration) {
        isBurning = true;Timer.schedule(new Timer.Task() {
            @Override
            public void run() {if (isBurning) isBurning = false;}
        }, duration);}
    public void detach(){getStage().addActor(new ParticleActor(manager, "particles/explosion.party", getX() + getWidth() / 2, getY() + getHeight() / 2, false));}

    public boolean isAlive() {return isAlive;}
    public void heal(int healthValue){if(health+healthValue< maxHealth) health+=healthValue;else  health = maxHealth;}
    public void kill() {isAlive = false;}

    public void setJustHit(boolean justHit) {
        this.justHit = justHit;
    }
}
