package com.alexpi.awesometanks;

import com.alexpi.awesometanks.screens.LevelScreen;
import com.alexpi.awesometanks.screens.MainScreen;
import com.alexpi.awesometanks.screens.UpgradesScreen;
import com.alexpi.awesometanks.world.Settings;
import com.alexpi.awesometanks.weapons.Weapon;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainGame extends Game {

    private AssetManager manager;
    public MainScreen mainScreen;
    public LevelScreen levelScreen;
    public UpgradesScreen upgradesScreen;
    private Preferences gameSettings;
    private Preferences gameValues;

    @Override
    public void create() {
        manager = new AssetManager();

        loadFonts();

        manager.load("uiskin/uiskin.json", Skin.class);
        manager.load("uiskin/uiskin.atlas", TextureAtlas.class);

        manager.load("sounds/explosion.ogg", Sound.class);
        manager.load("sounds/click_down.ogg", Sound.class);
        manager.load("sounds/click_up.ogg", Sound.class);
        manager.load("sounds/purchase.ogg", Sound.class);
        manager.load("sounds/gun_change.ogg", Sound.class);
        manager.load("sounds/minigun.ogg", Sound.class);
        manager.load("sounds/shotgun.ogg", Sound.class);
        manager.load("sounds/ricochet.ogg", Sound.class);
        manager.load("sounds/flamethrower.ogg", Sound.class);
        manager.load("sounds/rocket_launch.ogg", Sound.class);
        manager.load("sounds/canon.ogg", Sound.class);
        manager.load("sounds/laser.ogg", Sound.class);
        manager.load("sounds/railgun.ogg", Sound.class);

        manager.load("sprites/background.png", Texture.class);
        manager.load("sprites/button_background.9.png", Texture.class);
        manager.load("sprites/button_background_down.9.png", Texture.class);
        manager.load("sprites/label_background.9.png", Texture.class);
        manager.load("sprites/progress_bar_background.9.png", Texture.class);
        manager.load("sprites/progress_bar_foreground.9.png", Texture.class);


        manager.load("touchBackground.png", Texture.class);
        manager.load("touchKnob.png", Texture.class);

        manager.load("sprites/tank_wheels.png", Texture.class);
        manager.load("sprites/tank_body.png", Texture.class);

        manager.load("sprites/wall.png", Texture.class);
        manager.load("sprites/sand.png", Texture.class);
        manager.load("sprites/spawner.png", Texture.class);
        manager.load("sprites/box.png", Texture.class);
        manager.load("sprites/mine.png", Texture.class);
        manager.load("sprites/bricks.png", Texture.class);
        manager.load("sprites/gate.png", Texture.class);
        manager.load("sprites/shade.png", Texture.class);
        manager.load("sprites/turret_base.png", Texture.class);
        manager.load("sprites/explosion_shine.png", Texture.class);

        manager.load("sprites/nugget.png", Texture.class);
        manager.load("sprites/freezing_ball.png", Texture.class);
        manager.load("sprites/health_pack.png", Texture.class);
        manager.load("sprites/frozen.png", Texture.class);
        manager.load("sprites/gun_menu_icon.png", Texture.class);

        manager.load("sprites/sound_on.png", Texture.class);
        manager.load("sprites/sound_off.png", Texture.class);

        manager.load("sprites/bullet.png", Texture.class);
        manager.load("sprites/laser_ray.png", Texture.class);
        manager.load("sprites/railgun_ray.png", Texture.class);
        manager.load("sprites/railgun_laser.png", Texture.class);
        manager.load("sprites/rocket.png", Texture.class);
        manager.load("sprites/rocket_flame.png", Texture.class);

        manager.load("sprites/health_bar.png", Texture.class);
        manager.load("sprites/ricochet_bullet.png", Texture.class);

        manager.load("particles/flame.party", ParticleEffect.class);
        manager.load("particles/ricochets.party", ParticleEffect.class);
        manager.load("particles/railgun.party", ParticleEffect.class);
        manager.load("particles/collision.party", ParticleEffect.class);
        manager.load("particles/explosion.party", ParticleEffect.class);
        manager.load("particles/big-explosion.party", ParticleEffect.class);

        manager.load("weapons/minigun.png", Texture.class);
        manager.load("weapons/shotgun.png", Texture.class);
        manager.load("weapons/ricochet.png", Texture.class);
        manager.load("weapons/flamethrower.png", Texture.class);
        manager.load("weapons/canon.png", Texture.class);
        manager.load("weapons/rocket.png", Texture.class);
        manager.load("weapons/laser.png", Texture.class);
        manager.load("weapons/railgun.png", Texture.class);

        for (int i = 0; i < Weapon.Type.values().length; i++)
            manager.load("icons/icon_" + i + ".png", Texture.class);

        for (int i = 0; i < Weapon.Type.values().length; i++)
            manager.load("icons/icon_disabled_" + i + ".png", Texture.class);

        manager.finishLoading();

        gameSettings = Gdx.app.getPreferences("settings");
        gameValues = Gdx.app.getPreferences("values");

        boolean soundsOn = gameSettings.getBoolean("areSoundsActivated", true);
        Settings.INSTANCE.setSoundsOn(soundsOn);

        mainScreen = new MainScreen(this);
        levelScreen = new LevelScreen(this);
        upgradesScreen = new UpgradesScreen(this);

        setScreen(mainScreen);
    }

    private void loadFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter gameTitleParam = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        gameTitleParam.fontFileName = "fonts/font.ttf";
        gameTitleParam.fontParameters.size = 120;
        gameTitleParam.fontParameters.borderColor = new Color(.859f, .675f, .063f, 1f);
        gameTitleParam.fontParameters.borderWidth = 4f;
        gameTitleParam.fontParameters.color = new Color(.420f, .275f, 0f, 1f);
        gameTitleParam.fontParameters.shadowColor = Color.BLACK;
        gameTitleParam.fontParameters.shadowOffsetX = 8;
        gameTitleParam.fontParameters.shadowOffsetY = 8;
        manager.load("title_font1.ttf", BitmapFont.class, gameTitleParam);

        FreetypeFontLoader.FreeTypeFontLoaderParameter gameTitleParam2 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        gameTitleParam2.fontParameters.color = new Color(.580f, .133f, .008f, 1f);
        gameTitleParam2.fontFileName = "fonts/font.ttf";
        gameTitleParam2.fontParameters.size = 120;
        gameTitleParam2.fontParameters.borderColor = new Color(.859f, .675f, .063f, 1f);
        gameTitleParam2.fontParameters.borderWidth = 4f;
        gameTitleParam2.fontParameters.shadowColor = Color.BLACK;
        gameTitleParam2.fontParameters.shadowOffsetX = 8;
        gameTitleParam2.fontParameters.shadowOffsetY = 8;
        manager.load("title_font2.ttf", BitmapFont.class, gameTitleParam2);

        FreetypeFontLoader.FreeTypeFontLoaderParameter buttonFontParam1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        buttonFontParam1.fontParameters.color = new Color(.745f, .373f, 0f, 1f);
        buttonFontParam1.fontFileName = "fonts/font.ttf";
        buttonFontParam1.fontParameters.size = 32;
        manager.load("button_font1.ttf", BitmapFont.class, buttonFontParam1);


        FreetypeFontLoader.FreeTypeFontLoaderParameter labelFontParam1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        labelFontParam1.fontParameters.color = new Color(1f, .714f, 0.04f, 1f);
        labelFontParam1.fontFileName = "fonts/font.ttf";
        labelFontParam1.fontParameters.size = 64;
        manager.load("label_font1.ttf", BitmapFont.class, labelFontParam1);

        FreetypeFontLoader.FreeTypeFontLoaderParameter labelFontSmallParam1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        labelFontSmallParam1.fontParameters.color = new Color(1f, .714f, 0.04f, 1f);
        labelFontSmallParam1.fontFileName = "fonts/font.ttf";
        labelFontSmallParam1.fontParameters.size = 20;
        manager.load("label_font2.ttf", BitmapFont.class, labelFontSmallParam1);

        FreetypeFontLoader.FreeTypeFontLoaderParameter buttonFontSmallParam1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        buttonFontSmallParam1.fontParameters.color = new Color(.745f, .373f, 0f, 1f);
        buttonFontSmallParam1.fontFileName = "fonts/font.ttf";
        buttonFontSmallParam1.fontParameters.size = 20;
        manager.load("button_font2.ttf", BitmapFont.class, buttonFontSmallParam1);

    }

    @Override
    public void dispose() {
        manager.clear();
    }

    public AssetManager getManager() {
        return manager;
    }

    public Preferences getGameSettings() {
        return gameSettings;
    }

    public Preferences getGameValues() {
        return gameValues;
    }
}