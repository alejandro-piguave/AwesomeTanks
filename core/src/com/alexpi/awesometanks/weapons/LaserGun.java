package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Laser;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Alex on 04/01/2016.
 */
public class LaserGun extends Weapon {

    public LaserGun(AssetManager assetManager, int ammo, int power, short filter, boolean sound) {
        super("Lasergun", assetManager, "weapons/laser.png", "sounds/laser.ogg", ammo, power, filter, sound, .35f);
    }

    @Override
    public void createProjectile(Stage stage, AssetManager assetManager, World world, Vector2 position) {
        stage.addActor(new Laser(assetManager, world, position, currentAngleRotation, power,filter));

    }
}
