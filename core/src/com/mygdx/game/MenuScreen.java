/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author Ashok
 */
public class MenuScreen implements Screen
{
    private TestGame mg;
    private float fading = 1000;
    private float titleSize = 1.0f, titleDir = 0.0002f;
    private int fadeDir = -1;
    private float alpha = 1f;
    private float backgroundY = 0;
    private float namebtnWidth, namebtnHeight;
    
    private Texture background, help;
    private TextureRegion logo;
    private Dialog d;
    
    private String cool;
    
    private boolean onebtn = false, threebtn = false, fivebtn = false, namebtn = false;
    private boolean helping = false;
    
    private TextButton helpBtn, musicBtn, sfxBtn, creditsBtn;
    
    private GlyphLayout gl;
    
    public MenuScreen(TestGame g)
    {
        mg = g;
        mg.a.playMusic("title", 0.7f);
        Tank.imgScale = 3;
        Gdx.input.setInputProcessor(mg.stage);
        background = new Texture(Gdx.files.internal("dirtBackground.png"));
        help = new Texture(Gdx.files.internal("help.png"));
        logo = mg.sprites.get("logo");
        creditsBtn = new TextButton("Credits", mg.skin2);
        creditsBtn.setPosition((TestGame.VIEW_WIDTH-help.getWidth())/2+11, TestGame.VIEW_HEIGHT-(TestGame.VIEW_HEIGHT-help.getHeight())/2-creditsBtn.getHeight()-12);
        creditsBtn.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    if(creditsBtn.getText().toString().equals("Credits"))
                    {
                        creditsBtn.setText("Help");
                        mg.a.playSfx("click");
                        help = new Texture(Gdx.files.internal("credits.png"));
                    }
                    else
                    {
                        creditsBtn.setText("Credits");
                        mg.a.playSfx("click");
                        help = new Texture(Gdx.files.internal("help.png"));
                    }
                }
            });
        helpBtn = new TextButton("Help", mg.skin2);
        helpBtn.setPosition(TestGame.VIEW_WIDTH-helpBtn.getWidth()-5, TestGame.VIEW_HEIGHT-helpBtn.getHeight()-5);
        helpBtn.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    mg.a.playSfx("click");
                    helping = !helping;
                    if(helping)
                    {
                        helpBtn.setText("Close");
                        mg.stage.addActor(creditsBtn);
                    }
                    else
                    {
                        helpBtn.setText("Help");
                        help = new Texture(Gdx.files.internal("help.png"));
                        creditsBtn.remove();
                        creditsBtn.setText("Credits");
                    }
                }
            });
        musicBtn = new TextButton("Music", mg.skin2);
        musicBtn.setPosition(TestGame.VIEW_WIDTH-musicBtn.getWidth()-5, TestGame.VIEW_HEIGHT-musicBtn.getHeight()-10-helpBtn.getHeight());
        musicBtn.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    if(mg.a.getMusicVolume() == 1f)
                        mg.a.setMusicVolume(0f);
                    else
                        mg.a.setMusicVolume(1f);
                    mg.a.playSfx("click");
                }
            });
        sfxBtn = new TextButton("SFX", mg.skin2);
        sfxBtn.setPosition(TestGame.VIEW_WIDTH-sfxBtn.getWidth()-5, TestGame.VIEW_HEIGHT-sfxBtn.getHeight()-15-helpBtn.getHeight()-musicBtn.getHeight());
        sfxBtn.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    if(mg.a.getSfxVolume() == 1f)
                        mg.a.setSfxVolume(0f);
                    else
                        mg.a.setSfxVolume(1f);
                    mg.a.playSfx("click");
                }
            });
        mg.stage.clear();
        mg.stage.addActor(helpBtn);
        mg.stage.addActor(musicBtn);
        mg.stage.addActor(sfxBtn);
        mg.cam.position.set(TestGame.VIEW_WIDTH/2, TestGame.VIEW_HEIGHT/2, 0);
        mg.cam.update();
        gl = new GlyphLayout();
    }
    
    @Override
    public void show() {
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        mg.batch.setProjectionMatrix(mg.cam.combined);
        mg.batch.begin();
        backgroundY += delta*20;
        if(backgroundY > TestGame.VIEW_HEIGHT)
            backgroundY = 0;
        /*titleSize+=titleDir;
        if(titleSize > 1.05){
            titleSize = 1.05f;
            titleDir = -0.0002f;
        } else if (titleSize < 0.95) {
            titleSize = 0.95f;
            titleDir = 0.0002f;
        }*/ //MAKES TITLE LOGO ZOOM IN AND OUT SLOWLY (looks weird tho)
        if(fading >= 0)
        {
            fading += delta*1000*fadeDir;
            if(fading < 0)
                fading = 0;
            else if(fading > 1000)
            {
                fading = 1000;
                fadeDir = -1;
                if(onebtn)
                {
                    mg.out.println("1V1");
                    mg.setScreen(new GameScreen(mg, 0));
                }
                else if(threebtn)
                {
                    mg.out.println("3V3");
                    mg.setScreen(new GameScreen(mg, 1));
                }
                else if(fivebtn)
                {
                    mg.out.println("5V5");
                    mg.setScreen(new GameScreen(mg, 2));
                }
                else if(namebtn)
                {
                    mg.resetConnection();
                }
            }
            alpha = fading/1000;
        }
        if(fading == 0)
        {
            if(Gdx.input.justTouched())
            {
                float x = Gdx.input.getX()*((float)TestGame.VIEW_WIDTH/Gdx.graphics.getWidth());    //scaling to screen stretch
                float y = TestGame.VIEW_HEIGHT-Gdx.input.getY()*((float)TestGame.VIEW_HEIGHT/Gdx.graphics.getHeight());  //scaling to screen stretch
                if(x < TestGame.VIEW_WIDTH/3 && y < TestGame.VIEW_HEIGHT/6)
                {
                    onebtn = true;
                    fadeDir = 1;
                    mg.a.playSfx("click");
                }
                else if(x < TestGame.VIEW_WIDTH*2/3 && y < TestGame.VIEW_HEIGHT/6)
                {
                    threebtn = true;
                    fadeDir = 1;
                    mg.a.playSfx("click");
                }
                else if(x <= TestGame.VIEW_WIDTH && y < TestGame.VIEW_HEIGHT/6)
                {
                    fivebtn = true;
                    fadeDir = 1;
                    mg.a.playSfx("click");
                }
                else if(x < namebtnWidth && y > TestGame.VIEW_HEIGHT-namebtnHeight)
                {
                    namebtn = true;
                    fadeDir = 1;
                    mg.a.playSfx("click");
                }
            }
        }
        mg.batch.setColor(1f, 1f, 1f, 1f);
        mg.batch.draw(background, 0, backgroundY, TestGame.VIEW_WIDTH, background.getHeight());
        mg.batch.draw(background, 0, backgroundY-TestGame.VIEW_HEIGHT, TestGame.VIEW_WIDTH, background.getHeight());
        mg.font32.setColor(1f, 1f, 1f, 1f);
        mg.font16.setColor(1f, 1f, 1f, 1f);
        mg.batch.draw(mg.sprites.get("shadow"), 0, 0, TestGame.VIEW_WIDTH/3-1, TestGame.VIEW_HEIGHT/6);
        mg.batch.draw(mg.sprites.get("shadow"), TestGame.VIEW_WIDTH/3, 0, TestGame.VIEW_WIDTH/3-1, TestGame.VIEW_HEIGHT/6);
        mg.batch.draw(mg.sprites.get("shadow"), TestGame.VIEW_WIDTH*2/3, 0, TestGame.VIEW_WIDTH/3, TestGame.VIEW_HEIGHT/6);
        gl.setText(mg.font16, "Playing as; " + mg.myTankName);
        namebtnWidth = gl.width+10;
        namebtnHeight = gl.height+10;
        mg.batch.draw(mg.sprites.get("shadow"), 0, TestGame.VIEW_HEIGHT-gl.height-10, gl.width+10, gl.height+10);
        mg.font16.draw(mg.batch, "Playing as: " + mg.myTankName, 5, TestGame.VIEW_HEIGHT-5);
        gl.setText(mg.font32, "1 VS 1");
        mg.font32.draw(mg.batch, "1 VS 1", TestGame.VIEW_WIDTH/6-gl.width/2, TestGame.VIEW_HEIGHT/12+gl.height/2);
        gl.setText(mg.font32, "3 VS 3");
        mg.font32.draw(mg.batch, "3 VS 3", TestGame.VIEW_WIDTH/2-gl.width/2, TestGame.VIEW_HEIGHT/12+gl.height/2);
        gl.setText(mg.font32, "5 VS 5");
        mg.font32.draw(mg.batch, "5 VS 5", TestGame.VIEW_WIDTH*5/6-gl.width/2, TestGame.VIEW_HEIGHT/12+gl.height/2);
        mg.batch.draw(mg.sprites.get("logo"), TestGame.VIEW_WIDTH/2-mg.sprites.get("logo").getRegionWidth()*titleSize/2, 360, mg.sprites.get("logo").getRegionWidth()*titleSize, mg.sprites.get("logo").getRegionHeight()*titleSize);
        if(TestGame.VIEW_WIDTH > 700)
        {
            mg.batch.draw(mg.sprites.get("tank0"), 100, 140, 238, 170);                         //800x480 version
            mg.batch.draw(mg.sprites.get("tank1"), 462, 140, 119, 85, 238, 170, 1, 1, 180);     //800x480 version
        }
        else
        {
            mg.batch.draw(mg.sprites.get("tank0"), 50, 150, 210, 150);                          //600x500 version
            mg.batch.draw(mg.sprites.get("tank1"), 340, 150, 105, 75, 210, 150, 1, 1, 180);     //600x500 version
        }
        mg.batch.setColor(0f,0f,0f,1f);
        if(onebtn)
            mg.batch.draw(mg.sprites.get("button"), 1, 1, TestGame.VIEW_WIDTH/3, TestGame.VIEW_HEIGHT/6);
        if(threebtn)
            mg.batch.draw(mg.sprites.get("button"), TestGame.VIEW_WIDTH/3-1, 1, TestGame.VIEW_WIDTH/3+1, TestGame.VIEW_HEIGHT/6);
        if(fivebtn)
            mg.batch.draw(mg.sprites.get("button"), TestGame.VIEW_WIDTH*2/3-1, 1, TestGame.VIEW_WIDTH/3+1, TestGame.VIEW_HEIGHT/6);
        if(namebtn)
            mg.batch.draw(mg.sprites.get("button"), 0, TestGame.VIEW_HEIGHT-namebtnHeight, namebtnWidth, namebtnHeight);
        mg.batch.setColor(1f, 1f, 1f, 1f);
        if(helping)
            mg.batch.draw(help, (TestGame.VIEW_WIDTH-help.getWidth())/2, (TestGame.VIEW_HEIGHT-help.getHeight())/2);
        mg.batch.end();
        
        mg.stage.act();
        mg.stage.draw();
        
        mg.batch.begin();
        mg.batch.setColor(0f,0f,0f,alpha);
        mg.batch.draw(mg.sprites.get("black"), 0, 0, TestGame.VIEW_WIDTH, TestGame.VIEW_HEIGHT);
        mg.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        mg.stage.getViewport().update(width, height);
    } 

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        
    }

    @Override
    public void dispose() {
        background.dispose();
    }
    
}
