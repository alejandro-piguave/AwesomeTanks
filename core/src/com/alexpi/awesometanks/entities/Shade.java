package com.alexpi.awesometanks.entities;

import com.alexpi.awesometanks.utils.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.alexpi.awesometanks.utils.Constants;

/**
 * Created by Alex on 22/02/2016.
 */
public class Shade extends Image {
    private float posX, posY;
    private Tank player;
    private boolean isFading;
    public Shade(AssetManager manager, Tank player, float posX, float posY){
        setDrawable(new TextureRegionDrawable(new TextureRegion(manager.get("sprites/shade.png",Texture.class))));
        setBounds(posX* Constants.TILE_SIZE,posY*Constants.TILE_SIZE,Constants.TILE_SIZE /2,Constants.TILE_SIZE /2);
        this.posX = posX+.25f;
        this.posY = posY+.25f;
        this.player = player;
    }

    public void fadeOut(){addAction(Actions.fadeOut(.75f));}

    @Override
    public void act(float delta) {
        super.act(delta);
        if(!isFading){
            float distanceFromTank = (float) Utils.fastHypot(getPosX() - player.getPosX(), getPosY() - player.getPosY());
            if(distanceFromTank < player.visibilityRadius){
                isFading = true;
                fadeOut();
            }
        } else if(!isVisible()) remove();
    }

    public float getPosX() {return posX;}
    public float getPosY() {return posY;}
}
