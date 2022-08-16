package com.alexpi.awesometanks.entities;

import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.weapons.Weapon;


/**
 * Created by Alex on 17/02/2016.
 */
public class Enemy extends DamageableActor{
    private Body body;
    private Fixture fixture;
    private Sprite bodySprite, wheelsSprite, freezed;
    private Vector2 movement, targetPosition;
    private Weapon weapon;
    private float size, currentAngleRotation, desiredAngleRotation, movementSpeed = 60f;
    private boolean isFreezed, allowSounds;
    private AssetManager manager;
    private final static float ROTATION_SPEED = .035f;

    public Enemy(AssetManager manager, World world, Vector2 position, Vector2 targetPosition, float size, int type){
        super(manager,100 + Constants.prices[1][type]);
        bodySprite = new Sprite(manager.get("sprites/tank_body.png",Texture.class));
        wheelsSprite = new Sprite(manager.get("sprites/tank_wheels.png",Texture.class));
        freezed = new Sprite(manager.get("sprites/freezed.png",Texture.class));
        this.size = size;
        allowSounds = Gdx.app.getPreferences("settings").getBoolean("areSoundsActivated");
        this.manager = manager;
        movement = new Vector2();
        this.targetPosition = targetPosition;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x + .5f, position.y + .5f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size / 2, size / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 10f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.CAT_ENEMY;
        fixtureDef.filter.maskBits = Constants.CAT_BLOCK | Constants.CAT_ITEM | Constants.CAT_TANK | Constants.CAT_BULLET | Constants.CAT_ENEMY;

        body = world.createBody(bodyDef);
        body.setLinearDamping(3f);
        body.setAngularDamping(3f);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        body.setUserData(this);

        shape.dispose();

        weapon = Weapon.getWeaponAt(type, manager, 1, 2, Constants.ENEMY_BULLET_MASK, allowSounds);
        weapon.setUnlimitedAmmo(true);

        setSize(size * Constants.tileSize, size * Constants.tileSize);
        setOrigin(getOriginX() + getWidth() / 2, getOriginY() + getHeight() / 2);
        setPosition((body.getPosition().x - size / 2) * Constants.tileSize, (body.getPosition().y - size / 2) * Constants.tileSize);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(wheelsSprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.draw(bodySprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.draw(weapon.sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), weapon.sprite.getRotation());
        if(isFreezed)batch.draw(freezed,getX(),getY(),getWidth(),getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        float distanceFromTarget = (float) (Utils.fastHypot(targetPosition.x-body.getPosition().x,targetPosition.y-body.getPosition().y));
        if(distanceFromTarget < 7 && !isFreezed && isAlive()){
            setDesiredAngleRotation(targetPosition.x - body.getPosition().x, targetPosition.y - body.getPosition().y);
            updateAngleRotation();
            body.setTransform(body.getPosition(), currentAngleRotation);
            if(hasRotated())movement.set(movementSpeed * MathUtils.cos(currentAngleRotation) * delta,movementSpeed * MathUtils.sin(currentAngleRotation) * delta);
            else movement.set(0,0);

            body.setLinearVelocity(movement);

            weapon.setDesiredAngleRotation(targetPosition.x - body.getPosition().x, targetPosition.y - body.getPosition().y);
            weapon.updateAngleRotation(ROTATION_SPEED);
            if(weapon.hasRotated()) weapon.shoot(manager,getStage(),body.getWorld(),body.getPosition());
        }
        setPosition((body.getPosition().x - size / 2) * Constants.tileSize, (body.getPosition().y - size / 2) * Constants.tileSize);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        super.act(delta);
    }

    @Override
    public void detach() {
        super.detach();
        drop();
        body.destroyFixture(fixture);
        body.getWorld().destroyBody(body);
        remove();
    }


    public void freeze(float freezingTime){
        isFreezed = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isFreezed = false;
            }
        }, freezingTime);
    }
    public void setDesiredAngleRotation(float x, float y) {
        desiredAngleRotation = (float) Math.atan2(y,x);
        if(desiredAngleRotation < 0)
            desiredAngleRotation +=Math.PI*2;
    }
    public void updateAngleRotation() {
        float diff = desiredAngleRotation - currentAngleRotation;
        if(diff<0)diff+=Math.PI*2;

        if(diff>=Math.PI){currentAngleRotation-=.075f;diff-=Math.PI;}
        else if(diff<Math.PI) currentAngleRotation+=.075f;

        if(diff<.075f)currentAngleRotation = desiredAngleRotation;

        if(currentAngleRotation>Math.PI*2)currentAngleRotation= 0;
        else if(currentAngleRotation<0)currentAngleRotation= (float) (Math.PI*2);
    }
    public boolean hasRotated(){return currentAngleRotation == desiredAngleRotation;}

    public void drop(){
        int num1 = Utils.getRandomInt(5,16);
        for(int i =0; i <num1;i++)
            getStage().addActor(new GoldNugget(manager,body.getWorld(),body.getPosition()));
    }

}
