package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.projectiles.RicochetBullet;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Alex on 04/01/2016.
 */
public class Ricochet extends Weapon {

    public Ricochet(AssetManager assetManager, int ammo, int power, short filter, boolean sound)  {
        super("Ricochet", assetManager, "weapons/ricochet.png", "sounds/ricochet.ogg", ammo, power, filter, sound, .5f);
    }
    @Override
    public void createProjectile(Stage stage, AssetManager assetManager, World world, Vector2 position, DamageListener listener) {
        stage.addActor(new RicochetBullet(assetManager,world, position, shotSound,  listener,currentAngleRotation,isSound(), power, filter));
    }
}
