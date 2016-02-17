package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
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

    public MainMenuScreen(ButtonHero game) {
        Table table = new Table(game.uiskin);
        table.setFillParent(true);
        table.pad(100);
        TextButton testButton = new TextButton("Start Game!", game.uiskin);
        table.add(testButton);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                for (Actor a : game.uiStage.getActors()) {
                    a.remove();
                }
                game.uiStage.dispose();
                dispose();
            }
        });
        table.row();
        TextButton exitButton = new TextButton("Exit!", game.uiskin);
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
