/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Ashok
 */
public class GameScreen implements Screen
{
    private TestGame mg;
    
    public static final int FRAME_RATE = 50;
    
    private double gameTime = 60;
    
    private GameInput input;
    
    private ArrayList<GameMessage> messages;
    
    private Thread myGameThread;
    
    /*private Texture tankPic;
    private Texture dirtBackgroundPic;
    private Texture visionPic;
    private Texture blackPic;
    private Texture wallPic;*/
    
    private int[][] grid; //grid value >= 100 means there is a vision checkpoint there
    private int[][] picGrid;
    
    public HashMap<String, Tank> tanks = new HashMap<String, Tank>();
    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    
    private long updateTimer = System.currentTimeMillis();
    private long updateTimeout = 0;
    
    private int phase;
    public float alphaInc = 0.085f;
    private float digsoundtimer = 0;
    public float zoom = 1;
    private float phaseTimer = 3000;
    private float initAlpha = 1.0f, initAlphaDir = -1;
    private float houseParticleTimer = 0f, houseParticleTimax = (float)(Math.random()*0.5+0.2);
    
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    public boolean phaseChanging = false;
    private boolean phaseChangeHit = false;
    
    private GlyphLayout gl = new GlyphLayout();
    
    public GameScreen(TestGame g, int gameMode)
    {
        
        g.out.println("1V1");
        int size;
        if(gameMode == 0)
            size = 2;
        else if(gameMode == 1)
            size = 6;
        else
            size = 10;
        grid = new int[(int)(Math.pow(size,0.8)*919)+400][(int)(Math.pow(size,0.8)*804)+400]; //1v1: 800x700 per player. 5v5: 580x507 per player.
        picGrid = new int[(int)(Math.pow(size,0.8)*919)+400][(int)(Math.pow(size,0.8)*804)+400]; //1v1: 800x700 per player. 5v5: 580x507 per player.
        messages = new ArrayList<GameMessage>();
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[i].length; j++)
                if(i < 200 || i > grid.length-200 || j < 200 || j > grid[i].length-200)
                    grid[i][j] = 2;
                else
                {
                    grid[i][j] = 1;
                    picGrid[i][j] = (int)(Math.random()*4);
                }
        mg = g;
        mg.a.stopMusic();
        mg.stage.clear();
        mg.cam.update();
        phase = 0;
        input = new GameInput(mg, this);
        Gdx.input.setInputProcessor(input);
        startThread();
    }
    
    private void startThread()
    {
        myGameThread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    while (true) {
                        String line = mg.in.readLine();
                        //System.out.println(line);
                        if(line == null || line.equalsIgnoreCase("null"))
                            break;
                        if (line.startsWith("DISCONNECT"))
                        {
                            GameMessage g = new GameMessage();
                            if(tanks.get(line.split("/")[1]).getTeam() == 1)
                                g.addString(line.split("/")[1], Color.GREEN);
                            else
                                g.addString(line.split("/")[1], Color.BLUE);
                            g.addString(" has quit.", Color.LIGHT_GRAY);
                            messages.add(0, g);
                            mg.a.playSfx("msg");
                            tanks.remove(line.split("/")[1]);
                        }
                        else if (line.startsWith("GRID"))
                        {
                            String[] splitty = line.split("/");
                            grid[Integer.parseInt(splitty[1])][Integer.parseInt(splitty[2])] = Integer.parseInt(splitty[3]);
                        }
                        else if (line.startsWith("VISB"))
                        {
                            String[] splitt = line.split("VISB/");
                            for(int i = 1; i < splitt.length; i++)
                            {
                                String[] splitty = splitt[i].split("/");
                                tanks.get(mg.myTankName).setVisibilityB(Integer.parseInt(splitty[0]), Boolean.parseBoolean(splitty[1]));
                            }
                        }
                        else if (line.startsWith("VIS"))
                        {
                            String[] splitt = line.split("VIS/");
                            for(int i = 1; i < splitt.length; i++)
                            {
                                String[] splitty = splitt[i].split("/");
                                tanks.get(mg.myTankName).setVisibility(splitty[0], Boolean.parseBoolean(splitty[1]));
                            }
                        }
                        else if (line.startsWith("GCUNIT"))
                        {
                            String[] splitty = line.split("/");
                            int radius = 24;//(int)(3*mg.sprites.get("tank1").getRegionWidth()/2+3); //ATTENTION: the "3*" is hard-coded multiplication of image width because Tank.imgScale changes already!!
                            for(int sX = (int)(Integer.parseInt(splitty[1])-radius); sX < (int)(Integer.parseInt(splitty[1])+radius); sX++)
                            {
                                //double dist = Math.sqrt(radius*radius-(Integer.parseInt(splitty[0])-sX)*(Integer.parseInt(splitty[0])-sX));
                                for(int sY = (int)(Integer.parseInt(splitty[2])-radius); sY < (int)(Integer.parseInt(splitty[2])+radius); sY++)
                                {
                                    if(mg.digMask[(int)(sX-Integer.parseInt(splitty[1])+radius)][(int)(sY-Integer.parseInt(splitty[2])+radius)])
                                    {
                                        int type = grid[sX][sY]%100;
                                        if(type == 1)
                                        {
                                            grid[sX][sY] -= type;
                                            /*if(sX%10 == 0 && sY%10 == 0) {
                                                grid[sX][sY] = 100+type;
                                            }*/
                                        }
                                    }
                                }
                            }
                            //grid[Integer.parseInt(splitty[1])][Integer.parseInt(splitty[2])] += 100;
                        }
                        else if (line.startsWith("GC"))
                        {
                            String[] splitt = line.split("GC/");
                            for(int i = 1; i < splitt.length; i++)
                            {
                                String[] splitty = splitt[i].split("/");
                                int radius = 24;//(int)(3*mg.sprites.get("tank1").getRegionWidth()/2+3); //ATTENTION: the "3*" is hard-coded multiplication of image width because Tank.imgScale changes already!!
                                for(int sX = (int)(Integer.parseInt(splitty[0])-radius); sX < (int)(Integer.parseInt(splitty[0])+radius); sX++)
                                {
                                    for(int sY = (int)(Integer.parseInt(splitty[1])-radius); sY < (int)(Integer.parseInt(splitty[1])+radius); sY++)
                                    {
                                        if(mg.digMask[(int)(sX-Integer.parseInt(splitty[0])+radius)][(int)(sY-Integer.parseInt(splitty[1])+radius)])
                                        {
                                            int type = grid[sX][sY]%100;
                                            if(type == 1)
                                            {
                                                grid[sX][sY] -= type;
                                                /*if(sX%10 == 0 && sY%10 == 0)
                                                    grid[sX][sY] = 100+type;*/
                                            }
                                        }
                                    }
                                }
                            }
                            /*for(int i = 0; i < grid.length; i++)
                                for(int j = 0; j < grid[0].length; j++)
                                {
                                    int type = grid[i][j]%100;
                                    if(type == 0)
                                    {
                                        //grid[i][j] += 1;
                                        if(i%10 == 0 && j%10 == 0) {
                                            grid[i][j] = 100+type;
                                        }
                                    }
                                }*/
                            /*for(Tank t : tanks.values()) {
                                for(int i = (int)t.initX-90; i < (int)t.initX+90; i++) {
                                    for(int j = (int)t.initY-100; j < (int)t.initY+100; j++) {
                                        if(i < t.initX-85 || i > t.initX+85 || j < t.initY-95 | j > t.initY+95) {
                                            if(i > t.initX-30 && i < t.initX+30 && (j < t.initY-95 || j > t.initY+95)) {
                                                grid[i][j] = 0;
                                                if(i%10 == 0 && j%10 == 0) {
                                                    grid[i][j] += 100;
                                                }
                                            }
                                            else {
                                                grid[i][j] = 2;
                                            }
                                        } else {
                                            grid[i][j] = 0;
                                            if(i%10 == 0 && j%10 == 0)
                                                grid[i][j] += 100;
                                        }
                                    }
                                }
                            }*/
                        }
                        else if (line.startsWith("PHASE") && phase == 0)
                        {
                            //String[] splitty = line.split("/");
                            gameTime = 120;
                            phaseChanging = true;
                            for(Tank t : tanks.values())
                            {
                                t.setXSpeed(0);
                                t.setYSpeed(0);
                            }
                        }
                        else if (line.startsWith("BULLET"))
                        {
                            String[] splitty = line.split("/");
                            String src = splitty[2];
                            if(splitty[2].equals("null"))
                                src = null;
                            bullets.add(new Bullet(splitty[1], src, Integer.parseInt(splitty[3]), Double.parseDouble(splitty[4]), Double.parseDouble(splitty[5]), Double.parseDouble(splitty[6]), Double.parseDouble(splitty[7]), Integer.parseInt(splitty[8]), Integer.parseInt(splitty[9]), Boolean.parseBoolean(splitty[10]), Boolean.parseBoolean(splitty[11])));
                            if(src != null && src.equals(mg.myTankName))
                            {
                                mg.a.playSfx("shoot2");
                                if(isVisible(Double.parseDouble(splitty[4]), Double.parseDouble(splitty[5])))
                                {
                                    tanks.get(mg.myTankName).setVisibilityB(Integer.parseInt(splitty[3]), true);
                                }
                            }
                        }
                        else if (line.startsWith("SYSTEMMESSAGE"))
                        {
                            String[] splitty = line.split("/");
                            GameMessage g = new GameMessage();
                            for(int i = 1; i < splitty.length; i+=2)
                            {
                                String[] splitterino = splitty[i+1].split(",");
                                g.addString(splitty[i], new Color(Float.parseFloat(splitterino[0]),Float.parseFloat(splitterino[1]),Float.parseFloat(splitterino[2]),1));
                            }
                            messages.add(0, g);
                            if(g.getMessage().equals("1") || g.getMessage().equals("2") || g.getMessage().equals("3") || g.getMessage().equals("4") || g.getMessage().equals("5") || g.getMessage().equals("GO!"))
                                mg.a.playSfx("countdown");
                            else
                                mg.a.playSfx("msg");
                            if(g.getMessage().equals("GO!"))
                            {
                                gameStarted = true;
                                //mg.a.playMusic("game", 0.6f);
                                messages.add(0, new GameMessage((int)tanks.get(mg.myTankName).getX()+","+(int)tanks.get(mg.myTankName).getY(), Color.WHITE));
                            }
                            else if(g.getMessage().startsWith("The game is over.")) {
                                gameEnded = true;
                                messages.add(0, new GameMessage("Tap anywhere to return to the main menu...", Color.CYAN));
                                mg.a.playSfx("msg");
                            }
                            else if(g.getMessage().endsWith(" has died."))
                            {
                                if(tanks.containsKey(g.getMessage().substring(0, g.getMessage().length()-10)))
                                {
                                    Tank myT = tanks.get(g.getMessage().substring(0, g.getMessage().length()-10));
                                    myT.setDead(true);
                                    for(double a = 0; a < Math.PI*2; a+=Math.PI/(Math.random()*20+60))
                                    {
                                        particles.add(new Particle(mg.sprites.get("bullet2"),6,0,(int)(Math.random()*1000+2000),myT.getX(),myT.getY(),Math.cos(a)*0.005+Math.random()*0.02*Math.signum(Math.cos(a)), Math.sin(a)*0.005+Math.random()*0.02*Math.signum(Math.sin(a)),0,0,0.01*Math.random()+0.005,true));
                                    }
                                    for(double a = 0; a < Math.PI*2; a+=Math.PI/(Math.random()*20+60))
                                    {
                                        particles.add(new Particle(mg.sprites.get("bullet2"),6,0,(int)(Math.random()*1000+2000),myT.getX(),myT.getY(),Math.random()*0.01*Math.signum(Math.cos(a)), Math.random()*0.01*Math.signum(Math.sin(a)),-Math.cos(a)*0.00002,-Math.sin(a)*0.00002,0.01*Math.random()+0.005,true));
                                    }
                                    if(myT.getName().equals(mg.myTankName)) {
                                        messages.remove(0);
                                        messages.add(0, new GameMessage("You have died...", Color.RED));
                                    }
                                }
                                mg.a.playSfx("death3", false, 0.9f);
                            }
                        } 
                        else if (line.startsWith("MESSAGE"))
                        {
                            String[] splitty = line.split("/");
                            GameMessage g = new GameMessage();
                            for(int i = 1; i < splitty.length; i+=2)
                            {
                                String[] splitterino = splitty[i+1].split(",");
                                g.addString(splitty[i], new Color(Float.parseFloat(splitterino[0]),Float.parseFloat(splitterino[1]),Float.parseFloat(splitterino[2]),1));
                            }
                            messages.add(0, g);
                            mg.a.playSfx("msg");
                        }
                        else if (line.startsWith("GAMESETTING"))
                        {
                            String[] splitty = line.split("/", 2);
                            gameTime = Float.parseFloat(splitty[1]);
                        }
                        else if (line.startsWith("SPEEDUPDATE"))
                        {
                            try {
                                String[] splitty = line.split("/");
                                Tank t = tanks.get(splitty[1]);
                                t.setXSpeed(Float.parseFloat(splitty[2]));
                                t.setYSpeed(Float.parseFloat(splitty[3]));
                                updateTimeout = 0;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else if (line.startsWith("PUSHTANK") && !gameEnded)
                        {
                            try {
                                String[] splitt = line.split("PUSHTANK/");
                                for(int k = 1; k < splitt.length; k++)
                                {
                                    String[] splitty = splitt[k].split("/");
                                    Tank t;
                                    if(tanks.containsKey(splitty[0]))
                                        t = tanks.get(splitty[0]);
                                    else
                                        t = new Tank(splitty[0], Integer.parseInt(splitty[1]), Integer.parseInt(splitty[2]));
                                    t.setX(Integer.parseInt(splitty[1]));
                                    t.setY(Integer.parseInt(splitty[2]));
                                    t.setXSpeed(Float.parseFloat(splitty[3]));
                                    t.setYSpeed(Float.parseFloat(splitty[4]));
                                    t.setRotation(Double.parseDouble(splitty[5]));
                                    t.setTeam(Integer.parseInt(splitty[6]));
                                    t.setHealth(Integer.parseInt(splitty[7]));
                                    updateTimeout = 0;
                                    if(!gameStarted)
                                    {
                                        t.initX = (float) t.getX();
                                        t.initY = (float) t.getY();
                                        for(int i = (int)t.getX()-90; i < (int)t.getX()+90; i++)
                                            for(int j = (int)t.getY()-100; j < (int)t.getY()+100; j++)
                                                if(i < t.getX()-85 || i > t.getX()+85 || j < t.getY()-95 | j > t.getY()+95)
                                                    if(i > t.getX()-30 && i < t.getX()+30 && (j < t.getY()-95 || j > t.getY()+95)) {
                                                        grid[i][j] = 0;
                                                    } else {
                                                        if(t.getTeam() == 0)
                                                            grid[i][j] = 3;
                                                        else
                                                            grid[i][j] = 4;
                                                    }
                                                else
                                                    grid[i][j] = 0;
                                    }
                                    if(!tanks.containsKey(splitty[0]))
                                        tanks.put(t.getName(), t);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else
                        {
                            if(!line.startsWith("PUSHTANK"))
                                System.out.println("Unknown command recieved from server: " + line);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    int choice = JOptionPane.showConfirmDialog(null, "Couldn't connect to server! Try again?");
                    if(choice == 0)
                        run();
                    else
                    {
                        mg.out.println("DONE");
                        Gdx.app.exit();
                    }
                }
            }
        };
        myGameThread.start();
    }
    
    @Override
    public void show() {
    }
    
    @Override
    public void render(float delta)
    {
        if(gameEnded && Gdx.input.justTouched())
        {
            myGameThread.interrupt();
            mg.out.println("DONE");
            initAlpha = delta;
            initAlphaDir = 1;
            input = null;
            Gdx.input.setInputProcessor(null);
            gameEnded = false;
            gameStarted = false;
        }
        
        if(gameStarted && tanks.containsKey(mg.myTankName) && !gameEnded)
        {
            if(!phaseChanging)
                gameTime -= delta;
            if(gameTime < 0)
            {
                gameTime = 120;
                phaseChanging = true;
                for(Tank t : tanks.values())
                {
                    t.setXSpeed(0);
                    t.setYSpeed(0);
                }
            }
        }
        
        if(tanks.containsKey(mg.myTankName))
        {
            //the commented line below makes the camera update only when the tank is 5 px away from it, making a "shuffling" effect similar to the original tunneler.
            //if(Math.sqrt(Math.pow(tanks.get(mg.myTankName).getX()-mg.cam.position.x,2) + Math.pow(tanks.get(mg.myTankName).getY()-mg.cam.position.y,2)) > 6)
                mg.cam.position.set((float)tanks.get(mg.myTankName).getX(), (float)tanks.get(mg.myTankName).getY(), 0);
            mg.cam.zoom = zoom;
            mg.cam.update();
            mg.batch.setProjectionMatrix(mg.cam.combined);
        }
        houseParticleTimer+=delta;
        if(houseParticleTimer > houseParticleTimax)
        {
            houseParticleTimer = 0;
            houseParticleTimax = (float)(Math.random()*0.5+0.2);
            for(Tank t : tanks.values())
            {
                String str;
                if(t.getTeam() == 0)
                    str = "bluewall";
                else
                    str = "greenwall";
                for(int i = 0; i < 2; i++)
                    particles.add(new Particle(mg.sprites.get(str),3.5f,0,1200,t.initX+(Math.random()*170-85),t.initY+(Math.random()*190-95),0,0.02,0,-0.000001,0.008*Math.signum(Math.random()-0.5),true));
            }
        }
        
        mg.shake(delta);
        
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT); 

        mg.batch.begin();
        mg.batch.setColor(1f, 1f, 1f, 1f);
        
        for(int i = 0; i < particles.size(); i++)
        {
            Particle p = particles.get(i);
            p.update(delta*1000);
            if(p.isDone())
            {
                particles.remove(i);
                i--;
            }
        }
        
        if(gameStarted && tanks.containsKey(mg.myTankName))
        {
            updateTimeout += delta*1000;
            /*int startX = (int)(mg.cam.position.x-mg.sprites.get("vision2").getRegionWidth()/2+50);
            int startY = (int)(mg.cam.position.y-mg.sprites.get("vision2").getRegionHeight()/2+50);
            int endX = (int)(mg.cam.position.x+mg.sprites.get("vision2").getRegionWidth()/2-50);
            int endY = (int)(mg.cam.position.y+mg.sprites.get("vision2").getRegionHeight()/2-50);*/
            //if(phase == 0)
            //{
                int startX = (int)(mg.cam.position.x-mg.sprites.get("vision1").getRegionWidth()/2+30);
                int startY = (int)(mg.cam.position.y-mg.sprites.get("vision1").getRegionHeight()/2+30);
                int endX = (int)(mg.cam.position.x+mg.sprites.get("vision1").getRegionWidth()/2-30);
                int endY = (int)(mg.cam.position.y+mg.sprites.get("vision1").getRegionHeight()/2-30);
            //}
            if(startX < 0)
                startX = 0;
            if(startY < 0)
                startY = 0;
            if(endX > grid.length-1)
                endX = grid.length-1;
            if(endY > grid[0].length-1)
                endY = grid[0].length-1;
            float[][] alphastore = new float[endX-startX+50][endY-startY+50];
            for(int i = 24; i < alphastore.length-25; i++)
            {
                for(int j = 24; j < alphastore[i].length-25; j++)
                {
                    int type = grid[i-24+startX][j-24+startY]%100;
                    if(phase == 0) {
                        alphastore[i][j] = 1f;
                    }
                    else {
                        int vis = grid[i-24+startX][j-24+startY]/100;
                        if(type != 0)
                        {
                            alphastore[i][j] = 1f;
                        }
                        else {
                            if(alphastore[i][j] == 0f)
                            {
                                alphastore[i][j] = 0.25f;
                            }
                            if(vis == 1)
                            {
                                if(isVisible(i-24+startX, j-24+startY))
                                {
                                    int radius = 24;//(int)(3*mg.sprites.get("tank1").getRegionWidth()/2+3); //ATTENTION: the "3*" is hard-coded multiplication of image width because Tank.imgScale changes already!!
                                    for(int sX = (int)(i-radius); sX < (int)(i+radius); sX++)
                                    {
                                        //double dist = Math.sqrt(radius*radius-(i-sX)*(i-sX));
                                        for(int sY = (int)(j-radius); sY < (int)(j+radius); sY++)
                                        {
                                            if(mg.digMask[(int)(sX-i+radius)][(int)(sY-j+radius)])
                                            {
                                                alphastore[sX][sY] += alphaInc;
                                                if(alphastore[sX][sY] > 1f)
                                                    alphastore[sX][sY] = 1f;
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    //mg.batch.draw(mg.sprites.get("wall"), i+mg.mg.camShakeX, j+mg.camShakeY);
                                }
                            }
                        }
                    }
                }
            }
            for(int i = startX; i < endX; i++)
            {
                for(int j = startY; j < endY; j++)
                {
                    int type = grid[i][j]%100;
                    mg.batch.setColor(1f,1f,1f,alphastore[i-startX+24][j-startY+24]);
                    //if(phase == 0)
                    {
                        switch(type)
                        {
                            case 0:
                                mg.batch.draw(mg.sprites.get("dugdirt"+picGrid[i][j]), i+mg.camShakeX, j+mg.camShakeY);
                                break;
                            case 1:
                                mg.batch.draw(mg.sprites.get("dirt"+picGrid[i][j]), i+mg.camShakeX, j+mg.camShakeY);
                                break;
                            case 2:
                                mg.batch.draw(mg.sprites.get("wall"), i+mg.camShakeX, j+mg.camShakeY);
                                break;
                            case 3:
                                mg.batch.draw(mg.sprites.get("bluewall"), i+mg.camShakeX, j+mg.camShakeY);
                                break;
                            case 4:
                                mg.batch.draw(mg.sprites.get("greenwall"), i+mg.camShakeX, j+mg.camShakeY);
                                break;
                        }
                    }
                }
            }
            mg.batch.setColor(Color.WHITE);
            for(int bul = 0; bul < bullets.size(); bul++)
            {
                Bullet b = bullets.get(bul);
                //grid[(int)b.getX()][(int)b.getY()] = 0; //bullet destroys tiles
                double[] dat = b.mockUpdate((int)(delta*1000));
                Integer[] gridTypes = new Integer[(int)Math.sqrt(Math.pow(b.getX()-dat[0],2)+Math.pow(b.getY()-dat[1],2))];
                for(int i = 0; i < gridTypes.length; i++)
                {
                    gridTypes[i] = grid[(int)(b.getX()+Math.cos(b.getRotation())*i)][(int)(b.getY()+Math.sin(b.getRotation())*i)]%100;
                }
                boolean go = false;
                for(int ii : gridTypes)
                {
                    if(ii != 0) {
                        go = true;
                        break;
                    }
                }
                if(go && !b.piercesWalls)
                {
                    bullets.remove(bul);
                    bul--;
                    if(b.getSource() != null && b.getSource().equals(mg.myTankName))
                        mg.a.playSfx("hitwall");
                }
                else if(dat[2] >= b.getLifetime())
                {
                    bullets.remove(b);
                    bul--;
                }
                else if(!b.piercesTanks)
                {
                    for(Tank t : tanks.values())
                    {
                        if(!t.isDead() && Math.sqrt(Math.pow(t.getX()-b.getX(),2) + Math.pow(t.getY()-b.getY(),2)) < mg.sprites.get("tank1").getRegionWidth()*Tank.imgScale/2-2)
                        {
                            bullets.remove(bul);
                            bul--;
                            t.setHealth(t.getHealth()-b.getDamage());
                            for(double a = 0; a < Math.PI*4; a+=Math.PI/(Math.random()*10+15))
                                particles.add(new Particle(mg.sprites.get("bullet2"),2.5f,-0.001f,400,t.getX(),t.getY(),Math.random()*Math.cos(a)*0.05+0.015*Math.cos(a),Math.random()*Math.sin(a)*0.05+0.015*Math.sin(a),-Math.cos(a)*0.00005,-Math.sin(a)*0.00005,0.002*Math.signum(Math.random()-0.5),true));
                            if(t.getName().equals(mg.myTankName))
                            {
                                mg.a.stopSfx("hit");
                                mg.a.playSfx("hit", false, 0.55f);
                            }
                            break;
                        }
                    }
                }
                if(bullets.contains(b))
                {
                    b.update((int)(delta*1000));
                }
                if(Math.sqrt(Math.pow(b.getX()-mg.cam.position.x,2) + Math.pow(b.getY()-mg.cam.position.y,2)) < 235 &&
                        tanks.get(mg.myTankName).isVisibleB(b.getID()))
                {
                    TextureRegion t = mg.sprites.get(b.getName());
                    mg.batch.draw(t, (float)(b.getX()-t.getRegionWidth()/2.0)+mg.camShakeX, (float)(b.getY()-t.getRegionHeight()/2.0)+mg.camShakeY, (float)(t.getRegionWidth()/2.0), (float)(t.getRegionHeight()/2.0), t.getRegionWidth(), t.getRegionHeight(), 1, 1, (float)Math.toDegrees(b.getRotation()));
                }
            }
            for(Tank t : tanks.values())
            {
                //if(updateTimeout > 100 && !gameEnded)
                {
                    t.update((int)(delta*1000));
                }
                TextureRegion toDraw = mg.sprites.get("tank"+t.getTeam());
                if(t.isDead())
                {
                    toDraw = mg.sprites.get("deadtank");
                    t.setHealth(0);
                    t.explosionStateTime += delta;
                }
                else
                {
                    if(Math.abs(t.getX()-t.initX) < 85 && Math.abs(t.getY()-t.initY) < 95 && t.getHealth() < Tank.MAX_HEALTH)
                    {
                        t.setHealth(t.getHealth()+delta*50);
                        particles.add(new Particle(mg.sprites.get("wall"),2,500,
                                t.getX()+(Math.random()*Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()-Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()/2),
                                t.getY()+(Math.random()*Tank.imgScale*mg.sprites.get("tank1").getRegionHeight()-Tank.imgScale*mg.sprites.get("tank1").getRegionHeight()/2),
                                0,0.05,false));
                    }
                }
                int radius = 24;//(int)(Tank.imgScale*toDraw.getRegionWidth()/2+3);
                boolean dug = false;
                if(phase == 0) //digging a circle around the tank. println all chords in the circle of 1 pixel width.
                {
                    for(int sX = (int)(t.getX()-radius); sX < (int)(t.getX()+radius); sX++)
                    {
                        //double dist = Math.sqrt(radius*radius-(t.getX()-sX)*(t.getX()-sX));
                        for(int sY = (int)(t.getY()-radius); sY < (int)(t.getY()+radius); sY++)
                        {
                            if(mg.digMask[(int)(sX-t.getX()+radius)][(int)(sY-t.getY()+radius)])
                            {
                                int type = grid[sX][sY]%100;
                                if(type == 1)
                                {
                                    grid[sX][sY] = 0;
                                    int lifetime = (int)(Math.random()*100)+300;
                                    particles.add(new Particle(mg.sprites.get("dirt"+picGrid[sX][sY]), 2.7f, -0.01f, lifetime, sX, sY, Math.random()*0.1-0.05, Math.random()*0.1-0.05, 0, 0, 0.005*Math.signum(Math.random()-0.5), true));
                                    dug = true;
                                }
                            }
                        }
                    }
                }
                if(dug && t.getName().equals(mg.myTankName))
                {
                    digsoundtimer+=delta;
                    if(digsoundtimer >= 1/8.0)
                    {
                        mg.screenShake(1f, 0.07f);
                        mg.a.playSfx("dig2");
                        digsoundtimer = 0;
                    }
                }
                if(dug)
                {
                    t.setHealth(t.getHealth()-delta*7);
                }
                gl.setText(mg.font16, t.getName());
                mg.font16.setColor(Color.WHITE);
                int dist;
                //if(phase == 1)
                    //dist = 130;
                //else
                    dist = 235;
                if(t.getName().equals(mg.myTankName))
                {
                    mg.font16.draw(mg.batch, t.getName(), mg.cam.position.x-gl.width/2+mg.camShakeY, mg.cam.position.y-Tank.imgScale*toDraw.getRegionWidth()/2+mg.camShakeX);
                    TextureRegion hpb = mg.sprites.get("healthoutline");
                    TextureRegion hp = mg.sprites.get("healthbar");
                    mg.batch.draw(hpb, mg.cam.position.x-hpb.getRegionWidth()/2+mg.camShakeX, mg.cam.position.y+Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()/2+mg.camShakeY);
                    mg.batch.draw(hp, mg.cam.position.x-hp.getRegionWidth()/2+mg.camShakeX, mg.cam.position.y+Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()/2+1+mg.camShakeY, (float)(hp.getRegionWidth()*t.getHealth()/Tank.MAX_HEALTH), hp.getRegionHeight());
                    mg.batch.draw(toDraw, mg.cam.position.x-Tank.imgScale*toDraw.getRegionWidth()/2+mg.camShakeX, mg.cam.position.y-Tank.imgScale*toDraw.getRegionHeight()/2+mg.camShakeY, toDraw.getRegionWidth()*Tank.imgScale/2, toDraw.getRegionHeight()*Tank.imgScale/2, toDraw.getRegionWidth()*Tank.imgScale, toDraw.getRegionHeight()*Tank.imgScale, 1, 1, (int) Math.toDegrees(t.getRotation()));
                    if(t.isDead())
                    {
                        //TextureRegion e = mg.explosion.getKeyFrame(t.explosionStateTime);
                        //mg.batch.draw(e, mg.cam.position.x-e.getRegionWidth()/2+mg.camShakeX, mg.cam.position.y-e.getRegionHeight()/2+mg.camShakeY);
                    }
                }
                else if(Math.sqrt(Math.pow(t.getX()-mg.cam.position.x,2) + Math.pow(t.getY()-mg.cam.position.y,2)) < dist &&
                        (phase == 0 || tanks.get(mg.myTankName).isVisible(t.getName())))
                {
                    mg.font16.draw(mg.batch, t.getName(), (float)t.getX()-gl.width/2+mg.camShakeY, (float)t.getY()-Tank.imgScale*toDraw.getRegionWidth()/2+mg.camShakeX);
                    TextureRegion hpb = mg.sprites.get("healthoutline");
                    TextureRegion hp = mg.sprites.get("healthbar");
                    mg.batch.draw(hpb, (float)t.getX()-hpb.getRegionWidth()/2+mg.camShakeX, (float)t.getY()+Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()/2+mg.camShakeY);
                    mg.batch.draw(hp, (float)t.getX()-hp.getRegionWidth()/2+mg.camShakeX, (float)t.getY()+Tank.imgScale*mg.sprites.get("tank1").getRegionWidth()/2+1+mg.camShakeY, (float)(hp.getRegionWidth()*t.getHealth()/Tank.MAX_HEALTH), hp.getRegionHeight());
                    mg.batch.draw(toDraw, (float)t.getX()-Tank.imgScale*toDraw.getRegionWidth()/2+mg.camShakeX, (float)t.getY()-Tank.imgScale*toDraw.getRegionHeight()/2+mg.camShakeY, toDraw.getRegionWidth()*Tank.imgScale/2, toDraw.getRegionHeight()*Tank.imgScale/2, toDraw.getRegionWidth()*Tank.imgScale, toDraw.getRegionHeight()*Tank.imgScale, 1, 1, (int) Math.toDegrees(t.getRotation()));
                    if(t.isDead())
                    {
                        //TextureRegion e = mg.explosion.getKeyFrame(t.explosionStateTime);
                        //mg.batch.draw(e, (float)t.getX()-e.getRegionWidth()/2+mg.camShakeX, (float)t.getY()-e.getRegionHeight()/2+mg.camShakeY);
                    }
                }
            }
            for(int i = 0; i < particles.size(); i++)
            {
                Particle p = particles.get(i);
                if(Math.sqrt(Math.pow(p.x-mg.cam.position.x,2) + Math.pow(p.y-mg.cam.position.y,2)) < 235)
                {
                    mg.batch.setColor(1f,1f,1f,p.alpha);
                    mg.batch.draw(p.img, (float)p.x-p.texSize/2+mg.camShakeX, (float)p.y-p.texSize/2+mg.camShakeY, p.texSize/2, p.texSize/2, p.texSize, p.texSize, 1, 1, (int)Math.toDegrees(p.rotation));
                }
            }
            mg.batch.setColor(1f,1f,1f,1f);
            //if(phase == 0)
                mg.batch.draw(mg.sprites.get("vision1"), (float)mg.cam.position.x-mg.sprites.get("vision1").getRegionWidth()/2+mg.camShakeX, (float)mg.cam.position.y-mg.sprites.get("vision1").getRegionHeight()/2+mg.camShakeY);
            //else
                //mg.batch.draw(mg.sprites.get("vision2"), (float)mg.cam.position.x-mg.sprites.get("vision2").getRegionWidth()/2+mg.camShakeX, (float)mg.cam.position.y-mg.sprites.get("vision2").getRegionHeight()/2+mg.camShakeY);
        }
        if(phaseChanging)
        {
            if(phaseTimer == 3000)
            {
                messages.add(0, new GameMessage("Digging phase is over. Get ready to fight!", Color.CYAN));
                mg.a.playSfx("msg");
            }
            phaseTimer -= delta*1000;
            float alpha;
            if(phaseTimer > 2000)
                alpha = (3000-phaseTimer)/1000;
            else if(phaseTimer > 1000)
            {
                alpha = 1;
                if(!phaseChangeHit)
                {
                    phaseChangeHit = true;
                    for(Tank t : tanks.values()) {
                        t.setX(t.initX);
                        t.setY(t.initY);
                    }
                    phase = 1;
                    Tank.imgScale = 1.5f;
                    input.reset();
                    for(int i = 0; i < grid.length; i++)
                    {
                        for(int j = 0; j < grid[i].length; j++)
                        {
                            int type = grid[i][j]%100;
                            if(i%10 == 0 && j%10 == 0 && type == 0) {
                                grid[i][j] = 100+type;
                            }
                        }
                    }
                }
            }
            else
                alpha = 1-((1000-phaseTimer)/1000);
            if(phaseTimer < 0)
                phaseChanging = false;
            else
            {
                mg.batch.setColor(0f, 0f, 0f, alpha);
                mg.batch.draw(mg.sprites.get("black"), mg.cam.position.x-TestGame.VIEW_WIDTH/2+mg.camShakeX, mg.cam.position.y-TestGame.VIEW_HEIGHT/2+mg.camShakeY, TestGame.VIEW_WIDTH, TestGame.VIEW_HEIGHT);
            }
        }
        mg.font16.setColor(Color.WHITE);
        gl.setText(mg.font16, "FPS: "+(int)(1/delta));
        mg.font16.draw(mg.batch, "FPS: "+(int)(1/delta), mg.cam.position.x+TestGame.VIEW_WIDTH/2-gl.width-5+mg.camShakeY, mg.cam.position.y-TestGame.VIEW_HEIGHT/2+16+mg.camShakeX);
        String phs;
        if(phase == 0)
            phs = "digging";
        else
            phs = "fighting";
        if(gameTime <= 10)
            mg.font16.setColor(Color.RED);
        mg.font16.draw(mg.batch, (int)gameTime + " seconds left in " + phs + " phase.", mg.cam.position.x-TestGame.VIEW_WIDTH/2+5+mg.camShakeY, mg.cam.position.y+TestGame.VIEW_HEIGHT/2-5+mg.camShakeX);
        while(messages.size() > 12)
            messages.remove(messages.size()-1);
        for(int i = 0; i < messages.size(); i++)
        {
            GameMessage g = messages.get(i);
            float drawX = mg.cam.position.x-TestGame.VIEW_WIDTH/2+5;
            String test = "";
            for(int j = 0; j < g.message.size(); j++)
            {
                String s = g.message.get(j);
                Color c = g.color.get(j);
                gl.setText(mg.font16, s);
                mg.font16.setColor(c);
                mg.font16.draw(mg.batch, s, drawX+mg.camShakeY, mg.cam.position.y-TestGame.VIEW_HEIGHT/2+(i*(gl.height+4))+16+mg.camShakeX);
                drawX += gl.width-7;
                test += s;
            }
            gl.setText(mg.font16, test);
        }
        if(initAlpha != 0)
        {
            initAlpha += delta*initAlphaDir;
            if(initAlpha < 0)
                initAlpha = 0;
            else if(initAlpha > 1.0f)
            {
                initAlpha = 1.0f;
                initAlphaDir = -1;
                try {
                    mg.socket = new Socket(mg.myIP, mg.myPort);
                    mg.in = new BufferedReader(new InputStreamReader(mg.socket.getInputStream()));
                    mg.out = new PrintWriter(mg.socket.getOutputStream(), true);
                    mg.myHandlerHashCode = mg.in.readLine();
                    mg.out.println(mg.myTankName + "/" + mg.myHandlerHashCode);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                mg.setScreen(new MenuScreen(mg));
            }
            mg.batch.setColor(0f, 0f, 0f, initAlpha);
            mg.batch.draw(mg.sprites.get("black"), mg.cam.position.x-TestGame.VIEW_WIDTH/2+mg.camShakeX, mg.cam.position.y-TestGame.VIEW_HEIGHT/2+mg.camShakeY, TestGame.VIEW_WIDTH, TestGame.VIEW_HEIGHT);
        }
        mg.batch.end();
    }
    
    //Client sided implementation of visibility
    public boolean isVisible(double x1, double y1)
    {
        double startX = tanks.get(mg.myTankName).getX(), startY = tanks.get(mg.myTankName).getY(), endX = x1, endY = y1;
        double theta = Math.atan2(endX-startX, endY-startY);
        theta -= Math.PI/2;
        theta = -theta;
        double xInc = Math.cos(theta);
        double yInc = Math.sin(theta);
        while(visibilityHelper(startX, startY, endX, endY, xInc, yInc))
        {
            int type = grid[(int)startX][(int)startY]%100;
            if(type != 0)
            {
                return false;
            }
            startY += yInc;
            startX += xInc;
        }
        return true;
    }
    
    public boolean visibilityHelper(double sX, double sY, double eX, double eY, double xInc, double yInc)
    {
        if(xInc > 0) {
            if(yInc > 0) {
                if(sX < eX || sY < eY) {
                    return true;
                }
            }
            else {
                if(sX < eX || sY > eY) {
                    return true;
                }
            }
        } else {
            if(yInc > 0) {
                if(sX > eX || sY < eY) {
                    return true;
                }
            }
            else {
                if(sX > eX || sY > eY) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height)
    {
        /*mg.cam.viewportWidth = TestGame.VIEW_WIDTH;
        mg.cam.viewportHeight = TestGame.VIEW_HEIGHT;*/
        mg.stage.getViewport().update(width, height);
    }

    @Override
    public void pause()
    {
        
    }

    @Override
    public void resume()
    {
        
    }

    @Override
    public void hide() {
        
    }

    @Override
    public void dispose()
    {
         
    }
}