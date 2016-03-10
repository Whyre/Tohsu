package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ButtonHero extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public Stage uiStage;
	public Skin uiskin;
	private TextureAtlas uiatlas;


	public void create() {
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		uiStage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		uiatlas = new TextureAtlas(Gdx.files.internal("packed/ui.atlas"));
		uiskin = new Skin(Gdx.files.internal("packed/ui.json"), uiatlas);
		font.getData().setScale(7);
		font.setColor(Color.WHITE);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render(); //important!
	}

	public void dispose() {
		uiskin.dispose();
		uiStage.dispose();
		batch.dispose();
		font.dispose();
	}
}
