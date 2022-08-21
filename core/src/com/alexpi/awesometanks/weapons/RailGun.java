package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Rail;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Alex on 04/01/2016.
 */
public class RailGun extends Weapon {

    public RailGun(AssetManager assetManager, int ammo, int power, boolean filter, boolean sound) {
        super("Railgun", assetManager, "weapons/railgun.png", "sounds/railgun.ogg", ammo, power, filter, sound, .5f);
    }

    @Override
    public void shoot(final AssetManager manager, final Group group, final World world, final Vector2 currentPosition) {
        if(hasAmmo() && !isCoolingDown) {
            if (isSound()) shotSound.play(.4f);
            Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    group.addActor(new Rail(manager,world, currentPosition,currentAngleRotation, power, isPlayer));
                    if(isCoolingDown) isCoolingDown = false;
                }
            }, .5f);
            isCoolingDown = true;
        }
    }

    @Override
    public void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position) { }
}
