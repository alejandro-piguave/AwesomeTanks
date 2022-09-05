package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.entities.projectiles.Bullet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 03/01/2016.
 */
public class MiniGun extends Weapon {
    public MiniGun(float ammo, int power, boolean isPlayer) {
        super("Minigun","weapons/minigun.png","sounds/minigun.ogg", ammo, power, isPlayer, .06f, 1f);
        unlimitedAmmo = true;
    }

    @Override
    public void createProjectile(Group group, Vector2 position) {
        group.addActor(new Bullet(position, getCurrentAngleRotation(), 30f,.1f,3.5f+power, isPlayer));
    }
}
