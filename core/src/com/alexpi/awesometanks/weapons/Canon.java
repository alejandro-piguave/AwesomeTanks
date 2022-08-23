package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Bullet;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 04/01/2016.
 */
public class Canon extends Weapon {

    public Canon(AssetManager assetManager, int ammo, int power, boolean filter) {
        super("Canon", assetManager, "weapons/canon.png", "sounds/canon.ogg", ammo, power, filter, .5f);
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        group.addActor( new Bullet(assetManager,world, position, currentAngleRotation, 35f, .2f, 40+power*5, isPlayer));
    }
}
