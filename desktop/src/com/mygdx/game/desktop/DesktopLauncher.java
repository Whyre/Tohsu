package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.ButtonHero;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tohsu!";
		config.width = 1920;
		config.height = 1080;
		config.resizable = false;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//		config.fullscreen = true;
		new LwjglApplication(new ButtonHero(), config);
	}
}
