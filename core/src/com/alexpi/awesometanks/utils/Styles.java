package com.alexpi.awesometanks.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 * Created by Alex on 27/01/2016.
 */
public class Styles {


    public static List.ListStyle getListStyle(int fontSize){
        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        List.ListStyle style = skin.get("default", List.ListStyle.class);
        style.font = font;

        fontGenerator.dispose();

        return  style;
    }
    public static Label.LabelStyle getLabelStyle(int fontSize){
        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        Label.LabelStyle style = skin.get("default", Label.LabelStyle.class);
        style.font = font;

        fontGenerator.dispose();

        return  style;
    }

    public static TextButton.TextButtonStyle getTextButtonStyle(int fontSize){
        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        TextButton.TextButtonStyle style = skin.get("default", TextButton.TextButtonStyle.class);
        style.font = font;

        fontGenerator.dispose();

        return  style;
    }

    public static Window.WindowStyle getWindowStyle(int fontSize){
        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        Window.WindowStyle style = skin.get("default", Window.WindowStyle.class);
        style.titleFont = font;

        fontGenerator.dispose();

        return  style;
    }
}
