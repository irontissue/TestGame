/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

/**
 *
 * @author Ashok
 */
public class GameInput implements InputProcessor
{
    private GameScreen g;
    private TestGame t;
    private boolean l,r,u,d;
    
    public GameInput(TestGame tg, GameScreen mg)
    {
        g = mg;
        t = tg;
    }
    
    public void reset()
    {
        l = false;
        r = false;
        u = false;
        d = false;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if(!g.phaseChanging)
            if(keycode == Keys.LEFT)
            {
                t.out.println("KEYSTROKE/LEFT"); l = true;
            }
            else if(keycode == Keys.RIGHT)
            {
                t.out.println("KEYSTROKE/RIGHT"); r = true;
            }
            else if(keycode == Keys.UP)
            {
                t.out.println("KEYSTROKE/UP"); u = true;
            }
            else if(keycode == Keys.DOWN)
            {
                t.out.println("KEYSTROKE/DOWN"); d = true;
            }
            else if(keycode == Keys.Q)
            {
                t.out.println("KEYSTROKE/Q");
            }
            else if(keycode == Keys.X)
            {
                t.out.println("KEYSTROKE/SHOOT");
                if(g.tanks.get(t.myTankName).getHealth() > 5)
                    g.tanks.get(t.myTankName).setHealth(g.tanks.get(t.myTankName).getHealth()-5);
            }
            /*else if(keycode == Keys.W)
            {
                g.alphaInc += 0.01f;
                System.out.println(g.alphaInc);
            }
            else if(keycode == Keys.S)
            {
                g.alphaInc -= 0.01f;
                System.out.println(g.alphaInc);
            }*/
            /*else if(keycode == Keys.Z)
            {
                t.zoom += 0.02;
            }
            else if(keycode == Keys.X)
            {
                t.zoom -= 0.02;
            }*/
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(!g.phaseChanging)
            if(keycode == Keys.LEFT && l)
            {
                t.out.println("KEYSTROKE/LEFTRELEASED"); l = false;
            }
            else if(keycode == Keys.RIGHT && r)
            {
                t.out.println("KEYSTROKE/RIGHTRELEASED"); r = false;
            }
            else if(keycode == Keys.UP && u)
            {
                t.out.println("KEYSTROKE/UPRELEASED"); u = false;
            }
            else if(keycode == Keys.DOWN && d)
            {
                t.out.println("KEYSTROKE/DOWNRELEASED"); d = false;
            }
            else if(keycode == Keys.Q)
            {
                t.out.println("KEYSTROKE/QRELEASED");
            }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }
    
}
