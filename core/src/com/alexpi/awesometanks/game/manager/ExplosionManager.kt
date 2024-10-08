package com.alexpi.awesometanks.game.manager

import com.alexpi.awesometanks.game.components.health.HealthOwner
import com.alexpi.awesometanks.game.module.Settings
import com.alexpi.awesometanks.game.particles.ParticleActor
import com.alexpi.awesometanks.game.utils.fastHypot
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.game.stage.GameContext
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image

class ExplosionManager(private val gameContext: GameContext): Actor() {
    private val explosionSound: Sound = gameContext.getAssetManager().get("sounds/explosion.ogg")
    private val explosionTexture = gameContext.getAssetManager().get("sprites/explosion_shine.png", Texture::class.java)

    fun createLandMineExplosion(x: Float, y: Float){
        createExplosion(x,y ,2.5f, 350f,1f, 40f, .65f,false)
    }

    fun createCanonBallExplosion(x: Float, y: Float){
        createExplosion(x,y ,.25f, 35f,.05f, 15f, .45f, true)
    }

    private fun createExplosion(x: Float, y: Float, explosionRadius: Float, maxDamage: Float, volume: Float, rumblePower: Float, rumbleLength: Float, bigExplosion: Boolean){
        val explosionSize = TILE_SIZE * explosionRadius * 2
        val explosionX = TILE_SIZE * x
        val explosionY = TILE_SIZE * y
        stage.addActor(
            ParticleActor(
                gameContext,
                if (bigExplosion) "particles/big-explosion.party" else "particles/big-explosion.party",
                explosionX,
                explosionY,
                false
            )
        )
        val explosionShine = Image(explosionTexture)
        explosionShine.setBounds(
            explosionX - explosionSize * .5f,
            explosionY - explosionSize * .5f,
            explosionSize,
            explosionSize
        )
        explosionShine.setOrigin(explosionSize * .5f, explosionSize * .5f)
        explosionShine.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(.01f, .01f, .75f),
                    Actions.alpha(0f, .75f)
                ),
                Actions.run { explosionShine.remove() }
            )
        )
        stage.addActor(explosionShine)

        gameContext.getWorld().QueryAABB({
            val distanceFromMine = fastHypot(
                (it.body.position.x - x).toDouble(),
                (it.body.position.y - y).toDouble()
            ).toFloat()
            if (it.userData is HealthOwner && (distanceFromMine < explosionRadius)) {
                val damageableActor = (it.userData as HealthOwner)
                damageableActor.healthComponent.takeDamage(maxDamage * (explosionRadius - distanceFromMine) / explosionRadius)
            }
            true
        },x-explosionRadius,y-explosionRadius,x+explosionRadius,y+explosionRadius)

        if (Settings.soundsOn) explosionSound.play(volume)
        gameContext.getRumbleController().rumble(rumblePower, rumbleLength)
    }
}