package com.alexpi.awesometanks.entities.projectiles;

import com.alexpi.awesometanks.utils.Constants;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Alex on 16/01/2016.
 */
public abstract class Projectile extends Actor{
    public Body body;
    private Fixture fixture;
    protected Sprite sprite;
    private boolean destroyed = false;

    protected boolean used;
    public float damage,height, width, speed;

    public Projectile(World world, Vector2 position, Shape shape, float angle, float speed, float measure, float damage, boolean isPlayer){
        this.damage = damage;
        used = true;
        this.speed = speed;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        bodyDef.bullet=true;
        if(shape.getType() == Shape.Type.Polygon){
            height = measure;width = measure*2;
            ((PolygonShape)shape).setAsBox(measure, measure/2);}
        else if(shape.getType() == Shape.Type.Circle){
            height = width = measure;
            shape.setRadius(measure/2);}

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.1f;
        fixtureDef.shape = shape;
        fixtureDef.restitution = .9f;
        fixtureDef.filter.categoryBits = isPlayer? Constants.CAT_PLAYER_BULLET : Constants.CAT_ENEMY_BULLET;
        fixtureDef.filter.maskBits = isPlayer? Constants.PLAYER_BULLET_MASK : Constants.ENEMY_BULLET_MASK;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
        body.setLinearVelocity(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
        body.setFixedRotation(true);

        setSize(Constants.TILE_SIZE * width, Constants.TILE_SIZE * height);
        setOrigin(getOriginX() + getWidth() / 2, getOriginY() + getHeight() / 2);
        setPosition((body.getPosition().x - width/2) * Constants.TILE_SIZE, (body.getPosition().y - height/2) * Constants.TILE_SIZE);
        setRotation(angle * MathUtils.radiansToDegrees);

    }

    public boolean isEnemy(){return fixture.getFilterData().maskBits == Constants.ENEMY_BULLET_MASK;}

    public void detach(){

        body.destroyFixture(fixture);
        body.getWorld().destroyBody(body);
        remove();
    }

    public void destroy(){
        destroyed = true;
    }

    @Override
    public void act(float delta) {
        if(destroyed){
            detach();
            return;
        }
        setPosition((body.getPosition().x - width/2) * Constants.TILE_SIZE, (body.getPosition().y - height/2) * Constants.TILE_SIZE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(sprite!=null)
            batch.draw(sprite,getX(),getY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
    }

}
