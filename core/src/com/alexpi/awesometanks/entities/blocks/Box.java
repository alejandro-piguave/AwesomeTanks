package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.entities.items.FreezingBall;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.entities.items.HealthPack;
import com.alexpi.awesometanks.entities.tank.EnemyTank;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    private Group entityGroup;
    private int maxType;
    public Box(DamageListener listener, AssetManager manager, Group entityGroup, World world, Vector2 targetPosition, int posX, int posY, int level){
        super(manager,"sprites/box.png", world,new PolygonShape(),50, posX, posY, .8f, true, listener);
        this.manager = manager;
        this.entityGroup = entityGroup;
        this.targetPosition = targetPosition;
        maxType = Spawner.getMaxType(level);
    }

    public void drop(){
        switch (Utils.getRandomInt(4)){
            case 0:
                int num1 = Utils.getRandomInt(5,16);
                for(int i =0; i <num1;i++)
                    entityGroup.addActor(new GoldNugget(manager,body.getWorld(),body.getPosition()));
                break;
            case 1:
                entityGroup.addActor(new FreezingBall(manager, body.getWorld(), body.getPosition()));break;
            case 2:
                entityGroup.addActor(new HealthPack(manager,body.getWorld(),body.getPosition()));break;
            case 3:
                entityGroup.addActor(new EnemyTank(manager, entityGroup, body.getWorld(), body.getPosition(),targetPosition,.5f, maxType));break;
        }
    }

    @Override public void detach() {drop();super.detach();}
}
