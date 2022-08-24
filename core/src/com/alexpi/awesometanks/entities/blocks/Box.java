package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.items.FreezingBall;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.entities.items.HealthPack;
import com.alexpi.awesometanks.entities.tanks.EnemyTank;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Alex on 19/01/2016.
 */
public class Box extends Block {
    private AssetManager manager;
    private Vector2 targetPosition;
    private int maxType;
    public Box(DamageListener listener, AssetManager manager, World world, Vector2 targetPosition, Vector2 pos, int level){
        super(manager,"sprites/box.png", world,new PolygonShape(),50, pos, .8f, true, listener);
        this.manager = manager;
        this.targetPosition = targetPosition;
        maxType = Spawner.getMaxType(level);
    }

    public void drop(){
        switch (Utils.getRandomInt(4)){
            case 0:
                int num1 = Utils.getRandomInt(5,16);
                for(int i =0; i <num1;i++)
                    getParent().addActor(new GoldNugget(manager,body.getWorld(),body.getPosition()));
                break;
            case 1:
                getParent().addActor(new FreezingBall(manager, body.getWorld(), body.getPosition()));break;
            case 2:
                getParent().addActor(new HealthPack(manager,body.getWorld(),body.getPosition()));break;
            case 3:
                getParent().addActor(new EnemyTank(manager, body.getWorld(), body.getPosition(),targetPosition, EnemyTank.Tier.MINI, Utils.getRandomInt(maxType +1), getDamageListener()));break;
        }
    }

    @Override public void detach() {
        drop();
        super.detach();

    }
}
