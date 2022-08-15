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

/**
 * Created by Alex on 03/01/2016.
 */
public class MiniGun extends Weapon {
    public MiniGun() {super("Minigun");
        sprite = new Sprite(new Texture(Gdx.files.internal("weapons/minigun.png")));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/minigun.ogg"));}

    @Override
    public void shoot(AssetManager manager, Stage stage, World world, Vector2 currentPosition,  boolean sound) {
        if(canShoot){
            stage.addActor(new Bullet(manager,world, currentPosition, currentAngleRotation, 30f,.1f,5f+power, filter));
            if(sound)shotSound.play(.2f);
            canShoot = false;Timer.schedule(new Timer.Task() {@Override public void run() {if(!canShoot)canShoot = true;}},.1f);
        }
    }
}
