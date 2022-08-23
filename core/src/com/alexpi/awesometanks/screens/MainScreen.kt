package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.alexpi.awesometanks.widget.GameButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Styles;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.awt.Event;

/**
 * Created by Alex on 09/01/2016.
 */
public class MainScreen extends BaseScreen {

    private Stage stage;

    public MainScreen(final MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        final boolean soundsOn = game.getGameSettings().getBoolean("areSoundsActivated");
        stage = new Stage(new FillViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        Image background = new Image(game.getManager().get("sprites/background.png", Texture.class));
        background.setBounds(0,0,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        Table table = new Table();

        Label title1 = new Label("Awesome", Styles.getGameTitleStyle1(game.getManager()));
        Label title2 = new Label("Tanks", Styles.getGameTitleStyle2(game.getManager()));
        title1.setAlignment(Align.center);
        title2.setAlignment(Align.center);
        GameButton playButton = new GameButton(game.getManager(), new GameButton.OnClickListener() {
            @Override
            public void onClick() {
                stage.addAction(
                        Actions.sequence(
                                Actions.fadeOut(TRANSITION_DURATION),
                                Actions.run(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                game.setScreen(game.upgrades);
                                            }
                                        })
                        )
                );
            }
        },"Play", soundsOn);
        GameButton settingsButton = new GameButton(game.getManager(), new GameButton.OnClickListener() {
            @Override
            public void onClick() {
                stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.upgrades);}})));
            }
        },"Settings", soundsOn);

        table.setFillParent(true);
        table.center();
        table.add(title1).width(Constants.TILE_SIZE *8).row();
        table.add(title2).width(Constants.TILE_SIZE *8).padBottom(Constants.TILE_SIZE / 3).row();
        table.add(playButton).width(Constants.TILE_SIZE *3).height(Constants.TILE_SIZE).pad(Constants.TILE_SIZE / 3);
        table.row();
        table.add(settingsButton).width(Constants.TILE_SIZE *3).height(Constants.TILE_SIZE).pad(Constants.TILE_SIZE / 3);
        stage.addActor(background);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        stage.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(TRANSITION_DURATION)));
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1f);
        stage.act();
        stage.draw();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }

}
