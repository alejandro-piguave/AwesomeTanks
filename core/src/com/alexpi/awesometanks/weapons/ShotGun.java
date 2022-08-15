package com.alexpi.awesometanks.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.entities.projectiles.Bullet;

import java.util.Random;

/**
 * Created by Alex on 04/01/2016.
 */
public class ShotGun extends Weapon {
    Random rnd;

    public ShotGun() {
        super("Shotgun");
        sprite = new Sprite(new Texture(Gdx.files.internal("weapons/shotgun.png")));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun.ogg"));
        rnd = new Random();}

    @Override
    public void shoot(AssetManager manager, Stage stage, World world, Vector2 currentPosition, boolean sound) {
        if(hasAmmo() && canShoot){
            for(int i = 0; i < 10;i++){
                float num = rnd.nextFloat()-.5f;
                if(rnd.nextBoolean())num = -num;
                stage.addActor(new Bullet(manager,world, currentPosition, currentAngleRotation + num, rnd.nextInt(20) + 10, .12f,10f+power, filter));
            }
            if(sound)shotSound.play(.2f);
            canShoot = false;Timer.schedule(new Timer.Task() {@Override public void run() {if(!canShoot)canShoot = true;}},.5f);
        }
    }
}
