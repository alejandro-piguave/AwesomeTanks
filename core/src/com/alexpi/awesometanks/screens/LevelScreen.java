package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Styles;
import com.alexpi.awesometanks.widget.GameButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by Alex on 25/01/2016.
 */
public class LevelScreen extends BaseScreen {
    private Stage stage;
    private Label lockedLevelLabel;
    private SpriteBatch batch;
    private Texture background;
    private static final int LEVEL_TABLE_COLUMN_COUNT = 10;

    public LevelScreen(MainGame game) {super(game);}
    @Override
    public void show() {

        stage = new Stage(new ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        batch = new SpriteBatch();
        background = game.getManager().get("sprites/background.png", Texture.class);
        Table table = new Table();
        table.setFillParent(true);

        lockedLevelLabel = new Label("Locked Level", Styles.getLabelStyle(game.getManager(), (int) Constants.TILE_SIZE));
        lockedLevelLabel.setColor(Color.RED);
        lockedLevelLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        lockedLevelLabel.setSize(0, 0);
        lockedLevelLabel.addAction(Actions.alpha(0));

        Table levelTable = new Table();
        for(int i = 0; i < Constants.LEVEL_COUNT; i++){
            final int finalI = i;
            GameButton levelButton = new GameButton(game.getManager(), new GameButton.OnClickListener() {
                @Override
                public void onClick() {
                    if(game.getGameValues().getBoolean("unlocked"+ finalI) || finalI == 0)
                        stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {
                            @Override public void run() {game.setScreen(new GameScreen(game,finalI));}})));
                    else{
                        lockedLevelLabel.addAction(Actions.alpha(1f));
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                lockedLevelLabel.addAction(Actions.fadeOut(TRANSITION_DURATION));
                            }
                        }, 1f);
                    }
                }
            },String.valueOf(finalI+1));
            Cell<GameButton> gameButtonCell = levelTable.add(levelButton).size(80).pad(16);
            if((i+1) % LEVEL_TABLE_COLUMN_COUNT == 0)gameButtonCell.row();
        }

        table.add(new Label("Select level",Styles.getLabelStyleBackground(game.getManager()))).padTop(32).row();
        table.add(levelTable).expandY().expandX().fillX().top().padTop(64).row();

        stage.addActor(table);
        stage.addActor(lockedLevelLabel);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(TRANSITION_DURATION)));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width ,height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
        batch.dispose();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        batch.begin();
        batch.draw(background,0f,0f,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act();
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(game.upgradesScreen);
                }
            })));}
    }
}
