package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.entities.DamageableActor;
import com.alexpi.awesometanks.utils.Constants;

/**
 * Created by Alex on 19/01/2016.
 */
public abstract class Block extends DamageableActor{

    public Sprite sprite;
    Body body;
    Fixture fixture;
    protected float size;
    protected int posX, posY;

    public Block(AssetManager manager, World world, Shape shape, DamageListener listener, int health, int posX, int posY, float size){
        super(manager, listener, health);
        this.posX = posX; this.posY = posY;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(posX + .5f, posY + .5f);

        if(shape.getType() == Shape.Type.Polygon)
            ((PolygonShape)shape).setAsBox(size / 2, size / 2);
        else if(shape.getType() == Shape.Type.Circle)
            shape.setRadius(size/2);

        fixtureDef.density = 50f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.CAT_BLOCK;
        fixtureDef.filter.maskBits = Constants.CAT_TANK | Constants.CAT_BULLET | Constants.CAT_ITEM | Constants.CAT_ENEMY;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        body.setUserData(this);

        shape.dispose();

        setSize(size * Constants.TILE_SIZE, size * Constants.TILE_SIZE);
        setPosition((body.getPosition().x - size / 2) * Constants.TILE_SIZE, (body.getPosition().y - size / 2) * Constants.TILE_SIZE);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void detach(){
        super.detach();
        body.destroyFixture(fixture);
        body.getWorld().destroyBody(body);
        remove();
    }

}
