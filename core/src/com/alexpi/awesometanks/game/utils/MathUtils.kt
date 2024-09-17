package com.alexpi.awesometanks.game.utils

import com.badlogic.gdx.math.MathUtils
import kotlin.math.abs
import kotlin.math.sqrt


fun fastHypot(x: Double, y: Double): Double {
    return sqrt(x * x + y * y)
}

//We assume a and b are between 0 and PI*2
fun getNormalizedAbsoluteDifference(a: Float, b: Float): Float {
    var difference = b - a
    if(difference < -MathUtils.PI)
        difference += MathUtils.PI2
    else if(difference >= MathUtils.PI)
        difference -= MathUtils.PI2
    return abs(difference)
}

//Converts an angle to a value between 0 and MathUtils.PI2
fun Float.normalizeAngle(): Float {
    return if(this >= MathUtils.PI2)
        this % MathUtils.PI2
    else if(this < 0){
        this % MathUtils.PI2 + MathUtils.PI2
    }else this
}