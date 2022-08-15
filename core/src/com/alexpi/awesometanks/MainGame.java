package com.alexpi.awesometanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.alexpi.awesometanks.screens.LevelScreen;
import com.alexpi.awesometanks.screens.MainScreen;
import com.alexpi.awesometanks.screens.Settings;
import com.alexpi.awesometanks.screens.Upgrades;

public class MainGame extends Game {

	private AssetManager manager;
    public MainScreen mainScreen;
    public LevelScreen levelScreen;
	public Upgrades upgrades;
    public Settings settings;

	@Override
	public void create() {
		manager = new AssetManager();

		manager.load("uiskin/uiskin.json", Skin.class);
        manager.load("uiskin/uiskin.atlas", TextureAtlas.class);
        manager.load("uiskin/default.fnt", BitmapFont.class);

        manager.load("sounds/gun_change.ogg",Sound.class);

        manager.load("touchBackground.png", Texture.class);
        manager.load("touchKnob.png", Texture.class);

		manager.load("sprites/tank_wheels.png", Texture.class);
		manager.load("sprites/tank_body.png", Texture.class);

		manager.load("sprites/wall.png",Texture.class);
		manager.load("sprites/sand.png", Texture.class);
		manager.load("sprites/spawner.png",Texture.class);
        manager.load("sprites/box.png",Texture.class);
        manager.load("sprites/mine.png",Texture.class);
        manager.load("sprites/bricks.png",Texture.class);
        manager.load("sprites/gate.png",Texture.class);
        manager.load("sprites/shade.png",Texture.class);
        manager.load("sprites/turret_base.png",Texture.class);

        manager.load("sprites/nugget.png",Texture.class);
        manager.load("sprites/freezing_ball.png",Texture.class);
        manager.load("sprites/health_pack.png",Texture.class);
        manager.load("sprites/freezed.png",Texture.class);


		manager.load("sprites/bullet.png",Texture.class);
		manager.load("sprites/laser.png",Texture.class);

		manager.load("sprites/health_bar.png",Texture.class);
        manager.load("sprites/ricochet_bullet.png",Texture.class);

        manager.load("particles/flame.party", ParticleEffect.class);
        manager.load("particles/ricochets.party",ParticleEffect.class);
        manager.load("particles/railgun.party",ParticleEffect.class);
        manager.load("particles/collision.party",ParticleEffect.class);
        manager.load("particles/explosion.party",ParticleEffect.class);
        manager.load("particles/big-explosion.party",ParticleEffect.class);

		manager.load("weapons/minigun.png", Texture.class);
		manager.load("weapons/shotgun.png",Texture.class);
		manager.load("weapons/ricochet.png",Texture.class);
		manager.load("weapons/flamethrower.png",Texture.class);
		manager.load("weapons/canon.png",Texture.class);
		manager.load("weapons/laser.png", Texture.class);
		manager.load("weapons/railgun.png", Texture.class);


		for(int i=0;i<7;i++)
			manager.load("icons/icon_"+i+".png",Texture.class);

		for(int i = 0; i <7; i++)
			manager.load("icons/icon_disabled_"+i+".png", Texture.class);

		manager.finishLoading();//TERMINA DE CARGAR

        mainScreen = new MainScreen(this);
        levelScreen = new LevelScreen(this);
        settings = new Settings(this);
		upgrades = new Upgrades(this);

		setScreen(mainScreen);//PONE LA PANTALLA PRINCIPAL
	}

	@Override
	public void dispose() {
		manager.clear();
	}

	public AssetManager getManager() {
		return manager;
	}
}