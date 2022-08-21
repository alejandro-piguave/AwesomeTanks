package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Bullet;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 03/01/2016.
 */
public class MiniGun extends Weapon {
    public MiniGun(AssetManager assetManager, int ammo, int power, boolean isPlayer, boolean sound) {
        super("Minigun",assetManager,"weapons/minigun.png","sounds/minigun.ogg", ammo, power, isPlayer, sound, .06f);
        setUnlimitedAmmo(true);
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        group.addActor(new Bullet(assetManager,world, position, currentAngleRotation, 30f,.1f,3.5f+power, isPlayer));
    }
}
