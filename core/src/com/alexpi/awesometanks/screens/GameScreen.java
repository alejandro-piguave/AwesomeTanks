package com.alexpi.awesometanks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.alexpi.awesometanks.MainGame;
import com.alexpi.awesometanks.ParticleActor;
import com.alexpi.awesometanks.entities.DamageableActor;
import com.alexpi.awesometanks.entities.Enemy;
import com.alexpi.awesometanks.entities.Shade;
import com.alexpi.awesometanks.entities.Tank;
import com.alexpi.awesometanks.entities.blocks.Block;
import com.alexpi.awesometanks.entities.blocks.Box;
import com.alexpi.awesometanks.entities.blocks.Bricks;
import com.alexpi.awesometanks.entities.blocks.Gate;
import com.alexpi.awesometanks.entities.blocks.Mine;
import com.alexpi.awesometanks.entities.blocks.Spawner;
import com.alexpi.awesometanks.entities.blocks.Turret;
import com.alexpi.awesometanks.entities.blocks.Wall;
import com.alexpi.awesometanks.entities.items.FreezingBall;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.entities.items.HealthPack;
import com.alexpi.awesometanks.entities.items.Item;
import com.alexpi.awesometanks.entities.projectiles.Flame;
import com.alexpi.awesometanks.entities.projectiles.Projectile;
import com.alexpi.awesometanks.utils.Constants;
import com.alexpi.awesometanks.utils.Map;
import com.alexpi.awesometanks.utils.Styles;

import java.util.Vector;


/**
 * Created by Alex on 30/12/2015.
 */
public class GameScreen extends BaseScreen implements InputProcessor, ContactListener{
    private Vector<ImageButton>buttons;
    private Vector<Shade> shades;
    private Stage gameStage, UIStage, shadeStage;
    private World world;
    private Skin joystickSkin, uiSkin;
    private Touchpad joystick;
    private Tank tank;
    private Table weaponsTable;
    private Label gunName, money, ammoAmount;
    private Preferences gameSettings, gameValues;
    private int screenPointer, level;
    private boolean soundFX, isPaused, hasFinished, alreadyExecuted;
    private Map map;
    private Sound gunChange;

    public GameScreen(MainGame game, int level) {
        super(game);this.level = level;
    }

