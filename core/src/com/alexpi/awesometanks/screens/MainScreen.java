package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Styles;

/**
 * Created by Alex on 09/01/2016.
 */
public class MainScreen extends BaseScreen {

    private Stage mainStage;
    private Table table;
    private TextButton play, configuration;
    private Label title;

    public MainScreen(final MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        mainStage = new Stage();
        table = new Table();

        title = new Label("Awesome\nTanks", Styles.getLabelStyle((int) (Constants.tileSize * 1.5f)));
        title.setAlignment(Align.center);
        play = new TextButton("Play", Styles.getTextButtonStyle((int) (Constants.tileSize / 3)));
        configuration = new TextButton("Settings", Styles.getTextButtonStyle((int) (Constants.tileSize / 3)));


        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.upgrades);}})));}});

        configuration.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {mainStage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.settings);}})));}});

        table.setFillParent(true);
        table.center();
        table.add(title).width(Constants.tileSize*8).pad(Constants.tileSize / 3);
        table.row();
        table.add(play).width(Constants.tileSize*3).height(Constants.tileSize).pad(Constants.tileSize / 3);
        table.row();
        table.add(configuration).width(Constants.tileSize*3).height(Constants.tileSize).pad(Constants.tileSize / 3);
        mainStage.addActor(table);
        Gdx.input.setInputProcessor(mainStage);

        mainStage.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(TRANSITION_DURATION)));
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        mainStage.act();
        mainStage.draw();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        mainStage.dispose();
    }




}
