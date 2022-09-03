package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.CanonBall;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 04/01/2016.
 */
public class Canon extends Weapon {

    public Canon(AssetManager assetManager, float ammo, int power, boolean filter) {
        super("Canon", assetManager, "weapons/canon.png", "sounds/canon.ogg", ammo, power, filter, .5f, 1f);
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        group.addActor( new CanonBall(assetManager,world, position, getCurrentAngleRotation(), power, isPlayer));
    }
}
