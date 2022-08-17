package com.alexpi.awesometanks.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 14/01/2016.
 */
public class Constants {
    public static float screenWidth = Gdx.graphics.getWidth(), screenHeight = Gdx.graphics.getHeight(),
            centerY = screenHeight/2, centerX =screenWidth/2,
            tileSize=(screenWidth+screenHeight)/25;
    public static char wall = 'X', space = ' ',start = 'S', end = 'E', gate = 'G', bricks = 'B', box = '*'
            , bomb = 'O', spawner = '+';

    public static final String[]colorNames ={"White","Green","Red","Blue","Yellow","Orange","Purple","Sky","Grey","Light Grey"};
    public static final Color[] colors = {Color.WHITE,Color.CHARTREUSE,Color.SCARLET,Color.ROYAL, Color.GOLD, Color.ORANGE,Color.PURPLE, Color.SKY,Color.GRAY,  Color.LIGHT_GRAY};

    public static final int prices[][] = {
            {200, 500, 1000, 2000, 2000, 3500, 3500},
            {0, 100, 200, 300, 300, 400, 400},
            {0, 3000, 5000, 7500, 7500, 10000, 10000}
    };

    public static float TRANSITION_DURATION = .3f;

    public static final int MINIGUN = 0;
    public static final int SHOTGUN = 1;
    public static final int RICOCHET = 2;
    public static final int FLAMETHROWER = 3;
    public static final int CANON = 4;
    public static final int LASERGUN = 5;
    public static final int RAILGUN = 6;


    public static final short CAT_BULLET = 1;
    public static final short CAT_ITEM = 2;
    public static final short CAT_TANK = 4;
    public static final short CAT_BLOCK = 8;
    public static final short CAT_ENEMY = 16;
    public static final short ENEMY_BULLET_MASK = CAT_TANK | CAT_BLOCK;
    public static final short TANK_BULLET_MASK = CAT_ENEMY | CAT_BLOCK;


}
