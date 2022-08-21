package com.alexpi.awesometanks.entities.projectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alexpi.awesometanks.entities.ParticleActor;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Alex on 16/01/2016.
 */
public class RicochetBullet extends Projectile {

    ParticleActor particleActor;
    int hits;
    Sound hitSound;
    boolean soundFX;

    public RicochetBullet(AssetManager manager, World world, Vector2 pos, Sound sound, float angle, boolean soundFX, float power, boolean isPlayer) {
        super(world,pos,new CircleShape(), angle,20f,.2f,25+power*2,isPlayer);
        hitSound = sound;
        this.soundFX = soundFX;
        sprite = new Sprite(manager.get("sprites/ricochet_bullet.png",Texture.class));
        particleActor = new ParticleActor(manager,"particles/ricochets.party",getX()+getWidth()/2,getY()+getHeight()/2,true);
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
        particleActor.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    @Override
    public void detach() {
        particleActor.remove();
        super.detach();
    }

    @Override
    public void destroy() {
        if(hits <5){
            hits++;
            if(soundFX)hitSound.play();
        } else{
            super.destroy();
        }
    }

}
