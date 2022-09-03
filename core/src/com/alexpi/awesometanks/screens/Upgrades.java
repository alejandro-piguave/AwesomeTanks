package com.alexpi.awesometanks.screens;

import static com.alexpi.awesometanks.utils.Constants.TRANSITION_DURATION;
import static com.alexpi.awesometanks.utils.Constants.upgradePrices;

import com.alexpi.awesometanks.utils.Settings;
import com.alexpi.awesometanks.widget.GameButton;
import com.alexpi.awesometanks.widget.UpgradeTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Alex on 29/01/2016.
 */
public class Upgrades extends BaseScreen {
    private Stage stage;
    private SpriteBatch batch;
    private Label money;
    private UpgradeTable[] upgradables;
    private ImageButton[] weaponButtons;
    private UpgradeTable weaponPower,weaponAmmo;
    private Texture background;
    private int currentWeapon, moneyValue;
    private int[] weaponPowerValues;
    private float[] weaponAmmoValues;
    private boolean[] availableWeapons;

    private static final int UPGRADE_COUNT = 4;

    public Upgrades(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        final Sound purchaseSound = game.getManager().get("sounds/purchase.ogg",Sound.class);
        weaponButtons = new ImageButton[Constants.WEAPON_COUNT];
        upgradables = new UpgradeTable[UPGRADE_COUNT];
        Skin uiSkin = game.getManager().get("uiskin/uiskin.json");
        stage = new Stage(new ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        batch = new SpriteBatch();
        background = game.getManager().get("sprites/background.png");
        Table table = new Table();
        table.setFillParent(true);
        Table performance = new Table();
        Table buttons = new Table();
        Table currentWeaponTable = new Table();

        weaponPowerValues = new int[Constants.WEAPON_COUNT];
        weaponAmmoValues = new float[Constants.WEAPON_COUNT];
        for (int i = 0; i < Constants.WEAPON_COUNT;i++){
            //POWER
            weaponPowerValues[i] = game.getGameValues().getInteger("power" + i, 0);
            //AMMO
            weaponAmmoValues[i] = game.getGameValues().getFloat("ammo" + i, 100f);}
        availableWeapons = new boolean[Constants.WEAPON_COUNT];
        for (int i = 0; i <Constants. WEAPON_COUNT;i++)
            availableWeapons[i] = game.getGameValues().getBoolean("weapon"+i,true);

        for(int i = 0;i< UPGRADE_COUNT;i++){
            String name = "";
            switch (i){
                case 0: name = Constants.ARMOR;break;
                case 1: name = Constants.MOVEMENT_SPEED;break;
                case 2: name = Constants.ROTATION_SPEED;break;
                case 3: name = Constants.VISIBILITY;break;
            }
            int value = game.getGameValues().getInteger(name);
            upgradables[i] = new UpgradeTable(game.getManager(), name, value,5f,value ==5? 1000: upgradePrices[i][value]);
            final int finalI = i;
            upgradables[i].getBuyButton().addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (upgradables[finalI].canBuy(moneyValue)) {
                        if(Settings.INSTANCE.getSoundsOn())purchaseSound.play();
                        upgradables[finalI].increaseValue(1);
                        int value = upgradables[finalI].getValue();
                        money.setText((moneyValue-=upgradables[finalI].getPrice()) + " $");

                        if(upgradables[finalI].isMaxValue())
                            upgradables[finalI].getBuyButton().setVisible(false);
                        else upgradables[finalI].changePrice(upgradePrices[finalI][value]);
                    }
                }
            });
            if(upgradables[i].isMaxValue())
                upgradables[i].getBuyButton().setVisible(false);
        }

        GameButton playButton = new GameButton(game.getManager(), new GameButton.OnClickListener() {
            @Override
            public void onClick() {
                for(UpgradeTable p: upgradables)
                    game.getGameValues().putInteger(p.getName(),p.getValue());

                for(int i = 0; i< Constants.WEAPON_COUNT;i++){
                    game.getGameValues().putInteger("power"+i, weaponPowerValues[i]);
                    game.getGameValues().putFloat("ammo"+i, weaponAmmoValues[i]);
                    game.getGameValues().putBoolean("weapon"+i,availableWeapons[i]);
                }
                game.getGameValues().putInteger("money",moneyValue);
                game.getGameValues().flush();
                stage.addAction(Actions.sequence(Actions.fadeOut(TRANSITION_DURATION), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.levelScreen);}})));
    }
        },"Play");

        moneyValue = game.getGameValues().getInteger("money",0);
        money = new Label(moneyValue + " $", Styles.getLabelStyleBackground(game.getManager()));

        for(int i = 0; i < Constants.WEAPON_COUNT;i++){
            Texture up = game.getManager().get("icons/icon_" + i + ".png"),
                    disabled= game.getManager().get("icons/icon_disabled_"+i+".png");
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiSkin.get(Button.ButtonStyle.class));
            style.imageUp = new TextureRegionDrawable(new TextureRegion(up));
            style.imageDisabled = new TextureRegionDrawable(new TextureRegion(disabled));
            weaponButtons[i] = new ImageButton(style);
        }
        final ImageButton currentWeaponImage = new ImageButton(weaponButtons[0].getStyle());
        final Label currentWeaponName = new Label(Constants.WEAPON_NAMES[0], Styles.getLabelStyleSmall(game.getManager()));

        for(int i = 0; i < Constants.WEAPON_COUNT; i++){
            if( i > 0) weaponButtons[i].setDisabled(availableWeapons[i]);
            final int finalI = i;
            weaponButtons[i].addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    currentWeapon = finalI;

                    weaponPower.setValue(weaponPowerValues[currentWeapon]);
                    if(weaponPower.isMaxValue()){
                        weaponPower.getBuyButton().setVisible(false);
                    } else {
                        weaponPower.changePrice(Constants.gunUpgradePrices[currentWeapon][weaponPowerValues[currentWeapon]]);
                        weaponPower.getBuyButton().setVisible(true);
                    }

                    weaponAmmo.setValue(weaponAmmoValues[currentWeapon]);
                    weaponAmmo.changePrice(Constants.prices[0][currentWeapon]);

                    currentWeaponImage.setStyle(weaponButtons[finalI].getStyle());

                    if(weaponButtons[finalI].isDisabled()){
                        weaponAmmo.setVisible(false);
                        weaponPower.setVisible(false);
                        currentWeaponName.setText("$"+Constants.prices[1][currentWeapon]);
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
                if(weaponButtons[currentWeapon].isDisabled() && (moneyValue-Constants.prices[1][currentWeapon])> 0){
                    if(Settings.INSTANCE.getSoundsOn())purchaseSound.play();
                    money.setText((moneyValue -= Constants.prices[1][currentWeapon]) + " $");
                    availableWeapons[currentWeapon] = false;
                    weaponButtons[currentWeapon].setDisabled(false);
                    currentWeaponImage.setDisabled(false);

                    weaponAmmo.setVisible(true);
                    weaponPower.setVisible(true);
                }}});


        weaponPower = new UpgradeTable(game.getManager(), "Power", weaponPowerValues[0],5,weaponPowerValues[0] >=5? 5 : Constants.gunUpgradePrices[0][weaponPowerValues[0]]);
        if(weaponPower.isMaxValue())
            weaponPower.getBuyButton().setVisible(false);
        weaponPower.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponPower.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    if(Settings.INSTANCE.getSoundsOn())purchaseSound.play();
                    weaponPower.increaseValue(1);
                    weaponPowerValues[currentWeapon] = weaponPower.getValue();
                    money.setText((moneyValue -= weaponPower.getPrice()) + " $");
                    if(weaponPower.isMaxValue()){
                        weaponPower.getBuyButton().setVisible(false);
                    } else {
                        weaponPower.changePrice(Constants.gunUpgradePrices[currentWeapon][weaponPowerValues[currentWeapon]]);
                    }

                }}});

        weaponAmmo = new UpgradeTable(game.getManager(), "Ammo", weaponAmmoValues[0],100f,100);
        weaponAmmo.setVisible(false);
        weaponAmmo.getBuyButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponAmmo.canBuy(moneyValue) && !currentWeaponImage.isDisabled()) {
                    if(Settings.INSTANCE.getSoundsOn())purchaseSound.play();
                    weaponAmmo.increaseValue(20);
                    weaponAmmoValues[currentWeapon] = weaponAmmo.getValue();
                    money.setText((moneyValue -= weaponAmmo.getPrice()) + " $");
                }}});

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
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(TRANSITION_DURATION)));
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
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {@Override public void run() {game.setScreen(game.mainScreen);}})));}
    }
}
