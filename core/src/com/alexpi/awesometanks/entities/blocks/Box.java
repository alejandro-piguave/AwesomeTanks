package com.alexpi.awesometanks.entities.blocks;

import com.alexpi.awesometanks.entities.DamageListener;
import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.entities.Enemy;
import com.alexpi.awesometanks.entities.items.FreezingBall;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.entities.items.HealthPack;

/**
 * Created by Alex on 19/01/2016.
 */
public class Box extends Block {
    private AssetManager manager;
    private Vector2 targetPosition;
    private int maxType;
    public Box(AssetManager manager, World world, Vector2 targetPosition, DamageListener listener, int posX, int posY, int level){
        super(manager, world,new PolygonShape(), listener,50, posX, posY, .8f);
        this.manager = manager;
        this.targetPosition = targetPosition;
        maxType = Spawner.getMaxType(level);
        sprite = new Sprite(manager.get("sprites/box.png",Texture.class));
    }

    public void drop(){
        switch (Utils.getRandomInt(4)){
            case 0:
                int num1 = Utils.getRandomInt(5,16);
                for(int i =0; i <num1;i++)
                    getStage().addActor(new GoldNugget(manager,body.getWorld(),body.getPosition(), damageListener));
                break;
            case 1:
                getStage().addActor(new FreezingBall(manager, body.getWorld(), body.getPosition(), damageListener));break;
            case 2:
                getStage().addActor(new HealthPack(manager,body.getWorld(),body.getPosition(), damageListener));break;
            case 3:
                getStage().addActor(new Enemy(manager, body.getWorld(), body.getPosition(),targetPosition, damageListener,.5f, maxType));break;
        }
    }

    @Override public void detach() {drop();super.detach();}
}
