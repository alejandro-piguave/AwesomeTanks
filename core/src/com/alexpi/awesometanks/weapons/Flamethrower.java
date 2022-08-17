package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.projectiles.Flame;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;


/**
 * Created by Alex on 04/01/2016.
 */
public class Flamethrower extends Weapon {
    public Flamethrower(AssetManager assetManager, int ammo, int power, short filter, boolean sound) {
        super("Flamethrower", assetManager, "weapons/flamethrower.png", "sounds/flamethrower.ogg", ammo, power, filter, sound, .1f);
    }

    @Override
    public void createProjectile(Stage stage, AssetManager assetManager, World world, Vector2 position, DamageListener listener) {
        stage.addActor(new Flame(assetManager,stage,world, position, listener, currentAngleRotation,2f+power, filter));
    }
}
