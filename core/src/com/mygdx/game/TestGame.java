package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class TestGame extends Game
{
    SpriteBatch batch;
    BitmapFont font32;
    BitmapFont font16;
    Stage stage;
    Dialog d;
    
    String myTankName;
    String myIP;
    
    int myPort;
    
    boolean[][] digMask = new boolean[48][48];
    
    Texture spriteSheet;
    Texture digMaskImg;
    
    Animation explosion;
    
    Skin skin, skin2;
    
    HashMap<String, TextureRegion> sprites;
    
    public static final int VIEW_WIDTH = 600, VIEW_HEIGHT = 500;
    public float camShakeX = 0, camShakeY = 0, maxCamShake = 0, shakeTime = 0, maxShakeTime = 0;
    
    BufferedReader in;
    
    PrintWriter out;
    
    OrthographicCamera cam;
    
    Socket socket;
    
    String myHandlerHashCode;
    
    AudioPlayer a;

    @Override
    public void create()
    {
        a = new AudioPlayer();
        batch = new SpriteBatch();
        cam = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
        cam.update();
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        //stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Gdx.input.setInputProcessor(stage);
        Gdx.graphics.setVSync(true);
        skin = new Skin(Gdx.files.internal("uiskin2.json"));
        skin2 = new Skin(Gdx.files.internal("uiskin.json"));
        font32 = new BitmapFont(Gdx.files.internal("fixedsys32.fnt"), Gdx.files.internal("fixedsys32_0.png"), false);
        font16 = new BitmapFont(Gdx.files.internal("fixedsys16.fnt"), Gdx.files.internal("fixedsys16_0.png"), false);
        spriteSheet = new Texture(Gdx.files.internal("sprites.png"));
        sprites = new HashMap();
        sprites.put("vision2", new TextureRegion(spriteSheet, 0, 0, 350, 350));
        sprites.put("vision1", new TextureRegion(spriteSheet, 350, 0, 512, 512));
        sprites.put("logo", new TextureRegion(spriteSheet, 350, 512, 450, 90));
        sprites.put("tank0", new TextureRegion(spriteSheet, 0, 350, 14, 10));
        sprites.put("bluewall", new TextureRegion(sprites.get("tank0"),0,0,1,1));
        sprites.put("tank1", new TextureRegion(spriteSheet, 0, 360, 14, 10));
        sprites.put("greenwall", new TextureRegion(sprites.get("tank1"),0,0,1,1));
        sprites.put("deadtank", new TextureRegion(spriteSheet, 0, 370, 14, 10));
        sprites.put("dirt0", new TextureRegion(spriteSheet, 14, 350, 1, 1));
        sprites.put("dirt1", new TextureRegion(spriteSheet, 15, 350, 1, 1));
        sprites.put("dirt2", new TextureRegion(spriteSheet, 14, 351, 1, 1));
        sprites.put("dirt3", new TextureRegion(spriteSheet, 15, 351, 1, 1));
        sprites.put("dugdirt0", new TextureRegion(spriteSheet, 14, 352, 1, 1));
        sprites.put("dugdirt1", new TextureRegion(spriteSheet, 15, 352, 1, 1));
        sprites.put("dugdirt2", new TextureRegion(spriteSheet, 14, 353, 1, 1));
        sprites.put("dugdirt3", new TextureRegion(spriteSheet, 15, 353, 1, 1));
        sprites.put("black", new TextureRegion(spriteSheet, 16, 350, 1, 1));
        sprites.put("wall", new TextureRegion(spriteSheet, 16, 351, 1, 1));
        sprites.put("bullet1", new TextureRegion(spriteSheet, 14, 354, 6, 2));
        sprites.put("bullet2", new TextureRegion(spriteSheet, 14, 356, 6, 6));
        sprites.put("shadow", new TextureRegion(spriteSheet, 17, 350, 1, 1));
        sprites.put("healthoutline", new TextureRegion(spriteSheet, 20, 350, 32, 7));
        sprites.put("healthbar", new TextureRegion(spriteSheet, 52, 350, 30, 5));
        sprites.put("button", new TextureRegion(spriteSheet, 20, 357, 200, 82));
        batch.begin();
        for(TextureRegion tr : sprites.values()) {
            batch.draw(tr, -999999, -999999);
        }
        batch.end();
        Texture explosionSheet = new Texture(Gdx.files.internal("explosion.png"));
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, explosionSheet.getWidth()/8, explosionSheet.getHeight()/5);
        TextureRegion[] explosionRegions = new TextureRegion[40];
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                explosionRegions[index++] = tmp[i][j];
            }
        }
        explosionRegions[30] = new TextureRegion(explosionRegions[0], 0, 0, 1, 1);
        explosion = new Animation(0.02f, explosionRegions);
        digMaskImg = new Texture(Gdx.files.internal("dig2.png"));
        if(!digMaskImg.getTextureData().isPrepared())
            digMaskImg.getTextureData().prepare();
        Pixmap pm = digMaskImg.getTextureData().consumePixmap();
        for(int i = 0; i < pm.getWidth(); i++)
        {
            for(int j = 0; j < pm.getHeight(); j++)
            {
                Color c = new Color();
                Color.rgba8888ToColor(c, pm.getPixel(i, j));
                if(c.r == 0)
                    digMask[i][j] = true;
            }
        }
        pm.dispose();
        digMaskImg.dispose();
        initConnect();
    }
    
    public void resetConnection()
    {
        stage.clear();
        setScreen(null);
        try{
            out.close();
            in.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initConnect();
    }
    
    public void initConnect()
    {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("70.114.212.166", 8880), 3000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            myPort = Integer.parseInt(in.readLine());
            out.close();
            in.close();
            socket.close();
            socket = new Socket();
            socket.connect(new InetSocketAddress("70.114.212.166", myPort), 3000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            myHandlerHashCode = in.readLine();
            myIP = "70.114.212.166";
            login("Enter a name!");
        } catch (Exception ex) {
            ex.printStackTrace();
            connect("Couldn't connect. Enter server IP: ");
        }
    }
    
    public void connect(final String text)
    {
        final TextField f = new TextField("70.114.212.166", skin);
        d = new Dialog("Connect", skin){
            {
                setMovable(false);
                getTitleLabel().setAlignment(Align.center);
                getContentTable().add(text);
                getContentTable().row();
                getContentTable().add(f);
                button("Connect", 1);
                button("Cancel", 2);
            }

            @Override
            public void result(Object o)
            {
                a.playSfx("click");
                if((Integer)o == 1)
                {
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(f.getText(), 8880), 3000);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);
                        myPort = Integer.parseInt(in.readLine());
                        out.close();
                        in.close();
                        socket.close();
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(f.getText(), myPort), 3000);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);
                        myHandlerHashCode = in.readLine();
                        myIP = f.getText();
                        login("Enter a name!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        connect("Couldn't connect. Enter server IP:");
                    }
                }
                else
                {
                    Gdx.app.exit();
                }
            }
        }.show(stage);
    }
    
    public void login(final String text)
    {
        final TextField user = new TextField("", skin);
        user.setMaxLength(20);
        d = new Dialog("Name select", skin){
            {
                setMovable(false);
                getTitleLabel().setAlignment(Align.center);
                Label l = new Label(text, skin);
                l.setWidth(100);
                l.setWrap(true);
                getContentTable().add(text).padTop(5);
                getContentTable().row();
                Table t = new Table(skin);
                t.add("Name:");
                t.add(user).width(150).padLeft(10);
                getContentTable().add(t);
                button("Go!", 1);
                button("Cancel", 2);
            }
            
            @Override
            public void result(Object o)
            {
                a.playSfx("click");
                if((Integer)o == 1)
                {
                    try {
                        if(user.getText().trim().equals(""))
                        {
                            out.println("null");
                        }
                        else
                        {
                            out.println(user.getText());
                        }
                        String line = in.readLine();
                        if(line.startsWith("SUBMITNAME"))
                        {
                            String msg = "";
                            if(line.split("/")[1].equals("0"))
                                msg = "Invalid name. Try again!";
                            else
                                msg = "That name is currently being used. Try again!";
                            login(msg);
                        }
                        else
                        {
                            myTankName = line.split("/",2)[1];
                            setScreen(new MenuScreen(TestGame.this));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    Gdx.app.exit();
                }
            }
        }.show(stage);
    }
    
    public void screenShake(float amt, float duration)
    {
        camShakeX = amt;
        camShakeY = amt;
        maxCamShake = amt;
        shakeTime = duration;
        maxShakeTime = duration;
    }
    
    public void shake(float delta)
    {
        if(shakeTime > 0)
        {
            double a = Math.random()*3-1; int r1;
            if(a < 0)
                r1 = -1;
            else
                r1 = 1;
            double b = Math.random()*3-1; int r2;
            if(b < 0)
                r2 = -1;
            else
                r2 = 1;
            shakeTime -= delta;
            if(shakeTime < 0)
                shakeTime = 0;
            camShakeX = maxCamShake*(shakeTime/maxShakeTime)*r1;
            camShakeY = maxCamShake*(shakeTime/maxShakeTime)*r2;
        }
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage.act();
        stage.draw();
        
        super.render();
    }
    
    @Override
    public void dispose()
    {
        batch.dispose();
        font32.dispose();
        font16.dispose();
        spriteSheet.dispose(); 
        a.dispose();
        try{
            getScreen().dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try{
            out.close();
            in.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
