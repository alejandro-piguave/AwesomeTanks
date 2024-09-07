package com.alexpi.awesometanks.world.collision;

import com.alexpi.awesometanks.entities.actors.DamageableActor;
import com.alexpi.awesometanks.entities.blocks.Mine;
import com.alexpi.awesometanks.entities.blocks.Spawner;
import com.alexpi.awesometanks.entities.items.FreezingBall;
import com.alexpi.awesometanks.entities.items.GoldNugget;
import com.alexpi.awesometanks.entities.items.HealthPack;
import com.alexpi.awesometanks.entities.items.Item;
import com.alexpi.awesometanks.entities.projectiles.CanonBall;
import com.alexpi.awesometanks.entities.projectiles.Flame;
import com.alexpi.awesometanks.entities.projectiles.Projectile;
import com.alexpi.awesometanks.entities.projectiles.Rail;
import com.alexpi.awesometanks.entities.projectiles.Rocket;
import com.alexpi.awesometanks.entities.tanks.Player;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ContactManager implements ContactListener {

    public interface ContactListener{
        void onGoldNuggetFound(GoldNugget goldNugget);
        void onHealthPackFound(HealthPack healthPack);
        void onFreezingBallFound(FreezingBall freezingBall);
        void onBulletCollision(float x, float y);
        void onLandMineFound(float x, float y);
        void onExplosiveProjectileCollided(float x, float y);
    }

    private final ContactListener contactListener;

    public ContactManager(ContactListener contactListener){
        this.contactListener = contactListener;
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
                damageableActor.burn(((Flame) projectile).getBurnDuration());

            if(!(projectile.isEnemy() && damageableActor instanceof Spawner)){
                damageableActor.takeDamage(projectile.getDamage());
            }
            if(projectile instanceof CanonBall || projectile instanceof Rocket || projectile instanceof Rail){
                contactListener.onExplosiveProjectileCollided(projectile.getX()+projectile.getBodyWidth()*.5f, projectile.getY()+projectile.getBodyHeight()*.5f);
            }
            projectile.collide();

            contactListener.onBulletCollision(projectile.getX()+projectile.getBodyWidth()*.5f, projectile.getY()+projectile.getBodyHeight()*.5f);

            if(!damageableActor.isAlive() && (damageableActor instanceof Mine)){
                Fixture mineFixture = fixtureA.getUserData() instanceof Mine? fixtureA:fixtureB;
                float mineX = mineFixture.getBody().getPosition().x, mineY = mineFixture.getBody().getPosition().y;
                contactListener.onLandMineFound(mineX, mineY);
            }
        }

        if((fixtureA.getUserData()instanceof Item && fixtureB.getUserData()instanceof Player)
                || (contact.getFixtureB().getUserData()instanceof Item && fixtureA.getUserData()instanceof Player)){

            Item item = fixtureA.getUserData()instanceof Item?
                    (Item)fixtureA.getUserData(): (Item)fixtureB.getUserData();

            if(item instanceof GoldNugget) {
                GoldNugget nugget = (GoldNugget)item;
                contactListener.onGoldNuggetFound(nugget);
            }
            else if(item instanceof HealthPack){
                HealthPack pack  = (HealthPack) item;
                contactListener.onHealthPackFound(pack);
            }
            else if(item instanceof FreezingBall){
                FreezingBall ball = (FreezingBall) item;
                contactListener.onFreezingBallFound(ball);
            }
            item.pickUp();
        }

    }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
