package com.alexpi.awesometanks.screens;

import com.badlogic.gdx.Screen;
import com.alexpi.awesometanks.MainGame;

/**
 * Created by Alex on 30/12/2015.
 */
public abstract class BaseScreen implements Screen{

    private MainGame game;

    public BaseScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    public MainGame getGame() {
        return game;
    }
}
