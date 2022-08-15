package com.alexpi.awesometanks.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.entities.projectiles.Laser;

/**
 * Created by Alex on 04/01/2016.
 */
public class LaserGun extends Weapon {

    public LaserGun() {super("Lasergun");
        sprite = new Sprite(new Texture(Gdx.files.internal("weapons/laser.png")));
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.ogg"));}

    @Override
    public void shoot(AssetManager manager, Stage stage, World world, Vector2 currentPosition, boolean sound) {
        if(hasAmmo() && canShoot) {
            stage.addActor(new Laser(manager, world, currentPosition, currentAngleRotation, power,filter));
            if (sound) shotSound.play(.2f);
            canShoot = false;Timer.schedule(new Timer.Task() {@Override public void run() {if(!canShoot)canShoot = true;}},.35f);
        }
    }
}
