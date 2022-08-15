package com.alexpi.awesometanks.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.entities.projectiles.Rail;

/**
 * Created by Alex on 04/01/2016.
 */
public class RailGun extends Weapon {

    public RailGun() {
        super("Railgun");
        sprite = new Sprite(new Texture(Gdx.files.internal("weapons/railgun.png")));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/railgun.ogg"));}

    @Override
    public void shoot(final AssetManager manager, final Stage stage, final World world, final Vector2 currentPosition, boolean sound) {
        if(hasAmmo() && canShoot) {
            if (sound) shotSound.play(.4f);
            Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    stage.addActor(new Rail(manager,world,currentPosition, currentAngleRotation, power, filter));
                    if(!canShoot)canShoot = true;
                }
            }, .5f);
            canShoot = false;
        }
    }
}