    @Override
    public void show() {
        buttons = new Vector<ImageButton>();
        Vector<Block> blocks = new Vector<Block>();
        shades = new Vector<Shade>();
        joystickSkin = new Skin();
        UIStage = new Stage();
        gameStage = new Stage();
        shadeStage = new Stage();
        world = new World(new Vector2(0,0),true);
        gunChange = game.getManager().get("sounds/gun_change.ogg");

        gameSettings = Gdx.app.getPreferences("settings");
        gameValues = Gdx.app.getPreferences("values");
        soundFX = gameSettings.getBoolean("areSoundsActivated");

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(UIStage);
        multiplexer.addProcessor(this);

        world.setContactListener(this);

        map = new Map();
        map.getLevel(level);

        for(int y = 0;y <map.getRows();y++)
            for(int x = 0;x <map.getColumns() ;x++)
                if(map.getMap()[y][x] == Constants.start)
                    tank = new Tank(game.getManager(),world,new Vector2(x,map.getRows()-y),gameValues,
                            Constants.colors[gameSettings.getInteger("tankColor")],gameValues.getInteger("money",0),soundFX);
        for(int y = 0;y <map.getRows();y++){
            for(int x = 0;x <map.getColumns() ;x++) {
                for(int i = 0; i <2; i++)
                    for (int j = 0; j <2;j++)
                        shades.add(new Shade(game.getManager(), x + i * .5f, (map.getRows() - y) + j * .5f));

                if(map.getMap()[y][x] == Constants.wall)
                    blocks.add(new Wall(game.getManager(), world, x, map.getRows() - y));
                else{
                    if( map.getMap()[y][x] == Constants.gate)
                        blocks.add(new Gate(game.getManager(), world, x, map.getRows() - y));
                    else if(map.getMap()[y][x] == Constants.bricks)
                        blocks.add(new Bricks(game.getManager(), world, x, map.getRows() - y));
                    else if(map.getMap()[y][x] == Constants.box)
                        blocks.add(new Box(game.getManager(), world,tank.getBody().getPosition(), x, map.getRows() - y));
                    else if(map.getMap()[y][x] == Constants.spawner)
                        blocks.add(new Spawner(game.getManager(), world,tank.getBody().getPosition(), x, map.getRows() - y));
                    else if(map.getMap()[y][x] == Constants.bomb)
                        blocks.add(new Mine(game.getManager(), world, x, map.getRows() - y));
                    else if(Character.isDigit(map.getMap()[y][x])){
                        int num = Character.getNumericValue(map.getMap()[y][x]);
                        blocks.add(new Turret(game.getManager(), world, tank.getBody().getPosition(), x, map.getRows() - y, num));
                    }

                    Image space = new Image(game.getManager().get("sprites/sand.png",Texture.class));
                    space.setBounds(x * Constants.tileSize, (map.getRows()-y) * Constants.tileSize, Constants.tileSize, Constants.tileSize);
                    gameStage.addActor(space);
                }
            }
        }
        for(Shade s: shades)
            shadeStage.addActor(s);

        gameStage.addActor(tank);

        for(Block block: blocks)
            gameStage.addActor(block);


        uiSkin = game.getManager().get("uiskin/uiskin.json");

        gunName = new Label("Minigun", Styles.getLabelStyle((int) (Constants.tileSize / 4)));
        gunName.setPosition(UIStage.getWidth() / 2 - gunName.getWidth() / 2, 10);gunName.setAlignment(Align.center);

        ammoAmount = new Label(tank.getCurrentWeapon().getAmmo()+"/100",Styles.getLabelStyle((int) (Constants.tileSize/2)));
        float ammoAlignment = gameSettings.getBoolean("isAlignedToLeft")?10f:Constants.screenWidth-ammoAmount.getWidth()-10f;
        ammoAmount.setPosition(ammoAlignment,Constants.screenHeight-ammoAmount.getHeight());ammoAmount.setAlignment(Align.center);
        ammoAmount.setVisible(false);

        Timer.schedule(new Timer.Task() {@Override public void run() {gunName.addAction(Actions.fadeOut(1f));}}, 2f);


        money = new Label(tank.money+" $",Styles.getLabelStyle((int) (Constants.tileSize/3)));
        money.setPosition(Constants.centerX, Constants.screenHeight - money.getHeight(),Align.center);

        for(int i = 0; i < 7;i++){
            Texture texture = game.getManager().get("icons/icon_"+i+".png");
            Texture disabled= game.getManager().get("icons/icon_disabled_"+i+".png");
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiSkin.get(Button.ButtonStyle.class));
            style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
            style.imageDisabled = new TextureRegionDrawable(new TextureRegion(disabled));
            buttons.add(new ImageButton(style));
        }

        float buttonsAlignment,joystickAlignment;
        joystickAlignment = gameSettings.getBoolean("isAlignedToLeft")?10: Constants.screenWidth- Constants.screenHeight/2.5f-10;
        joystick = new Touchpad (0,getTouchPadStyle());
        joystick.setColor(joystick.getColor().r, joystick.getColor().g, joystick.getColor().b, 0.5f);
        joystick.setBounds(joystickAlignment, 10, Constants.screenHeight / 2.5f, Constants.screenHeight / 2.5f);

        buttonsAlignment = gameSettings.getBoolean("isAlignedToLeft")? Constants.screenWidth- Constants.tileSize:0;
        weaponsTable = new Table();
        weaponsTable.top();
        weaponsTable.setBounds(buttonsAlignment, 0, Constants.tileSize, Constants.screenHeight);
        for(ImageButton i: buttons)
            weaponsTable.add(i).width(Constants.screenHeight/7).height(Constants.screenHeight / 7).row();


