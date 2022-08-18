package com.alexpi.awesometanks.entities.items;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.Detachable;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;
import com.alexpi.awesometanks.utils.Constants;

/**
 * Created by Alex on 19/02/2016.
 */
public abstract class Item extends Actor implements Detachable {

    private Sprite sprite;
    protected Body body;
    protected Fixture fixture;
    public float size;
    protected DamageListener listener;

    public Item(AssetManager manager,String fileName, World world,Vector2 position, DamageListener listener, float size){
        this.size = size;
        this.listener = listener;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape shape = new CircleShape();
        shape.setRadius(size/2);

        fixtureDef.density = 2f;
        fixtureDef.restitution = .1f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.CAT_ITEM;
        fixtureDef.filter.maskBits = Constants.CAT_TANK | Constants.CAT_BLOCK | Constants.CAT_ENEMY;

        body = world.createBody(bodyDef);
        body.setLinearDamping(1f);
        body.setAngularDamping(.5f);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        sprite = new Sprite(manager.get(fileName, Texture.class));
        setSize(size * Constants.TILE_SIZE, size * Constants.TILE_SIZE);
        setOrigin(getOriginX() + getWidth() / 2, getOriginY() + getHeight() / 2);
        setPosition((body.getPosition().x - size / 2) * Constants.TILE_SIZE, (body.getPosition().y - size / 2) * Constants.TILE_SIZE);
    }
    @Override
    public void act(float delta) {
        setPosition((body.getPosition().x - size / 2) * Constants.TILE_SIZE, (body.getPosition().y - size / 2) * Constants.TILE_SIZE);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void detach(){
        body.destroyFixture(fixture);
        body.getWorld().destroyBody(body);
        remove();
    }

    public void pickUp(){
        listener.onDeath(this);
    }

}
