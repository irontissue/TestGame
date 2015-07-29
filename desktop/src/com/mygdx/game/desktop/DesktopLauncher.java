package com.mygdx.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.TestGame;

public class DesktopLauncher{
    public static LwjglApplication app;
    
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = TestGame.VIEW_WIDTH;
        config.height = TestGame.VIEW_HEIGHT;
        //config.resizable = false;
        config.foregroundFPS = 50;
        config.backgroundFPS = 50;
        config.initialBackgroundColor = Color.BLACK;
        config.title = "Tunneler M";
        config.addIcon("icon32.png", Files.FileType.Internal);
        config.addIcon("icon16.png", Files.FileType.Internal);
        config.addIcon("icon128.png", Files.FileType.Internal);
        app = new LwjglApplication(new TestGame(), config);
    }
}
