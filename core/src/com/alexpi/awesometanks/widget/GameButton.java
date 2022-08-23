package com.alexpi.awesometanks.widget;

import com.alexpi.awesometanks.utils.Settings;
import com.alexpi.awesometanks.utils.Styles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameButton extends TextButton {
    public interface OnClickListener{
        void onClick();
    }

    public GameButton(AssetManager assetManager, final OnClickListener onClickListener, String text){
        super(text, Styles.getTextButtonStyle1(assetManager));
        final Sound clickSoundDown = assetManager.get("sounds/click_down.ogg",Sound.class);
        final Sound clickSoundUp = assetManager.get("sounds/click_down.ogg",Sound.class);

        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(Settings.INSTANCE.getSoundsOn()) clickSoundDown.play();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(Settings.INSTANCE.getSoundsOn()) clickSoundUp.play();
                if(onClickListener != null) onClickListener.onClick();
            }
        });
    }
}
