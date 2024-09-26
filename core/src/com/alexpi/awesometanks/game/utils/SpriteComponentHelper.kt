package com.alexpi.awesometanks.game.utils

import com.alexpi.awesometanks.game.components.BodyComponent
import com.alexpi.awesometanks.game.components.SpriteComponent
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite

fun getSpriteComponent(assetManager: AssetManager, texturePath: String, layer: SpriteComponent.Layer, bodyComponent: BodyComponent): SpriteComponent {
    val spriteComponent = SpriteComponent()
    spriteComponent.layer = layer
    spriteComponent.sprite = Sprite(assetManager.get<Texture>(texturePath))
    spriteComponent.sprite.setBounds(bodyComponent)
    spriteComponent.sprite.setOrigin(spriteComponent.sprite.width/2, spriteComponent.sprite.height/2)

    return spriteComponent
}