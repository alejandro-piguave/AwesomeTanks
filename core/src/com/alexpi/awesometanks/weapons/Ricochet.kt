package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.RicochetBullet;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 04/01/2016.
 */
public class Ricochet extends Weapon {

    public Ricochet(AssetManager assetManager, float ammo, int power, boolean isPlayer)  {
        super("Ricochet", assetManager, "weapons/ricochet.png", "sounds/ricochet.ogg", ammo, power, isPlayer, .5f, .9f);
    }
    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        group.addActor(new RicochetBullet(assetManager,world, position, shotSound,getCurrentAngleRotation(), power, isPlayer));
    }
}
