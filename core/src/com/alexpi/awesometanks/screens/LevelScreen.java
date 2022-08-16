package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Styles;

/**
 * Created by Alex on 25/01/2016.
 */
public class LevelScreen extends BaseScreen {
    private Stage stage;
    private ScrollPane pane;
    private TextButton play;
    private Table table;
    private List list;
    private Skin skin;
    private Preferences gameValues;
    private Label unlockedLevel;

    public LevelScreen(MainGame game) {super(game);}
    @Override
    public void show() {
        gameValues = Gdx.app.getPreferences("values");
        skin = game.getManager().get("uiskin/uiskin.json");
        play = new TextButton("Play",Styles.getTextButtonStyle(game.getManager(), (int) (Constants.tileSize/3)));

        play.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(gameValues.getBoolean("unlocked"+list.getSelectedIndex()) || list.getSelectedIndex() == 0)
                    stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {
                        @Override public void run() {game.setScreen(new GameScreen(game,list.getSelectedIndex()));}})));
                else{
                    unlockedLevel.addAction(Actions.alpha(1f));
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            unlockedLevel.addAction(Actions.fadeOut(TRANSITION_DURATION));
                        }
                    }, 1f);
                }
            }
        });

        stage = new Stage();
        table = new Table(skin);
        table.setFillParent(true);

        unlockedLevel  = new Label("Locked Level", Styles.getLabelStyle(game.getManager(), (int) Constants.tileSize));
        unlockedLevel.setColor(Color.RED);
        unlockedLevel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        unlockedLevel.setSize(0, 0);
        unlockedLevel.addAction(Actions.alpha(0));

        list = new List(Styles.getListStyle(game.getManager(), (int) (Constants.tileSize/3)));
        Array array = new Array();
        for(int i=1;i<=30;i++) array.add("Level "+i);
        list.setItems(array);
        pane = new ScrollPane(list,skin);

        table.add(new Label("Select level",Styles.getLabelStyle(game.getManager(), (int)(Constants.tileSize/2)))).row();
        table.add(pane).width(Constants.centerY).height(Constants.tileSize * 5).row();
        table.add(play).size(Constants.tileSize * 3, Constants.tileSize).pad(Constants.tileSize / 8);
        stage.addActor(table);
        stage.addActor(unlockedLevel);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(TRANSITION_DURATION)));
    }

    @Override
    public void hide() {
        stage.dispose();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        stage.act();
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(game.upgrades);
                }
            })));}
    }
}
