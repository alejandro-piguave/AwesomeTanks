package com.alexpi.awesometanks.screens.upgrades

import com.alexpi.awesometanks.MainGame
import com.alexpi.awesometanks.game.module.Settings.soundsOn
import com.alexpi.awesometanks.screens.BaseScreen
import com.alexpi.awesometanks.screens.LevelScreen
import com.alexpi.awesometanks.screens.MainScreen
import com.alexpi.awesometanks.screens.SCREEN_HEIGHT
import com.alexpi.awesometanks.screens.SCREEN_WIDTH
import com.alexpi.awesometanks.screens.TILE_SIZE
import com.alexpi.awesometanks.screens.TRANSITION_DURATION
import com.alexpi.awesometanks.screens.widget.GameButton
import com.alexpi.awesometanks.screens.widget.Styles
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.onClick

/**
 * Created by Alex on 29/01/2016.
 */
class UpgradesScreen(game: MainGame) : BaseScreen(game) {
    private val stage: Stage = Stage(ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT))
    private val background: Texture = game.manager.get("sprites/background.png")
    private var currentWeapon = 0
    override fun show() {
        val purchaseSound = game.manager.get("sounds/purchase.ogg", Sound::class.java)
        val table = Table()
        table.setFillParent(true)
        val performance = Table()
        val buttons = Table()
        val currentWeaponTable = Table()

        //Money label
        val moneyValue = MoneyValue(0)
        val moneyLabel = MoneyLabel(game.manager, moneyValue)
        moneyValue.money = game.gameRepository.getMoney()

        //Retrieving the current ammo, power and availability of each weapon
        val weaponValues = WeaponUpgrade.values().map { game.gameRepository.getWeaponValues(it) }

        //Creates the sections for upgrading the tanks armor, rotation speed, movement speed and visibility
        val upgradeTables = PerformanceUpgrade.values().map { upgradeType ->
            val value = game.gameRepository.getUpgradeLevel(upgradeType)
            val upgradeTable =
                IntUpgradeTable(
                    game.manager,
                    upgradeType.name,
                    value,
                    upgradeType.prices.size,
                    if (value == 5) 1000 else upgradeType.prices[value]
                )
            upgradeTable.onBuyClick = {
                if (upgradeTable.canBuy(moneyValue.money)) {
                    if (soundsOn) purchaseSound.play()
                    upgradeTable.upgradeLevel += 1
                    moneyValue.money -= upgradeTable.upgradePrice
                    if (!upgradeTable.isMaxLevel) upgradeTable.upgradePrice = upgradeType.prices[upgradeTable.upgradeLevel]
                }
            }
            upgradeTable
        }
        //Creates the back button
        val backButton = GameButton(game.manager, "Back") {
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run {
                game.screen = MainScreen(game)
            }))
        }


        //Creates the next button
        val nextButton = GameButton(game.manager, "Next") {
            PerformanceUpgrade.values().forEachIndexed{ index, upgradeType ->
                game.gameRepository.saveUpgradeLevel(upgradeType, upgradeTables[index].upgradeLevel)
            }
            WeaponUpgrade.values().forEachIndexed { index, weaponType ->
                game.gameRepository.saveWeaponValues(weaponType, weaponValues[index])
            }
            game.gameRepository.saveMoney(moneyValue.money)
            stage.addAction(
                Actions.sequence(
                    Actions.fadeOut(TRANSITION_DURATION), Actions.run {
                        game.screen = LevelScreen(game)
                    }
                )
            )
        }


        val currentWeaponImage = ImageButton(getWeaponButtonStyle(WeaponUpgrade.values()[currentWeapon]))
        val currentWeaponName = Label(WeaponUpgrade.values()[currentWeapon].name, Styles.getLabelStyleSmall(game.manager))

        //Creates the button for upgrading the selected weapon power
        val weaponPower = IntUpgradeTable(
            game.manager, "Power", weaponValues[0].power,  WeaponUpgrade.values()[currentWeapon].upgradePrices.size,
            if (weaponValues[currentWeapon].power >= 5) 5 else WeaponUpgrade.values()[currentWeapon].upgradePrices[weaponValues[0].power]
        ).apply {
            onBuyClick =  {
                if (canBuy(moneyValue.money) && !currentWeaponImage.isDisabled) {
                    if (soundsOn) purchaseSound.play()
                    moneyValue.money -= upgradePrice
                    weaponValues[currentWeapon].power = upgradeLevel
                    upgradeLevel += 1
                    if (!isMaxLevel) upgradePrice = (WeaponUpgrade.values()[currentWeapon].upgradePrices[weaponValues[currentWeapon].power])
                }
            }
        }

        //Creates the button for buying more ammo
        val weaponAmmo = UpgradeTable(
            game.manager,
            "Ammo",
            weaponValues[0].ammo,
            100f,
            100
        ).apply {
            isVisible = false
            onBuyClick = {
                if (canBuy(moneyValue.money) && !currentWeaponImage.isDisabled) {
                    if (soundsOn) purchaseSound.play()
                    upgradeLevel += 20
                    weaponValues[currentWeapon].ammo = upgradeLevel.toFloat()
                    moneyValue.money -= upgradePrice
                }
            }
        }

        //Creates the row of weapon buttons at the bottom of the screen
        val weaponButtons =  WeaponUpgrade.values().map { weapon ->
            val weaponButton = ImageButton(getWeaponButtonStyle(weapon)).apply {
                //Sets the availability of all the weapon buttons except for the minigun which is always available
                if(weapon.ordinal > 0 ) isDisabled = !weaponValues[weapon.ordinal].isAvailable
                onClick {
                    currentWeapon = weapon.ordinal
                    weaponPower.upgradeLevel = weaponValues[currentWeapon].power
                    if (!weaponPower.isMaxLevel) {
                        weaponPower.upgradePrice = weapon.upgradePrices[weaponValues[currentWeapon].power]
                    }
                    weaponAmmo.upgradeLevel = weaponValues[currentWeapon].ammo
                    weaponAmmo.upgradePrice = weapon.ammoPrice
                    currentWeaponImage.style = style
                    if (isDisabled) {
                        weaponAmmo.isVisible = false
                        weaponPower.isVisible = false
                        currentWeaponName.setText("\$${weapon.price}")
                    } else {
                        weaponAmmo.isVisible = true
                        weaponPower.isVisible = true
                        currentWeaponName.setText(weapon.name)
                    }
                    if (currentWeapon == 0) {
                        weaponAmmo.isVisible = false
                    } else {
                        currentWeaponImage.isDisabled = !weaponValues[currentWeapon].isAvailable
                    }
                }
            }

            buttons.add(weaponButton).size(TILE_SIZE, TILE_SIZE).pad(TILE_SIZE / 5)
            weaponButton
        }

        //Sets the click listener for the big weapon image button in the middle of the screen
        currentWeaponImage.onClick {
            val currentGunPrice = WeaponUpgrade.values()[currentWeapon].price
            if (weaponButtons[currentWeapon].isDisabled && (moneyValue.money - currentGunPrice) > 0) {
                if (soundsOn) purchaseSound.play()
                moneyValue.money -= currentGunPrice
                weaponValues[currentWeapon].isAvailable = true
                weaponButtons[currentWeapon].isDisabled = false
                currentWeaponImage.isDisabled = false
                weaponAmmo.isVisible = true
                weaponPower.isVisible = true
            }
        }

        //Adds all buttons and tables to the UI
        performance.add(upgradeTables[0]).size(TILE_SIZE * 2f, TILE_SIZE * 1.5f).pad(8f)
        performance.add(upgradeTables[1]).size(TILE_SIZE * 2f, TILE_SIZE * 1.5f).pad(8f).row()
        performance.add(upgradeTables[2]).size(TILE_SIZE * 2f, TILE_SIZE * 1.5f).pad(8f)
        performance.add(upgradeTables[3]).size(TILE_SIZE * 2f, TILE_SIZE * 1.5f).pad(8f).row()
        val currentWeaponUpgradeTable = Table()
        val currentWeaponInfoTable = Table()
        currentWeaponInfoTable.add(currentWeaponImage)
            .size(TILE_SIZE * 1.5f, TILE_SIZE * 1.5f).row()
        currentWeaponInfoTable.add(currentWeaponName).padTop(8f).row()
        currentWeaponUpgradeTable.add(weaponPower)
            .size(TILE_SIZE * 2, TILE_SIZE * 1.5f).pad(8f).row()
        currentWeaponUpgradeTable.add(weaponAmmo)
            .size(TILE_SIZE * 2, TILE_SIZE * 1.5f).pad(8f).row()
        currentWeaponTable.add(currentWeaponInfoTable)
        currentWeaponTable.add(currentWeaponUpgradeTable)
        table.add(moneyLabel).colspan(2).padTop(16f).row()
        table.add(performance)
        table.add(currentWeaponTable).row()
        table.add(buttons).colspan(2).row()
        table.add(backButton).size(TILE_SIZE * 3, TILE_SIZE).padBottom(16f).right()
            .spaceRight(40f)
        table.add(nextButton).size(TILE_SIZE * 3, TILE_SIZE).padBottom(16f).left()
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
        Gdx.input.isCatchBackKey = true
        stage.addAction(
            Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(TRANSITION_DURATION)
            )
        )
    }

    //Gets the button style for a specified weapon index
    private fun getWeaponButtonStyle(weaponUpgrade: WeaponUpgrade): ImageButtonStyle {
        val uiSkin = game.manager.get<Skin>("uiskin/uiskin.json")
        val up = game.manager.get<Texture>(weaponUpgrade.enabledIconPath)
        val disabled = game.manager.get<Texture>(weaponUpgrade.disabledIconPath)
        val style = ImageButtonStyle(uiSkin.get(ButtonStyle::class.java))
        style.imageUp = TextureRegionDrawable(TextureRegion(up))
        style.imageDisabled = TextureRegionDrawable(TextureRegion(disabled))
        return style
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        stage.batch.begin()
        stage.batch.draw(
            background,
            0f,
            0f,
            stage.width,
            stage.height
        )
        stage.batch.end()
        stage.act(delta)
        stage.draw()
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stage.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.run {
                game.screen = MainScreen(game)
            }))
        }
    }
}

