package com.alexpi.awesometanks.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Alex on 25/01/2016.
 */
public class Map {
    private char[][] map;
    int rA, cA, rows, columns;
    public Map(){
    }

    public char[][] getMap(){return map;}

    public int getColumns() {return cA;}
    public int getRows() {return rA;}
    public float getWidth(){return cA*Constants.tileSize;}
    public float getHeight(){return rA*Constants.tileSize;}

    public void getLevel (int num){
        FileHandle file = Gdx.files.internal("levels/levels.txt");
        BufferedReader reader = new BufferedReader(file.reader());
        String line;
        Vector<String>ans = new Vector<String>();
        try {
            line = reader.readLine();
            while (line !=null && !line.contains(String.valueOf(num)))
                line = reader.readLine();
            while ((line = reader.readLine())!=null && !line.contains("#"))
                ans.add(line);

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ans.isEmpty())return;
        cA = ans.firstElement().length();
        rA = ans.size();
        map = new char[rA][cA];

        for(int i = 0; i <rA;i++)
            map[i] = ans.get(i).toCharArray();



    }
    public void deleteBlockAt(int posX, int posY){
        map[posY][posX] = Constants.space;
    }

    public void generateRandomMaze(int rows, int columns) {
        this.rows = rows; this.columns = columns;
        Random rnd  = new Random();
        int rP = 2 * rows + 1, cP = 2 * columns + 1;
        int extension = 3, width= extension+1;

        rA = rows * (extension + 1) + 1;
        cA = columns * (extension + 1) + 1;

        char[][] miniMaze = new char[rP][cP];
        map = new char[rA][cA];

        StringBuilder s = new StringBuilder(columns);
        for(int x=0;x<cP;x++)
            s.append(Constants.wall);

        for(int x=0;x<rP;x++) miniMaze[x] = s.toString().toCharArray();


        // select random point and open as start node
        Point st = new Point(rnd.nextInt(rows)*2+1,rnd.nextInt(columns)*2+1,null);
        miniMaze[st.r][st.c] = Constants.start;

        // iterate through direct neighbors of node
        Vector<Point> frontier = new Vector<Point>();
        for(int x=-1;x<=1;x++)
            for(int y=-1;y<=1;y++){
                if(x==0&&y==0||x!=0&&y!=0)
                    continue;
                try{
                    if(miniMaze[st.r+x][st.c+y]== Constants.space) continue;
                }catch(Exception e){ // ignore ArrayIndexOutOfBounds
                    continue;
                }
                // add eligible points to frontier
                frontier.add(new Point(st.r+x,st.c+y,st));
            }

        Point last=null;
        while(!frontier.isEmpty()){

            // pick current node at random
            Point cu = frontier.remove(rnd.nextInt(frontier.size()));
            Point op = cu.opposite();
            try{
                // if both node and its opposite are blocks
                if(miniMaze[cu.r][cu.c]== Constants.wall){
                    if(miniMaze[op.r][op.c]== Constants.wall){

                        // open path between the nodes
                        miniMaze[cu.r][cu.c]= Constants.space;
                        miniMaze[op.r][op.c]= Constants.space;

                        // store last node in order to mark it later
                        last = op;
                        // iterate through direct neighbors of node, same as earlier
                        for(int x=-1;x<=1;x++)
                            for(int y=-1;y<=1;y++){
                                if(x==0&&y==0||x!=0&&y!=0) continue;
                                try{
                                    if(miniMaze[op.r+x][op.c+y]== Constants.space) continue;
                                }catch(Exception e){
                                    continue;
                                }
                                frontier.add(new Point(op.r+x,op.c+y,op));
                            }
                    }
                }
            }catch(Exception e){ // ignore NullPointer and ArrayIndexOutOfBounds
            }
            if(frontier.isEmpty())
                miniMaze[last.r][last.c]= Constants.end;
        }
        for(int i =0;i<rA;i++)
            for(int j=0;j<cA;j++)
                map[i][j]= Constants.space;

        for (int i = 0; i < rA; i += width)
            for (int j = 0; j < cA; j++) map[i][j] = Constants.wall;

        for (int i = 0; i < cA; i += width)
            for (int j = 0; j < rA; j++) map[j][i] = Constants.wall;


        for (int i = 2; i < rP - 1; i += 2) {
            for (int j = 1; j < cP - 1; j += 2) {
                int row, col;
                if (miniMaze[i][j] == Constants.space) {
                    row = (i / 2) * width;
                    col = ((j - 1) / 2) * width + 1;
                    for (int h = col; h < col + extension; h++) map[row][h] = Constants.space;
                }
            }
        }
        for (int i = 1; i < rP - 1; i += 2) {
            for (int j = 2; j < cP - 1; j += 2) {
                int row, col;
                if (miniMaze[i][j] == Constants.space) {
                    row = ((i - 1) / 2) * width + 1;
                    col = (j / 2) * width;
                    for (int h = row; h < row + extension; h++) map[h][col] = Constants.space;
                }
            }
        }


        for (int i = 1; i < rP; i += 2) {
            for (int j = 1; j < cP; j += 2) {
                if (miniMaze[i][j] == Constants.start || miniMaze[i][j] == Constants.end)
                    map[(((i - 1) / 2) * width + 1) + rnd.nextInt(extension)][(((j - 1) / 2) * width + 1) + rnd.nextInt(extension)] = miniMaze[i][j];
            }
        }

       /* Gdx.app.log("Maze width",getWidth()+"");
        Gdx.app.log("Maze height",getHeight()+"");
        Gdx.app.log("Maze rows",getRows()+"");
        Gdx.app.log("Maze columns",getColumns()+"");
        Gdx.app.log("Maze mini rows",rP+"");
        Gdx.app.log("Maze mini columns",cP+"");
        for(int i= 0; i <rA;i++){
            for(int j = 0; j<cA;j++)
                System.out.print(map[i][j]);
            System.out.println();
        }*/





    }

}
