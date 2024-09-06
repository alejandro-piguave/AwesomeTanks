package com.alexpi.awesometanks.map

import com.badlogic.gdx.Gdx
import java.io.BufferedReader
import java.io.IOException

class MapLoader {
    fun load(level: Int): Array<CharArray> {
        val file = Gdx.files.internal("levels/level$level.txt")
        val reader = BufferedReader(file.reader())
        val arrayList = mutableListOf<CharArray>()

        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                line?.let { arrayList.add(it.toCharArray()) }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return arrayList.toTypedArray()
    }

}