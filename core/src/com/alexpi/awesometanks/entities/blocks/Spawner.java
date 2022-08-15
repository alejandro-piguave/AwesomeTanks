package com.alexpi.awesometanks.entities.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.alexpi.awesometanks.entities.Enemy;

import java.util.Random;

/**
 * Created by Alex on 15/02/2016.
 */
public class Spawner extends Block {
    private long lastSpawn, interval;
    private Random randomizer;
    private AssetManager manager;
    private Vector2 targetPosition;

    public Spawner(AssetManager manager,World world, Vector2 targetPosition, int posX, int posY) {
        super(manager,world,new PolygonShape(),500, posX, posY,1f);
        this.manager = manager;
        this.targetPosition = targetPosition;
        randomizer = new Random();
        lastSpawn = System.currentTimeMillis();
        interval = (randomizer.nextInt(10)+10)*1000;
        sprite = new Sprite(manager.get("sprites/spawner.png",Texture.class));

    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if(lastSpawn + interval <  System.currentTimeMillis()){
            lastSpawn = System.currentTimeMillis();
            interval = (randomizer.nextInt(10)+10)*1000;
            getStage().addActor(new Enemy(manager, body.getWorld(), body.getPosition(),targetPosition, .75f, randomizer.nextInt(7)));
        }
    }
}
