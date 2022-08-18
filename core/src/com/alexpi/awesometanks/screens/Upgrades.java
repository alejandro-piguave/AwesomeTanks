package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;

import com.alexpi.awesometanks.widget.GameButton;
import com.alexpi.awesometanks.widget.UpgradeTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Styles;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Alex on 29/01/2016.
 */
public class Upgrades extends BaseScreen {
    Stage stage;
    Table table, performance, buttons, currentWeaponTable;
    Label money;
    private UpgradeTable[] upgradables;
    private ImageButton[] weaponButtons;
    private UpgradeTable weaponPower,weaponAmmo;
    Skin uiSkin;
    Preferences gameValues;
    int currentWeapon, moneyValue;
    int[][] values;
    boolean[] availableWeapons;

    private static final int UPGRADE_COUNT = 4;
    private static final int WEAPON_COUNT = 7;

    public Upgrades(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        final boolean soundsOn = game.getGameSettings().getBoolean("areSoundsActivated");
        final Sound purchaseSound = game.getManager().get("sounds/purchase.ogg",Sound.class);
        gameValues = Gdx.app.getPreferences("values");

        weaponButtons = new ImageButton[WEAPON_COUNT];
        upgradables = new UpgradeTable[UPGRADE_COUNT];
        uiSkin = game.getManager().get("uiskin/uiskin.json");
        stage = new Stage(new FillViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        Image background = new Image(game.getManager().get("sprites/background.png", Texture.class));
        background.setBounds(0,0,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        table = new Table();
        table.setFillParent(true);
        performance = new Table();
        buttons = new Table();
        currentWeaponTable = new Table();

        values = new int[2][WEAPON_COUNT];
        for (int i = 0; i < WEAPON_COUNT;i++){
            values[0][i] = gameValues.getInteger("power" + i, 0);
            values[1][i] = gameValues.getInteger("ammo" + i, 20);}
        availableWeapons = new boolean[7];
        for (int i = 0; i < WEAPON_COUNT;i++)
            availableWeapons[i] = gameValues.getBoolean("weapon"+i,true);

        for(int i = 0;i< UPGRADE_COUNT;i++){
            String name = "";
            switch (i){
                case 0: name = "health";break;
                case 1: name = "speed";break;
                case 2: name = "rotation";break;
                case 3: name = "visibility";break;}
            upgradables[i] = new UpgradeTable(game.getManager(), name, gameValues.getInteger(name),5f,500);
            upgradables[i].changePrice(500+ upgradables[i].getValue()*500);
        }

        GameButton playButton = new GameButton(game.getManager(), new GameButton.OnClickListener() {
            @Override
            public void onClick() {
                for(UpgradeTable p: upgradables)
                    gameValues.putInteger(p.getName(),p.getValue());

                for(int i = 0; i< WEAPON_COUNT;i++){
                    gameValues.putInteger("power"+i,values[0][i]);
                    gameValues.putInteger("ammo"+i,values[1][i]);
                    gameValues.putBoolean("weapon"+i,availableWeapons[i]);
                }
                gameValues.putInteger("money",moneyValue);
                gameValues.flush();
                stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.levelScreen);}})));
    }
        },"Play", soundsOn);

        moneyValue = gameValues.getInteger("money",1500);
        money = new Label(moneyValue + " $", Styles.getLabelStyleBackground(game.getManager()));

        for(int i = 0; i < WEAPON_COUNT;i++){
            Texture up = game.getManager().get("icons/icon_" + i + ".png"),
                    disabled= game.getManager().get("icons/icon_disabled_"+i+".png");
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiSkin.get(Button.ButtonStyle.class));
            style.imageUp = new TextureRegionDrawable(new TextureRegion(up));
            style.imageDisabled = new TextureRegionDrawable(new TextureRegion(disabled));
            weaponButtons[i] = new ImageButton(style);
        }
        final ImageButton currentWeaponImage = new ImageButton(weaponButtons[0].getStyle());
        final Label currentWeaponName = new Label(Constants.WEAPON_NAMES[0], Styles.getLabelStyleSmall(game.getManager()));

        for(int i = 0; i < WEAPON_COUNT; i++){
            if( i > 0) weaponButtons[i].setDisabled(availableWeapons[i]);
            final int finalI = i;
            weaponButtons[i].addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentWeapon = finalI;

                    weaponPower.setValue(values[0][currentWeapon]);
                    weaponPower.changePrice(Constants.prices[0][currentWeapon] + (Constants.prices[0][currentWeapon] / 2) * weaponPower.getValue());

                    weaponAmmo.setValue(values[1][currentWeapon]);
                    weaponAmmo.changePrice(Constants.prices[1][currentWeapon]);

                    currentWeaponImage.setStyle(weaponButtons[finalI].getStyle());

                    if(weaponButtons[finalI].isDisabled()){
                        weaponAmmo.setVisible(false);
                        weaponPower.setVisible(false);
                        currentWeaponName.setText("$"+Constants.prices[2][currentWeapon]);
                    } else{
                        weaponAmmo.setVisible(true);
                        weaponPower.setVisible(true);
                        currentWeaponName.setText(Constants.WEAPON_NAMES[finalI]);
                    }

                    if(currentWeapon == 0){
                        weaponAmmo.setVisible(false);
                    } else {
                        currentWeaponImage.setDisabled(availableWeapons[currentWeapon]);
                    }


                }
            });
            buttons.add(weaponButtons[i]).size(Constants.TILE_SIZE,Constants.TILE_SIZE).pad(Constants.TILE_SIZE /5);
        }


        currentWeaponImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(weaponButtons[currentWeapon].isDisabled() && (moneyValue-Constants.prices[2][currentWeapon])> 0){
                    if(soundsOn)purchaseSound.play();
                    money.setText((moneyValue -= Constants.prices[2][currentWeapon]) + " $");
                    availableWeapons[currentWeapon] = false;
                    weaponButtons[currentWeapon].setDisabled(false);
                    currentWeaponImage.setDisabled(false);

                    weaponAmmo.setVisible(true);
                    weaponPower.setVisible(true);
                }}});

        weaponAmmo = new UpgradeTable(game.getManager(), "Ammo",values[0][0],100f,100);
        weaponAmmo.setVisible(false);
        weaponAmmo.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponAmmo.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    if(soundsOn)purchaseSound.play();
                    weaponAmmo.increaseValue(20);
                    values[1][currentWeapon] = weaponAmmo.getValue();
                    money.setText((moneyValue -= weaponAmmo.getPrice()) + " $");
                }}});

        weaponPower = new UpgradeTable(game.getManager(), "Power",values[1][0],5,200 + 100 * values[0][0]);
        weaponPower.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponPower.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    if(soundsOn)purchaseSound.play();
                    weaponPower.increaseValue(1);
                    values[0][currentWeapon] = weaponPower.getValue();
                    money.setText((moneyValue -= weaponPower.getPrice()) + " $");
                }}});

        for(final UpgradeTable p: upgradables){
            p.getBuyButton().addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (p.canBuy(moneyValue)) {
                        if(soundsOn)purchaseSound.play();
                        p.increaseValue(1);
                        money.setText((moneyValue-=p.getPrice()) + " $");
                        p.changePrice(p.getPrice()+500);
                        if(p.getValue() == p.getMaxValue())p.getBuyButton().setVisible(false);
                    }
                }
            });
            if(p.getValue() == p.getMaxValue())p.getBuyButton().setVisible(false);
        }


        performance.add(upgradables[0]).size(Constants.TILE_SIZE *2f, Constants.TILE_SIZE * 1.5f).pad(8);
        performance.add(upgradables[1]).size(Constants.TILE_SIZE *2f, Constants.TILE_SIZE * 1.5f).pad(8).row();
        performance.add(upgradables[2]).size(Constants.TILE_SIZE *2f, Constants.TILE_SIZE * 1.5f).pad(8);
        performance.add(upgradables[3]).size(Constants.TILE_SIZE *2f, Constants.TILE_SIZE * 1.5f).pad(8).row();


        Table currentWeaponUpgradeTable = new Table();
        Table currentWeaponInfoTable = new Table();
        currentWeaponInfoTable.add(currentWeaponImage).size(Constants.TILE_SIZE * 1.5f, Constants.TILE_SIZE * 1.5f).row();
        currentWeaponInfoTable.add(currentWeaponName).padTop(8).row();
        currentWeaponUpgradeTable.add(weaponPower).size(Constants.TILE_SIZE *2,Constants.TILE_SIZE * 1.5f).pad(8).row();
        currentWeaponUpgradeTable.add(weaponAmmo).size(Constants.TILE_SIZE *2,Constants.TILE_SIZE * 1.5f).pad(8).row();
        currentWeaponTable.add(currentWeaponInfoTable);
        currentWeaponTable.add(currentWeaponUpgradeTable);

        table.add(money).colspan(2).padTop(16).row();
        table.add(performance);
        table.add(currentWeaponTable).row();
        table.add(buttons).colspan(2).row();
        table.add(playButton).size(Constants.TILE_SIZE * 3, Constants.TILE_SIZE).colspan(2).padBottom(16);
        stage.addActor(background);
        stage.addActor(table);
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
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.mainScreen);}})));}
    }
}
