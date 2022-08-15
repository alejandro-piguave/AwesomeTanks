package com.alexpi.awesometanks.entities.blocks;

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

import java.util.Random;

/**
 * Created by Alex on 19/01/2016.
 */
public class Box extends Block {
    private AssetManager manager;
    private Vector2 targetPosition;
    public Box(AssetManager manager, World world, Vector2 targetPosition,int posX, int posY){
        super(manager, world,new PolygonShape(), 50, posX, posY, .8f);
        this.manager = manager;
        this.targetPosition = targetPosition;
        sprite = new Sprite(manager.get("sprites/box.png",Texture.class));
    }

    public void drop(){
        Random rnd = new Random();
        switch (rnd.nextInt(4)){
            case 0:
                int num1 = rnd.nextInt(11)+5;
                for(int i =0; i <num1;i++)
                    getStage().addActor(new GoldNugget(manager,body.getWorld(),body.getPosition(),rnd.nextFloat()*(.25f-.1f)+.1f, (float) (rnd.nextFloat()*(Math.PI*2))));
                break;
            case 1:
                getStage().addActor(new FreezingBall(manager, body.getWorld(), body.getPosition()));break;
            case 2:
                getStage().addActor(new HealthPack(manager,body.getWorld(),body.getPosition()));break;
            case 3:
                getStage().addActor(new Enemy(manager, body.getWorld(), body.getPosition(),targetPosition,.5f,rnd.nextInt(7)));break;
        }
    }

    @Override public void detach() {drop();super.detach();}
}
