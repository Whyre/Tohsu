package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Created by William on 1/30/2016.
 */
public class MainMenuScreen implements Screen {
    final ButtonHero game;

    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private OrthographicCamera camera;
    private boolean startGame;

    public MainMenuScreen(ButtonHero game) {
        TextureAtlas uiatlas = new TextureAtlas(Gdx.files.internal("packed/ui.atlas"));
        Skin uiskin = new Skin(Gdx.files.internal("packed/ui.json"), uiatlas);
        Table table = new Table(uiskin);
        table.setFillParent(true);
        table.pad(100);
        TextButton testButton = new TextButton("Start Game!", uiskin);
        table.add(testButton);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame = true;
                game.setScreen(new GameScreen(game));
                game.uiStage.clear();
                dispose();
            }
        });
        table.row();
        TextButton exitButton = new TextButton("Exit!", uiskin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.dispose();
                Gdx.app.exit();
            }
        });
        table.add(exitButton);
        game.uiStage.addActor(table);
        this.game = game;
        inputMultiplexer.addProcessor(game.uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.uiStage.act(delta);
        game.uiStage.draw();
        camera.update();
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