        for (final Button ib: buttons) {
            if(buttons.indexOf(ib) > 0)ib.setDisabled(gameValues.getBoolean("weapon"+buttons.indexOf(ib),true));
            if(!ib.isDisabled())ib.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(soundFX)gunChange.play();
                    tank.setCurrentWeapon(buttons.indexOf(ib));
                    for (Button ib: buttons)
                        ib.setColor(ib.getColor().r,ib.getColor().g,ib.getColor().b,.5f);
                    ib.setColor(ib.getColor().r, ib.getColor().g, ib.getColor().b, 1f);

                    ammoAmount.setText(tank.getCurrentWeapon().getAmmo()+"/100");
                    if(buttons.indexOf(ib)==0)ammoAmount.setVisible(false);else ammoAmount.setVisible(true);

                    gunName.setText(tank.getCurrentWeapon().getName());
                    gunName.addAction(Actions.alpha(1f));
                    Timer.schedule(new Timer.Task() {@Override public void run() {
                        gunName.addAction(Actions.fadeOut(1f));}}, 2f);
                }
            });
            if(!buttons.firstElement().equals(ib))
                ib.setColor(ib.getColor().r,ib.getColor().g,ib.getColor().b,.5f);

        }
        UIStage.addActor(joystick);
        UIStage.addActor(weaponsTable);
        UIStage.addActor(gunName);
        UIStage.addActor(money);
        UIStage.addActor(ammoAmount);

        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(joystick.isTouched()&& (Math.abs(joystick.getKnobPercentX())>.2f || Math.abs(joystick.getKnobPercentY())>.2f)){
            tank.setDirection(joystick.getKnobPercentX(),joystick.getKnobPercentY());
            tank.isMoving = true;

            for(Shade shade: shades){
                float distanceFromTank = (float) Math.hypot(shade.getPosX()-tank.getPosX(),shade.getPosY()-tank.getPosY());
                if(distanceFromTank < tank.visibilityRadius)
                    shade.fadeOut();
            }

        }else tank.isMoving = false;

        if(!isPaused){
            world.step(1 / 40f, 6, 2);
            gameStage.act(delta);
            shadeStage.act(delta);
            ammoAmount.setText(tank.getCurrentWeapon().getAmmo() + "/100");

            hasFinished = true;
            for(Actor actor: gameStage.getActors())
                if(actor instanceof Turret || actor instanceof Enemy || actor instanceof Spawner){hasFinished = false; break;}

            gameStage.getCamera().position.set(tank.getCenterX(), tank.getCenterY(), 0);
            shadeStage.getCamera().position.set(gameStage.getCamera().position);
        }
        if(!tank.isAlive() && !alreadyExecuted){
            isPaused = alreadyExecuted = true;
            showLevelFailedDialog();}
        else if( hasFinished && !alreadyExecuted){
            isPaused = alreadyExecuted = true;
            showLevelCompletedDialog();}
        gameStage.draw();
        shadeStage.draw();

        UIStage.act(delta);
        UIStage.draw();
    }


    private void saveProgress(){
        if(hasFinished)gameValues.putBoolean("unlocked"+(++level),true);
        tank.saveProgress(gameValues);
        gameValues.putInteger("money", tank.money);
        gameValues.flush();
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tank.getCurrentWeapon().setDesiredAngleRotation(
                screenX - Constants.centerX,(Constants.screenHeight-screenY) - Constants.centerY);
        screenPointer = pointer;
        return true;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(pointer == screenPointer){
            tank.getCurrentWeapon().setDesiredAngleRotation(
                    screenX - Constants.centerX,(Constants.screenHeight-screenY) - Constants.centerY);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer == screenPointer) {
            tank.getCurrentWeapon().setDesiredAngleRotation(
                    screenX - Constants.centerX,(Constants.screenHeight-screenY) - Constants.centerY);
            tank.hasToShoot = true;
            return true;
        }
        return false;
    }
    private void showLevelFailedDialog(){
        Dialog levelFailed = new Dialog("Level failed", Styles.getWindowStyle((int) (Constants.tileSize / 3)));
        TextButton back = new TextButton("Back", Styles.getTextButtonStyle((int) (Constants.tileSize / 4)));
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;
                saveProgress();
                UIStage.addAction(Actions.fadeOut(.5f));
                gameStage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(game.upgrades);
                    }
                })));
            }
        });
        levelFailed.button(back);
        levelFailed.show(UIStage);
    }
    private void showLevelCompletedDialog(){
        Dialog levelCompleted = new Dialog("Level completed", Styles.getWindowStyle((int) (Constants.tileSize / 3)));
        TextButton continueButton = new TextButton("Continue", Styles.getTextButtonStyle((int) (Constants.tileSize / 4)));
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;saveProgress();
                UIStage.addAction(Actions.fadeOut(.5f));
                gameStage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(game.upgrades);
                    }
                })));}});
        levelCompleted.button(continueButton);
        levelCompleted.show(UIStage);


        saveProgress();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();

        if((fixtureA.getUserData() instanceof Projectile && (fixtureB.getUserData() instanceof DamageableActor))
                || ((fixtureB.getUserData() instanceof Projectile) && (fixtureA.getUserData() instanceof DamageableActor))){

            Projectile projectile = fixtureA.getUserData()instanceof Projectile?
                    (Projectile)fixtureA.getUserData(): (Projectile)fixtureB.getUserData();

            DamageableActor damageableActor =(DamageableActor) (fixtureA.getUserData()instanceof DamageableActor?
                    fixtureA.getUserData(): fixtureB.getUserData());

            if(projectile instanceof Flame)
                damageableActor.burn(((Flame) projectile).burnDuration);

            projectile.kill();

            if( !(projectile.isEnemy() && damageableActor instanceof Spawner)   )
                damageableActor.getHit(projectile.damage);

            gameStage.addActor(new ParticleActor(game.getManager(),"particles/collision.party",
                    damageableActor.getX()+damageableActor.getWidth()/2, damageableActor.getY()+damageableActor.getHeight()/2,false));

           if(!damageableActor.isAlive() && (damageableActor instanceof  Mine)){
                Fixture mineFixture = fixtureA.getUserData() instanceof Mine? fixtureA:fixtureB;
                float mineX = mineFixture.getBody().getPosition().x, mineY = mineFixture.getBody().getPosition().y;
               gameStage.addActor(new ParticleActor(game.getManager(),"particles/big-explosion.party",mineX*Constants.tileSize,mineY*Constants.tileSize,false));
                Array<Body> bodies = new Array<Body>();
                world.getBodies(bodies);
                for(Body body: bodies){
                    float distanceFromMine = (float) Math.hypot(body.getPosition().x - mineX,body.getPosition().y - mineY);
                    if(body.getUserData() instanceof DamageableActor &&( distanceFromMine <  5f))
                        ((DamageableActor) body.getUserData()).getHit(350 * ((5f - distanceFromMine) / 5f));
                }
            }
        }

        if((fixtureA.getUserData()instanceof Item && fixtureB.getUserData()instanceof Tank)
                || (contact.getFixtureB().getUserData()instanceof Item && fixtureA.getUserData()instanceof Tank)){

            Item item = fixtureA.getUserData()instanceof Item?
                    (Item)fixtureA.getUserData(): (Item)fixtureB.getUserData();

            if(item instanceof GoldNugget) {GoldNugget nugget = (GoldNugget)item;money.setText((tank.money += nugget.value) + " $");}
            else if(item instanceof HealthPack)
                tank.heal(((HealthPack)item).health);
            else if(item instanceof FreezingBall){
                for(Actor a: gameStage.getActors())
                    if(a instanceof Enemy)
                        ((Enemy)a).freeze(5);
            }
            item.kill();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            isPaused = true;
            Dialog pauseMenu = new Dialog("Pause Menu", Styles.getWindowStyle((int) (Constants.tileSize / 3)));
            TextButton back = new TextButton("Back", Styles.getTextButtonStyle((int) (Constants.tileSize / 4)));
            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isPaused = false;
                    UIStage.addAction(Actions.fadeOut(.5f));
                    gameStage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(game.levelScreen);
                        }
                    })));
                }
            });
            TextButton resume = new TextButton("Resume", Styles.getTextButtonStyle((int) (Constants.tileSize / 4)));
            resume.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isPaused = false;
                }
            });
            pauseMenu.button(back);
            pauseMenu.button(resume);
            pauseMenu.show(UIStage);
            return true;
        }
        else if(keycode == Input.Keys.SPACE){
            saveProgress();
            gameStage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(game.levelScreen);
                }
            })));
            return true;
        }
        return false;
    }

    @Override
    public void hide() {
        UIStage.dispose();
        gameStage.dispose();
        shadeStage.dispose();
        world.dispose();

        Gdx.input.setInputProcessor(null);
    }

    private Touchpad.TouchpadStyle getTouchPadStyle(){
        joystickSkin.add("touchBackground", game.getManager().get("touchBackground.png"));
        joystickSkin.add("touchKnob", game.getManager().get("touchKnob.png"));

        Touchpad.TouchpadStyle joystickStyle = new Touchpad.TouchpadStyle();
        joystickStyle.background = joystickSkin.getDrawable("touchBackground");
        joystickStyle.knob = joystickSkin.getDrawable("touchKnob");

        return joystickStyle;
    }

    @Override
    public boolean keyUp(int keycode) {return false;}

    @Override
    public boolean keyTyped(char character) {return false;}

    @Override
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(int amount) {return false;}

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}