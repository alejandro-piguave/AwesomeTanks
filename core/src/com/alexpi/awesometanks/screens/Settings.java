package com.alexpi.awesometanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Styles;

/**
 * Created by Alex on 23/01/2016.
 */
public class Settings extends BaseScreen {

    private Stage stage;
    private Table table;
    private Skin skin;
    private Label title;
    private CheckBox left, right, soundFX;
    private Label alignment, sounds, tankColor;
    private Preferences settings;
    private ButtonGroup align;
    private ScrollPane pane;
    private List list;

    public Settings(MainGame game) {
        super(game);
    }
    @Override
    public void show() {
        settings = Gdx.app.getPreferences("settings");
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        table = new Table();
        table.setFillParent(true);table.top();

        left = new CheckBox("Align to left.",getCheckBoxStyle((int) (Constants.tileSize/3)));
        right = new CheckBox("Align to right.",getCheckBoxStyle((int) (Constants.tileSize/3)));

        align = new ButtonGroup(left,right);

        soundFX = new CheckBox("Enable sounds.",getCheckBoxStyle((int) (Constants.tileSize/3)));

        if(settings.getBoolean("isAlignedToLeft"))left.setChecked(true); else right.setChecked(true);
        if(settings.getBoolean("areSoundsActivated"))soundFX.setChecked(true);

        title = new Label("Settings", Styles.getLabelStyle((int) (Constants.tileSize)));
        alignment = new Label("Joystick's alignment",Styles.getLabelStyle((int) (Constants.tileSize/2)));
        sounds = new Label("Sounds",Styles.getLabelStyle((int) (Constants.tileSize/2)));
        tankColor = new Label("Tank Color",Styles.getLabelStyle((int) (Constants.tileSize/2)));



        Array array = new Array();
        for(int i =0;i<10;i++)
            array.add(Constants.colorNames[i]);

        list = new List(Styles.getListStyle((int) (Constants.tileSize/3)));
        list.setItems(array);
        list.setSelectedIndex(settings.getInteger("tankColor",0));

        pane = new ScrollPane(list, skin);

        table.add(title).pad(Constants.tileSize / 6).center().row();
        table.add(alignment).pad(Constants.tileSize / 4).center().row();
        table.add(left).left();table.row();
        table.add(right).left();table.row();
        table.add(sounds).pad(Constants.tileSize/4).center().row();
        table.add(soundFX).left().row();
        table.add(tankColor).pad(Constants.tileSize/4).center().row();
        table.add(pane).size(Constants.tileSize*4,Constants.tileSize*2);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(.5f)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        stage.act();
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {game.setScreen(game.mainScreen);}
            })));}

    }


    @Override
    public void hide() {
        settings.putBoolean("isAlignedToLeft",left.isChecked());
        settings.putBoolean("areSoundsActivated",soundFX.isChecked());
        settings.putInteger("tankColor",list.getSelectedIndex());
        settings.flush();
        stage.dispose();
        Gdx.input.setInputProcessor(null);

    }


    public CheckBox.CheckBoxStyle getCheckBoxStyle(int fontSize){
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        BitmapFont font = fontGenerator.generateFont(parameter);

        CheckBox.CheckBoxStyle style = skin.get("default", CheckBox.CheckBoxStyle.class);
        style.font = font;

        fontGenerator.dispose();

        return  style;
    }
}
