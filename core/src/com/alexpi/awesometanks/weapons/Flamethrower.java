package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Flame;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;


/**
 * Created by Alex on 04/01/2016.
 */
public class Flamethrower extends Weapon {
    public Flamethrower(AssetManager assetManager, float ammo, int power, boolean filter) {
        super("Flamethrower", assetManager, "weapons/flamethrower.png", "sounds/flamethrower.ogg", ammo, power, filter, .4f, .75f);
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        group.addActor(new Flame(assetManager, world, position, currentAngleRotation,2f+power, isPlayer));
    }
}
