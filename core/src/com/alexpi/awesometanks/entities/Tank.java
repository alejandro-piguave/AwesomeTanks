package com.alexpi.awesometanks.entities;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.weapons.Weapon;

import java.util.Vector;


/**
 * Created by Alex on 02/01/2016.
 */
public class Tank extends DamageableActor{

    private Sprite bodySprite, wheelsSprite;
    private int currentWeapon;
    private Vector<Weapon> weapons;
    private Body body;
    private Fixture fixture;
    public float jX, jY, size, currentAngleRotation, desiredAngleRotation, rotationSpeed, movementSpeed;
    public boolean alive, isMoving, hasToShoot, allowSounds;
    public Vector2 movement;
    public int money;
    public float visibilityRadius;
    private AssetManager manager;

    public Tank(AssetManager manager, World world, Vector2 position,Preferences gameValues, Color color, int money,boolean sounds){
        super(manager, 1200 + gameValues.getInteger("health")*200 );
        allowSounds = sounds;this.manager = manager;this.money = money;
        bodySprite = new Sprite(manager.get("sprites/tank_body.png",Texture.class));
        wheelsSprite = new Sprite(manager.get("sprites/tank_wheels.png",Texture.class));

        size = .75f;
        currentAngleRotation = desiredAngleRotation = jX = jY = 0;
        alive = true;isMoving=false;
        movement = new Vector2();

        weapons = new Vector(7);

        for(int i = 0; i < 7; i++)
            weapons.add(Weapon.getWeaponAt(i));

        for(int i = 0; i < 7; i++)
            weapons.get(i).setValues(gameValues.getInteger("ammo" + i), gameValues.getInteger("power" + i), Constants.TANK_BULLET_MASK);

        visibilityRadius = 1.5f+ gameValues.getInteger("visibility")/2f;
        rotationSpeed = .05f+gameValues.getInteger("rotation") /40f;
        movementSpeed = 100f + gameValues.getInteger("speed")*10f;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + .5f, position.y + .5f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size / 2, size / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 10f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.CAT_TANK;
        fixtureDef.filter.maskBits = Constants.CAT_BLOCK | Constants.CAT_ITEM | Constants.CAT_ENEMY | Constants.CAT_BULLET;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        body.setUserData(this);

        shape.dispose();

        setSize(size * Constants.tileSize, size * Constants.tileSize);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition((body.getPosition().x - size / 2) * getWidth(), (body.getPosition().y - size / 2) * getHeight());
        setColor(color);
        setJustHit(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getColor().a*=parentAlpha;
        batch.draw(wheelsSprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(getColor());
        batch.draw(bodySprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.draw(weapons.get(currentWeapon).sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), weapons.get(currentWeapon).sprite.getRotation());
        batch.setColor(1, 1, 1, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if(isMoving){
            movement.set(movementSpeed * jX * delta, movementSpeed * jY * delta);
            setDesiredAngleRotation(jX,jY);
            updateAngleRotation();
            body.setLinearVelocity(movement);
            body.setTransform(body.getPosition(), currentAngleRotation);
        }else{
            body.setLinearVelocity(0, 0);
            body.setAngularVelocity(0);
        }
        getCurrentWeapon().updateAngleRotation(rotationSpeed);

        if(hasToShoot && getCurrentWeapon().hasRotated() && isAlive()){
            getCurrentWeapon().shoot(manager, getStage(), body.getWorld(), body.getPosition(), allowSounds);
            getCurrentWeapon().decreaseAmmo();
            hasToShoot = false;
        }
        setPosition((body.getPosition().x - size / 2) * Constants.tileSize, (body.getPosition().y - size / 2) * Constants.tileSize);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        super.act(delta);
    }
    public void setDirection(float x, float y){jX = x;jY = y;}
    public void setDesiredAngleRotation(float x, float y) {
        desiredAngleRotation = (float) Math.atan2(y,x);
        if(desiredAngleRotation < 0)
            desiredAngleRotation +=Math.PI*2;
    }
    public void updateAngleRotation() {
        float diff = desiredAngleRotation - currentAngleRotation;
        if(diff<0)diff+=Math.PI*2;

        if(diff>=Math.PI){currentAngleRotation-=.1f;diff-=Math.PI;}
        else if(diff<Math.PI) currentAngleRotation+=.1f;

        if(diff<.1f)currentAngleRotation = desiredAngleRotation;

        if(currentAngleRotation>Math.PI*2)currentAngleRotation= 0;
        else if(currentAngleRotation<0)currentAngleRotation= (float) (Math.PI*2);
    }

    @Override
    public void detach(){
        super.detach();
        body.destroyFixture(fixture);
        body.getWorld().destroyBody(body);
        remove();
        for(Weapon w: weapons)
            w.detach();
    }

    public void saveProgress(Preferences gameValues){
        for(int i =0; i<weapons.size();i++)
            gameValues.putInteger("ammo" + i, weapons.get(i).getAmmo());}

    public void setCurrentWeapon(int num){
        currentWeapon = num;
    }
    public Weapon getCurrentWeapon(){return weapons.get(currentWeapon);}
    public float getPosY() {return body.getPosition().y;}
    public float getPosX() {return body.getPosition().x;}
    public Body getBody() {return body;}
}
