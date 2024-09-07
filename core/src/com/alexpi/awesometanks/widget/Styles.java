package com.alexpi.awesometanks.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Created by Alex on 27/01/2016.
 */
public class Styles {

    public static Label.LabelStyle getGameTitleStyle1(AssetManager assetManager){
        Label.LabelStyle style1 = new Label.LabelStyle();
        style1.font = assetManager.get("title_font1.ttf",BitmapFont.class);
        return  style1;
    }

    public static Label.LabelStyle getGameTitleStyle2(AssetManager assetManager){
        Label.LabelStyle style2 = new Label.LabelStyle();
        style2.font = assetManager.get("title_font2.ttf",BitmapFont.class);

        return  style2;
    }

    public static Label.LabelStyle getLabelStyleBackground(AssetManager assetManager){
        Label.LabelStyle style1 = new Label.LabelStyle();
        style1.font = assetManager.get("label_font1.ttf",BitmapFont.class);
        style1.background = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/label_background.9.png", Texture.class),20,20,20,20));

        return  style1;
    }

    public static Label.LabelStyle getLabelStyleSmall(AssetManager assetManager){
        Label.LabelStyle style1 = new Label.LabelStyle();
        style1.font = assetManager.get("label_font2.ttf",BitmapFont.class);
        return  style1;
    }

    public static Label.LabelStyle getLabelStyle(AssetManager assetManager, int fontSize){
        Skin skin = assetManager.get("uiskin/uiskin.json", Skin.class);
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        Label.LabelStyle style = skin.get("default", Label.LabelStyle.class);
        style.font = font;

        fontGenerator.dispose();

        return  style;
    }

    public static TextButton.TextButtonStyle getTextButtonStyleSmall(AssetManager assetManager){
        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/button_background.9.png", Texture.class),20,20,20,20));
        NinePatchDrawable patchDown = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/button_background_down.9.png", Texture.class),20,20,20,20));
        return new TextButton.TextButtonStyle(patch, patchDown, patch, assetManager.get("button_font2.ttf", BitmapFont.class));
    }

    public static TextButton.TextButtonStyle getTextButtonStyle1(AssetManager assetManager){
        NinePatchDrawable patch = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/button_background.9.png", Texture.class),20,20,20,20));
        NinePatchDrawable patchDown = new NinePatchDrawable(new NinePatch(assetManager.get("sprites/button_background_down.9.png", Texture.class),20,20,20,20));
        return new TextButton.TextButtonStyle(patch, patchDown, patch, assetManager.get("button_font1.ttf", BitmapFont.class));
    }

    public static Touchpad.TouchpadStyle getTouchPadStyle(AssetManager assetManager){
        Skin joystickSkin = new Skin();
        joystickSkin.add("touchBackground", assetManager.get("touchBackground.png"));
        joystickSkin.add("touchKnob", assetManager.get("touchKnob.png"));

        Touchpad.TouchpadStyle joystickStyle = new Touchpad.TouchpadStyle();
        joystickStyle.background = joystickSkin.getDrawable("touchBackground");
        joystickStyle.knob = joystickSkin.getDrawable("touchKnob");

        return joystickStyle;
    }
}
