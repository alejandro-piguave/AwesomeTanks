package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Bullet;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 04/01/2016.
 */
public class ShotGun extends Weapon {

    public ShotGun(AssetManager assetManager, int ammo, int power, boolean filter, boolean sound) {
        super("Shotgun", assetManager,"weapons/shotgun.png", "sounds/shotgun.ogg", ammo, power, filter, sound,1.25f);
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) {
        for(int i = 0; i < 10;i++){
            float num = (float) (Math.random()-.5f);
            group.addActor(new Bullet(assetManager,world, position,currentAngleRotation + num, Utils.getRandomInt(10,30), .12f,10f+power, isPlayer));
        }
    }
}
