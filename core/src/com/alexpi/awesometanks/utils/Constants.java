package com.alexpi.awesometanks.utils;

/**
 * Created by Alex on 14/01/2016.
 */
public class Constants {
    public static final float SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720,
            CENTER_Y = SCREEN_HEIGHT /2, CENTER_X = SCREEN_WIDTH /2,
            TILE_SIZE =80;
    public static final char wall = 'X', space = ' ',start = 'S', gate = '@', bricks = '#', box = '*'
            , bomb = 'O', spawner = '+',
            MINIGUN_BOSS = 'A', SHOTGUN_BOSS = 'B', RICOCHET_BOSS = 'C', FLAMETHROWER_BOSS = 'D', CANON_BOSS = 'E',
            LASERGUN_BOSS = 'F', RAILGUN_BOSS = 'G';

    public static final char[] solidBlocks = {wall, gate, bricks};

    public static final float SQRT2_2 = 0.70710678118f;

    public static final int[][] prices = {
            {0, 100, 200, 300, 300, 400, 400}, //AMMO PRICES
            {0, 2750, 8000, 10000, 10000, 28000, 28000}//GUN PRICES
    };

    public static final int[][] gunUpgradePrices = {
            {200, 300, 400, 500, 600},//MINIGUN
            {500, 900, 1300, 1700, 2100},//SHOTGUN
            {2500, 3000, 3500, 4000, 4500},//RICOCHET
            {3000, 4000, 5000, 6000, 7000},//FLAMETHROWER
            {3000, 4000, 5000, 6000, 7000},//CANON,
            {11000, 12000, 13000, 14000, 15000},//LASER
            {11000, 12000, 13000, 14000, 15000}//RAILGUN
    };

    public static final int[][] upgradePrices = {
            {2000, 4000, 8000, 16000, 32000},//ARMOR
            {500, 600, 700, 800, 900}, //MOVEMENT SPEED
            {500, 600, 700, 800, 900}, //ROTATION SPEED
            {500, 600, 700, 800, 900} //VISIBILITY
    };

    public static final int LEVEL_COUNT = 30;

    public static final String ARMOR = "armor";
    public static final String MOVEMENT_SPEED = "speed";
    public static final String ROTATION_SPEED = "rotation";
    public static final String VISIBILITY = "visibility";

    public static final String[] WEAPON_NAMES = {"Minigun", "Shotgun", "Ricochet", "Flamethrower", "Canon", "Lasergun", "Railgun"};

    public static final float TRANSITION_DURATION = .3f;

    public static final int MINIGUN = 0;
    public static final int SHOTGUN = 1;
    public static final int RICOCHET = 2;
    public static final int FLAMETHROWER = 3;
    public static final int CANON = 4;
    public static final int LASERGUN = 5;
    public static final int RAILGUN = 6;


    public static final short CAT_PLAYER_BULLET = 1;
    public static final short CAT_ITEM = 2;
    public static final short CAT_PLAYER = 4;
    public static final short CAT_BLOCK = 8;
    public static final short CAT_ENEMY = 16;
    public static final short CAT_ENEMY_BULLET = 32;
    public static final short ENEMY_BULLET_MASK = CAT_PLAYER | CAT_BLOCK;
    public static final short PLAYER_BULLET_MASK = CAT_ENEMY | CAT_BLOCK;


}
