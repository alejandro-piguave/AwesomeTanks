package com.alexpi.awesometanks.entities.projectiles;

import com.alexpi.awesometanks.utils.Settings;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.alexpi.awesometanks.entities.actors.ParticleActor;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Alex on 16/01/2016.
 */
public class RicochetBullet extends Projectile {

    ParticleActor particleActor;
    int hits;
    Sound hitSound;

    private final static int MAX_HITS = 3;

    public RicochetBullet(AssetManager manager, World world, Vector2 pos, Sound sound, float angle, float power, boolean isPlayer) {
        super(world,pos,new CircleShape(), angle,20f,.2f,25+power*2,isPlayer);
        hitSound = sound;
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
        if(hits <MAX_HITS){
            hits++;
            if(Settings.INSTANCE.getSoundsOn())hitSound.play();
        } else{
            super.destroy();
        }
    }

}
