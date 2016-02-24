package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.game.ButtonHero;

import java.io.IOException;

public class DesktopLauncher {
    public static void main(String[] arg) throws IOException {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        TexturePacker.process(settings, "textures", "packed", "game");
		TexturePacker.process(settings, "uistuff", "packed", "ui");


		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tohsu!";
		config.width = 1920;
		config.height = 1080;
		config.resizable = false;
		config.vSyncEnabled = true;
		config.foregroundFPS = 120;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//		config.fullscreen = true;
		new LwjglApplication(new ButtonHero(), config);
	}
}
