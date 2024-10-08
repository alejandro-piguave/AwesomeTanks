package com.alexpi.awesometanks.game.utils;

public class RandomUtils {
    public static int getRandomInt(int min, int max){
        return (int)Math.floor(Math.random()*(max-min)+min);
    }

    public static boolean getRandomBoolean(){
        return Math.random() > 0.5;
    }

    public static int getRandomInt(int max){
        return (int)Math.floor(Math.random()*max);
    }

    public static float getRandomFloat(float min, float max){
        return (float)Math.random()*(max-min)+min;
    }

    public static float getRandomFloat(float max){
        return (float)Math.random()*max;
    }

    public static float getRandomFloat(double max){
        return (float)Math.floor(Math.random()*(max));
    }

}
