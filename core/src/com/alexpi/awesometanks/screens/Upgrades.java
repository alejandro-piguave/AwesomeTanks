package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.Performance;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Styles;

import java.util.Vector;

/**
 * Created by Alex on 29/01/2016.
 */
public class Upgrades extends BaseScreen {
    Stage stage;
    Table table, performance, buttons, currentWeaponTable;
    TextButton play;
    Label money, weaponPrice;
    Vector<Performance> performances;
    Vector<ImageButton> weapons;
    ImageButton currentWeaponImage;
    Performance weaponAmmo, weaponPower;
    Skin uiSkin;
    Preferences gameValues;
    int currentWeapon, moneyValue, values[][];
    boolean availableWeapons[];

    public Upgrades(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        gameValues = Gdx.app.getPreferences("values");

        weapons = new Vector<>(8);
        performances = new Vector<>(4);
        uiSkin = game.getManager().get("uiskin/uiskin.json");
        stage = new Stage();

        table = new Table();table.setFillParent(true);
        performance = new Table();
        buttons = new Table();
        currentWeaponTable = new Table();

        values = new int[2][7];
        for (int i = 0; i < 7;i++){
            values[0][i] = gameValues.getInteger("power" + i, 0);
            values[1][i] = gameValues.getInteger("ammo" + i, 20);}
        availableWeapons = new boolean[7];
        for (int i = 0; i < 7;i++)
            availableWeapons[i] = gameValues.getBoolean("weapon"+i,true);

        for(int i = 0;i< 4;i++){
            String name = "";
            switch (i){
                case 0: name = "health";break;
                case 1: name = "speed";break;
                case 2: name = "rotation";break;
                case 3: name = "visibility";break;}
            performances.add(new Performance(name,uiSkin,(int)(Constants.tileSize/4),gameValues.getInteger(name),5f,500));
            performances.get(i).changePrice(500+performances.get(i).getValue()*500);
        }

        play = new TextButton("Play", Styles.getTextButtonStyle((int) (Constants.tileSize / 3)));

        moneyValue = gameValues.getInteger("money",1500);
        money = new Label(moneyValue + " $", Styles.getLabelStyle((int) (Constants.tileSize)));
        weaponPrice = new Label("",Styles.getLabelStyle((int) (Constants.tileSize)/2));
        weaponPrice.setAlignment(Align.center);

        for(int i = 0; i < 7;i++){
            Texture up = game.getManager().get("icons/icon_" + i + ".png"),
                    disabled= game.getManager().get("icons/icon_disabled_"+i+".png");
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiSkin.get(Button.ButtonStyle.class));
            style.imageUp = new TextureRegionDrawable(new TextureRegion(up));
            style.imageDisabled = new TextureRegionDrawable(new TextureRegion(disabled));
            weapons.add(new ImageButton(style));}

        for(final ImageButton i: weapons){
            if(weapons.indexOf(i) > 0)i.setDisabled(availableWeapons[weapons.indexOf(i)]);
            i.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentWeapon = weapons.indexOf(i);

                    weaponPower.setValue(values[0][currentWeapon]);
                    weaponPower.changePrice(Constants.prices[0][currentWeapon] + (Constants.prices[0][currentWeapon] / 2) * weaponPower.getValue());

                    weaponAmmo.setValue(values[1][currentWeapon]);
                    weaponAmmo.changePrice(Constants.prices[1][currentWeapon]);

                    currentWeaponImage.setStyle(i.getStyle());

                    if(i.isDisabled()){weaponAmmo.setVisible(false);weaponPower.setVisible(false);weaponPrice.setVisible(true);}
                    else{weaponAmmo.setVisible(true);weaponPower.setVisible(true);}

                    if(currentWeapon == 0){weaponAmmo.setVisible(false);weaponPrice.setVisible(false);}
                    else {currentWeaponImage.setDisabled(availableWeapons[currentWeapon]);}

                    weaponPrice.setText("Price: "+Constants.prices[2][currentWeapon]);

                }
            });buttons.add(i).size(Constants.tileSize,Constants.tileSize).pad(Constants.tileSize/5);}


        currentWeaponImage = new ImageButton(weapons.firstElement().getStyle());
        currentWeaponImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(weapons.get(currentWeapon).isDisabled() && (moneyValue-Constants.prices[2][currentWeapon])> 0){
                    money.setText((moneyValue -= Constants.prices[2][currentWeapon]) + " $");
                    availableWeapons[currentWeapon] = false;
                    weapons.get(currentWeapon).setDisabled(false);
                    weaponPrice.setVisible(false);
                    currentWeaponImage.setDisabled(false);

                    weaponAmmo.setVisible(true);
                    weaponPower.setVisible(true);
                }}});

        weaponAmmo = new Performance("Ammo",uiSkin,(int)(Constants.tileSize/4),values[0][0],100f,100);
        weaponAmmo.setVisible(false);
        weaponAmmo.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponAmmo.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    weaponAmmo.increaseValue(20);
                    values[1][currentWeapon] = weaponAmmo.getValue();
                    money.setText((moneyValue -= weaponAmmo.getPrice()) + " $");
                }}});

        weaponPower = new Performance("Power",uiSkin,(int)(Constants.tileSize/4),values[1][0],5,200 + 100 * values[0][0]);
        weaponPower.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponPower.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    weaponPower.increaseValue(1);
                    values[0][currentWeapon] = weaponPower.getValue();
                    money.setText((moneyValue -= weaponPower.getPrice()) + " $");
                }}});

        for(final Performance p: performances){
            p.getBuyButton().addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (p.canBuy(moneyValue)) {
                        p.increaseValue(1);
                        money.setText((moneyValue-=p.getPrice()) + " $");
                        p.changePrice(p.getPrice()+500);
                        if(p.getValue() == p.getMaxValue())p.getBuyButton().setVisible(false);
                    }
                }
            });
            if(p.getValue() == p.getMaxValue())p.getBuyButton().setVisible(false);
            performance.add(p).size(Constants.tileSize*2f, Constants.tileSize).row();
        }

        currentWeaponTable.add(currentWeaponImage).size(Constants.tileSize * 2, Constants.tileSize * 2).row();
        currentWeaponTable.add(weaponPower).size(Constants.tileSize*2,Constants.tileSize).row();
        currentWeaponTable.add(weaponAmmo).size(Constants.tileSize*2,Constants.tileSize).row();


        table.add(money).colspan(3).row();
        table.add(performance);
        table.add(weaponPrice).width(Constants.tileSize);
        table.add(currentWeaponTable).row();
        table.add(buttons).colspan(3).row();
        table.add(play).size(Constants.tileSize * 3, Constants.tileSize).pad(Constants.tileSize / 6).colspan(3);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(TRANSITION_DURATION)));

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for(Performance p: performances)
                    gameValues.putInteger(p.getName(),p.getValue());

                for(int i = 0; i< 7;i++){
                    gameValues.putInteger("power"+i,values[0][i]);
                    gameValues.putInteger("ammo"+i,values[1][i]);
                    gameValues.putBoolean("weapon"+i,availableWeapons[i]);
                }
                gameValues.putInteger("money",moneyValue);
                gameValues.flush();
                stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.levelScreen);}})));}});
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
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.mainScreen);}})));}
    }
}
