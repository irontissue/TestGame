/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.TestGame;

/**
 *
 * @author Ashok
 */
public class DesktopApplet extends LwjglApplet
{
    private static final long serialVersionUID = 1L;
    
    private static final LwjglApplicationConfiguration config = 
        new LwjglApplicationConfiguration() {
        {
            width = TestGame.VIEW_WIDTH;
            height = TestGame.VIEW_HEIGHT;
            //resizable = false;
            foregroundFPS = 50;
            backgroundFPS = 50;
            initialBackgroundColor = Color.BLACK;
        }
    };
    
    public DesktopApplet() {
        super(new TestGame(), config);
    }
    
    public static void main (String[] arg) {
        new DesktopApplet();
    }
}
