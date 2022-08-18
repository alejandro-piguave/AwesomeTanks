package com.alexpi.awesometanks.widget;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class GameProgressBar extends Actor {
    private float maxValue, value;
    private boolean vertical;
    private final NinePatchDrawable background,  foreground;
    private static final float borderWidth = 4f;
    public GameProgressBar(AssetManager assetManager, float maxValue, float initialValue, boolean vertical){
        if(maxValue <= 0) throw new IllegalArgumentException("max value must be positive");
        if( initialValue > maxValue) throw new IllegalArgumentException("initialValue can't be greater tha max");
        this.maxValue = maxValue;
        this.value = initialValue;
        this.vertical = vertical;
        this.background = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/progress_bar_background.9.png", Texture.class),6,6,6,6));
        this.foreground = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/progress_bar_foreground.9.png", Texture.class),6,6,6,6));
        if(vertical) setWidth(20);
        else setHeight(20);
    }

    public GameProgressBar(AssetManager assetManager, float maxValue, float initialValue){
        this(assetManager, maxValue, initialValue, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        background.draw(batch,getX(),getY(),getWidth(),getHeight());
        if(value > 0) {
            if(vertical)
                foreground.draw(batch, getX() + borderWidth, getY() + borderWidth, (getWidth() - 2*borderWidth) ,
                        (getHeight() - 2* borderWidth) * (value / maxValue));
                else foreground.draw(batch, getX() + borderWidth, getY() + borderWidth,
                    (getWidth() - 2*borderWidth) * (value / maxValue), getHeight() - 2* borderWidth);
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        if( value > maxValue) throw new IllegalArgumentException("initialValue can't be greater tha max");
        this.value = value;
    }

    public float getMaxValue() {
        return maxValue;
    }
}
