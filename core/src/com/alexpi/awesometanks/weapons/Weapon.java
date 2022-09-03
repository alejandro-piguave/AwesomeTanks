package com.alexpi.awesometanks.weapons;

import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Settings;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    protected float ammo;
    protected int power;
    public Sprite sprite;
    protected Sound shotSound;
    protected float desiredAngleRotation, currentAngleRotation;
    private final String name;
    protected boolean isPlayer;
    protected boolean isCoolingDown;
    protected boolean unlimitedAmmo = false;
    protected final float coolingDownTime;
    private final float ammoConsumption;

    public Weapon(String name, AssetManager assetManager, String texturePath, String shotSoundPath, float ammo, int power, boolean isPlayer, float coolingDownTime, float ammoConsumption){
        this.name = name;
        this.isCoolingDown = false;
        this.ammoConsumption = ammoConsumption;
        this.sprite = new Sprite(assetManager.get(texturePath,Texture.class));
        this.shotSound = assetManager.get(shotSoundPath,Sound.class);
        this.ammo = ammo;
        this.power = power;
        this.isPlayer = isPlayer;
        this.coolingDownTime = coolingDownTime;
    }

    public String getName() {return name;}
    public float getAmmo(){return ammo;}
    protected void decreaseAmmo(){
        if(ammo - ammoConsumption >0)
            ammo -= ammoConsumption;
        else ammo = 0;
    }

    public boolean hasAmmo(){return ammo>0;}

    public void setDesiredAngleRotation(float x, float y) {
        desiredAngleRotation = (float) Math.atan2(y,x);
        if(desiredAngleRotation <0) desiredAngleRotation +=Math.PI*2;
    }

    public void setDesiredAngleRotation(float angle) {
        desiredAngleRotation = angle;
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

    public void draw(Batch batch, Color color, float x, float parentAlpha, float originX, float originY, float width, float height, float scaleX, float scaleY, float y){
        batch.draw(sprite, x, y, originX, originY, width, height, scaleX, scaleY, sprite.getRotation());
    }

    public float getCurrentAngleRotation() {
        return currentAngleRotation;
    }

    public void setUnlimitedAmmo(boolean unlimitedAmmo) {
        this.unlimitedAmmo = unlimitedAmmo;
    }


    public void shoot(AssetManager assetManager, Group group, World world, Vector2 position){
        if(canShoot()) {
            createProjectile(group, assetManager, world, position);
            if (Settings.INSTANCE.getSoundsOn()) shotSound.play();
            if (!unlimitedAmmo) decreaseAmmo();
            isCoolingDown = true;
            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    if(isCoolingDown) isCoolingDown = false;
                }
                }, coolingDownTime);
        }
    }

    public void await(){

    }

    public abstract void createProjectile(Group group, AssetManager assetManager, World world, Vector2 position);

    protected boolean canShoot(){return (hasAmmo() || unlimitedAmmo) && !isCoolingDown && hasRotated();}

    public static Weapon getWeaponAt(int i, AssetManager assetManager, float ammo, int power, boolean isPlayer){
        Weapon weapon = null;
        switch (i){
            case Constants.MINIGUN: weapon = new MiniGun(assetManager, ammo, power, isPlayer);break;
            case Constants.SHOTGUN: weapon = new ShotGun(assetManager, ammo, power, isPlayer);break;
            case Constants.RICOCHET: weapon = new Ricochet(assetManager, ammo, power, isPlayer);break;
            case Constants.FLAMETHROWER: weapon = new Flamethrower(assetManager, ammo, power, isPlayer);break;
            case Constants.CANON: weapon = new Canon(assetManager, ammo, power, isPlayer);break;
            case Constants.ROCKET: weapon = new RocketLauncher(assetManager, ammo, power, isPlayer); break;
            case Constants.LASERGUN: weapon = new LaserGun(assetManager, ammo, power, isPlayer);break;
            case Constants. RAILGUN: weapon = new RailGun(assetManager, ammo, power, isPlayer);break;
        }
        return weapon;
    }
}
