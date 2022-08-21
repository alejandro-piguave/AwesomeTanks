package com.alexpi.awesometanks.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rumble {
    private static float time = 0;
    private static float currentTime = 0;
    private static float power = 0;
    private static float currentPower = 0;
    private static Vector2 pos = new Vector2();

    public static void rumble(float rumblePower, float rumbleLength) {
        power = rumblePower;
        time = rumbleLength;
        currentTime = 0;
    }

    public static Vector2 tick(float delta) {
        if (currentTime <= time) {
            currentPower = power * ((time - currentTime) / time);

            pos.x = (MathUtils.random() - 0.5f) * 2 * currentPower;
            pos.y = (MathUtils.random() - 0.5f) * 2 * currentPower;

            currentTime += delta;
        } else {
            time = 0;
        }
        return pos;
    }

    public static float getRumbleTimeLeft() {
        return time;
    }

    public static Vector2 getPos() {
        return pos;
    }
}