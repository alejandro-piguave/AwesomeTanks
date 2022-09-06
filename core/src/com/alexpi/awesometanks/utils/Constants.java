package com.alexpi.awesometanks.utils;

/**
 * Created by Alex on 14/01/2016.
 */
public class Constants {
    public static final float SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720, TILE_SIZE =80;

    public static final float SQRT2_2 = 0.70710678118f; // sqrt(2)/2

    public static final int LEVEL_COUNT = 30;

    public static final float TRANSITION_DURATION = .3f;

    public static final short CAT_PLAYER_BULLET = 1;
    public static final short CAT_ITEM = 2;
    public static final short CAT_PLAYER = 4;
    public static final short CAT_BLOCK = 8;
    public static final short CAT_ENEMY = 16;
    public static final short CAT_ENEMY_BULLET = 32;
    public static final short ENEMY_BULLET_MASK = CAT_PLAYER | CAT_BLOCK;
    public static final short PLAYER_BULLET_MASK = CAT_ENEMY | CAT_BLOCK;
}
