package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.weapons.Weapon;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 18/02/2016.
 */
public class Turret extends Block {
    private Weapon weapon;
    private Vector2 targetPosition;
    private AssetManager manager;
    private Group entityGroup;
    private final static float ROTATION_SPEED = .035f;
    public Turret(DamageListener listener, AssetManager manager, Group entityGroup, World world, Vector2 targetPosition, int posX, int posY, int type, boolean sound) {
        super(manager,"sprites/turret_base.png",  world,new PolygonShape(),500, posX, posY, .8f, true, listener);
        this.manager = manager;
        this.entityGroup = entityGroup;
        Filter filter = new Filter();
        filter.categoryBits = Constants.CAT_ENEMY;
        fixture.setFilterData(filter);
        this.targetPosition = targetPosition;

        weapon = Weapon.getWeaponAt(type, manager, 1, 3, false, sound);
        weapon.setUnlimitedAmmo(true);
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(weapon.sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), weapon.sprite.getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float distanceFromTarget  = (float) (Utils.fastHypot(targetPosition.x-body.getPosition().x,targetPosition.y-body.getPosition().y));

        if(distanceFromTarget < 4 && isAlive()){
            weapon.setDesiredAngleRotation(targetPosition.x - body.getPosition().x, targetPosition.y - body.getPosition().y);
            weapon.updateAngleRotation(ROTATION_SPEED);
            if(weapon.hasRotated())weapon.shoot(manager,entityGroup,body.getWorld(),body.getPosition());
        }
    }

}
