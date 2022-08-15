package com.alexpi.awesometanks.weapons;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Alex on 03/01/2016.
 */
public abstract class Weapon {
    protected int ammo;
    protected int power;
    public Sprite sprite;
    protected Sound shotSound;
    protected float desiredAngleRotation, currentAngleRotation;
    private String name;
    protected short filter;
    protected boolean canShoot;

    public Weapon(String name){this.name = name;canShoot = true;}
    public void detach(){
        sprite.getTexture().dispose();
        shotSound.dispose();
    }

    public String getName() {return name;}
    public int getAmmo(){return ammo;}
    public void decreaseAmmo(){if(ammo>0)ammo--;}

    public void setValues(int ammo, int power, short filter) {this.ammo = ammo;this.power = power;this.filter = filter;}

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

    public abstract void shoot(AssetManager manager,Stage stage, World world, Vector2 currentPosition, boolean sound);

    public static Weapon getWeaponAt(int i){
        Weapon weapon = null;
        switch (i){
            case 0:weapon = new MiniGun();break;
            case 1:weapon = new ShotGun();break;
            case 2:weapon = new Ricochet();break;
            case 3:weapon = new Flamethrower();break;
            case 4:weapon = new Canon();break;
            case 5:weapon = new LaserGun();break;
            case 6:weapon = new RailGun();break;
        }
        return weapon;
    }
}
