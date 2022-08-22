package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.tanks.EnemyTank;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 15/02/2016.
 */
public class Spawner extends Block {
    private long lastSpawn, interval;
    private final AssetManager manager;
    private final Vector2 targetPosition;
    private final int maxType;
    private final Group entityGroup;

    public Spawner(DamageListener listener, AssetManager manager, Group entityGroup, World world, Vector2 targetPosition, Vector2 pos, int level) {
        super(manager,"sprites/spawner.png", world,new PolygonShape(), getHealth(level), pos,1f, true, listener);
        this.manager = manager;
        this.entityGroup = entityGroup;
        this.targetPosition = targetPosition;
        this.fixture.getFilterData().maskBits = Constants.CAT_PLAYER | Constants.CAT_PLAYER_BULLET | Constants.CAT_ITEM;
        lastSpawn = System.currentTimeMillis();
        interval = 1000;
        maxType = getMaxType(level);
    }

    private static int getHealth(int level){
        return 200 + (800-200)/30*level;
    }

    public static int getMaxType(int level) {
        if(level <=3)
            return Constants.RICOCHET;
        else return Constants.RAILGUN;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(lastSpawn + interval <  System.currentTimeMillis()){
            lastSpawn = System.currentTimeMillis();
            interval = Utils.getRandomInt(10000,15000);
            entityGroup.addActor(new EnemyTank(manager, entityGroup, body.getWorld(), body.getPosition(),targetPosition, .75f, Utils.getRandomInt(maxType+1), getDamageListener() ));
        }
    }
}
