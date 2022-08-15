package com.alexpi.awesometanks.entities;

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
    public Shade( AssetManager manager, float posX, float posY){
        setDrawable(new TextureRegionDrawable(new TextureRegion(manager.get("sprites/shade.png",Texture.class))));
        setBounds(posX* Constants.tileSize,posY*Constants.tileSize,Constants.tileSize/2,Constants.tileSize/2);
        this.posX = posX+.25f;
        this.posY = posY+.25f;
    }

    public void fadeOut(){addAction(Actions.fadeOut(.75f));}

    @Override
    public void act(float delta) {super.act(delta);if(!isVisible())remove();}

    public float getPosX() {return posX;}
    public float getPosY() {return posY;}
}
