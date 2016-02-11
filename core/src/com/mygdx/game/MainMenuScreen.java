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
        TextButton testButton = new TextButton("test", uiskin);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame = true;
                game.setScreen(new GameScreen(game));
                game.uiStage.clear();
                dispose();
            }
        });
        game.uiStage.addActor(testButton);
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
        game.uiStage.act();
        game.uiStage.draw();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.batch.end();
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
