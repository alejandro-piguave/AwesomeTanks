package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Bullet;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Alex on 03/01/2016.
 */
public class MiniGun extends Weapon {
    public MiniGun(AssetManager assetManager, int ammo, int power, short filter, boolean sound) {
        super("Minigun",assetManager,"weapons/minigun.png","sounds/minigun.ogg", ammo, power, filter, sound, .075f);
        setUnlimitedAmmo(true);
    }

    @Override
    public void createProjectile(Stage stage, AssetManager assetManager, World world, Vector2 position) {
        stage.addActor(new Bullet(assetManager,world, position, currentAngleRotation, 30f,.1f,5f+power, filter));
    }
}
