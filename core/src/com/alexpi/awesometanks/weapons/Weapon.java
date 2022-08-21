package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.utils.Constants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Alex on 03/01/2016.
 */
public abstract class Weapon {
    protected int ammo;
    protected int power;
    public Sprite sprite;
    protected Sound shotSound;
    protected float desiredAngleRotation, currentAngleRotation;
    private final String name;
    protected boolean isPlayer;
    protected boolean isCoolingDown;
    private final boolean sound;
    private boolean unlimitedAmmo = false;
    private final float coolingDownTime;

    public Weapon(String name, AssetManager assetManager, String texturePath, String shotSoundPath, int ammo, int power, boolean isPlayer, boolean sound, float coolingDownTime){
        this.name = name;
        this.isCoolingDown = false;
        this.sprite = new Sprite(assetManager.get(texturePath,Texture.class));
        this.shotSound = assetManager.get(shotSoundPath,Sound.class);
        this.ammo = ammo;
        this.power = power;
        this.isPlayer = isPlayer;
        this.sound = sound;
        this.coolingDownTime = coolingDownTime;
    }

    public String getName() {return name;}
    public int getAmmo(){return ammo;}
    private void decreaseAmmo(){if(ammo>0)ammo--;}

    public boolean hasAmmo(){return ammo>0;}

    public void setDesiredAngleRotation(float x, float y) {
        desiredAngleRotation = (float) Math.atan2(y,x);
        if(desiredAngleRotation <0) desiredAngleRotation +=Math.PI*2;
    }

    public boolean hasRotated(){return currentAngleRotation == desiredAngleRotation;}
    public void updateAngleRotation(float rotationSpeed) {
        float diff = desiredAngleRotation -currentAngleRotation;
        if(diff<0)diff+=Math.PI*2;

        if(diff>=Math.PI){currentAngleRotation-=rotationSpeed;diff-=Math.PI;}
        else if(diff<Math.PI) currentAngleRotation+=rotationSpeed;

        if(diff<rotationSpeed)currentAngleRotation = desiredAngleRotation;

        if(currentAngleRotation>Math.PI*2)currentAngleRotation= 0;
        else if(currentAngleRotation<0)currentAngleRotation= (float) (Math.PI*2);

        sprite.setRotation(currentAngleRotation*MathUtils.radiansToDegrees);
    }

    public float getCurrentAngleRotation() {
        return currentAngleRotation;
    }

    public void setUnlimitedAmmo(boolean unlimitedAmmo) {
        this.unlimitedAmmo = unlimitedAmmo;
    }

    public boolean isSound() {
        return sound;
    }

    public void shoot(AssetManager assetManager, Group group, World world, Vector2 position){
        if(canShoot()) {
            createProjectile(group, assetManager, world, position);
            if (sound) shotSound.play();
            if (!unlimitedAmmo) decreaseAmmo();
            isCoolingDown = true;
            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    if(isCoolingDown) isCoolingDown = false;
                }
                }, coolingDownTime);
        }
    }

    public abstract void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position);

    private boolean canShoot(){return (hasAmmo() || unlimitedAmmo) && !isCoolingDown;}

    public static Weapon getWeaponAt(int i, AssetManager assetManager, int ammo, int power, boolean isPlayer, boolean sound){
        Weapon weapon = null;
        switch (i){
            case Constants.MINIGUN: weapon = new MiniGun(assetManager, ammo, power, isPlayer, sound);break;
            case Constants.SHOTGUN: weapon = new ShotGun(assetManager, ammo, power, isPlayer, sound);break;
            case Constants.RICOCHET: weapon = new Ricochet(assetManager, ammo, power, isPlayer, sound);break;
            case Constants.FLAMETHROWER: weapon = new Flamethrower(assetManager, ammo, power, isPlayer, sound);break;
            case Constants.CANON: weapon = new Canon(assetManager, ammo, power, isPlayer, sound);break;
            case Constants.LASERGUN: weapon = new LaserGun(assetManager, ammo, power, isPlayer, sound);break;
            case Constants. RAILGUN: weapon = new RailGun(assetManager, ammo, power, isPlayer, sound);break;
        }
        return weapon;
    }
}
