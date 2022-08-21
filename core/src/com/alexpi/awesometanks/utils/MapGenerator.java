package com.alexpi.awesometanks.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Alex on 25/01/2016.
 */
public class MapGenerator {
    public static char[][] getLevelMap(int level){
        FileHandle file = Gdx.files.internal("levels/levels.txt");
        BufferedReader reader = new BufferedReader(file.reader());
        String line;
        Vector<String> ans = new Vector<>();
        try {
            line = reader.readLine();
            while (line !=null && !line.contains(String.valueOf(level)))
                line = reader.readLine();
            while ((line = reader.readLine())!=null && !line.contains("#"))
                ans.add(line);

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ans.isEmpty()) throw new IllegalArgumentException("No such level");
        int cA = ans.firstElement().length();
        int rA = ans.size();
        char[][] map = new char[rA][cA];

        for(int i = 0; i <rA;i++)
            map[i] = ans.get(i).toCharArray();
        return map;
    }


    public static boolean[][] getShadowMap(char[][] map){
        boolean[][] shadowMap = new boolean[map.length][map[0].length];

        //for(int y = 0; y < map.length; y++) for(int x = 0; x < map[y].length; x++)


        return shadowMap;
    }

}
