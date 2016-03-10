package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.File;

/**
 * Work in progress
 */
public class GameScreen implements Screen {
    static final int HEIGHT = 1080;

    static TextureRegion hitObject1, hitObject2, holdObject1;
    static String hitFlagString = "";
    static long visualOffsetMillis = 0;
    final ButtonHero game;
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private OrthographicCamera camera;
    private ShapeRenderer shapeTester;
    private BeatMap currentBeatMap;
    private TextureAtlas rhythmAtlas;
    private ScoreManager scoreManager;
    private BulletHell bulletHell;

    public GameScreen(final ButtonHero game) {
        this.game = game;
        rhythmAtlas = new TextureAtlas(Gdx.files.internal("packed/game.atlas"));
        hitObject1 = rhythmAtlas.findRegion("mania-note1");
        hitObject2 = rhythmAtlas.findRegion("mania-note2");
        holdObject1 = rhythmAtlas.findRegion("mania-note1");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        scoreManager = new ScoreManager();
        currentBeatMap = new BeatMap(new File("colors.txt"), scoreManager, game.uiskin);
        bulletHell = new BulletHell(1920/2, 1920);
        inputMultiplexer.addProcessor(game.uiStage);
        inputMultiplexer.addProcessor(currentBeatMap);
        Gdx.input.setInputProcessor(inputMultiplexer);
        shapeTester = new ShapeRenderer();
        currentBeatMap.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentBeatMap.update(delta);
        bulletHell.act();
//        game.uiStage.act(delta);
//        game.uiStage.draw();
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        shapeTester.setProjectionMatrix(camera.combined);
        shapeTester.begin(ShapeRenderer.ShapeType.Line);
        shapeTester.setColor(Color.WHITE);
        shapeTester.line(0, BeatMap.BAR_POSITION + 45, 1920, BeatMap.BAR_POSITION + 45);
        for (int i = 0; i < 3; i++) {
            shapeTester.line(BeatMap.XPOSITIONS[i + 1] + 25, 0, BeatMap.XPOSITIONS[i + 1] + 25, 1080);
        }
        shapeTester.end();
        currentBeatMap.draw();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        currentBeatMap.draw(game.batch);
        bulletHell.draw();
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        currentBeatMap.pause();
    }

    @Override
    public void resume() {
        currentBeatMap.resume();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.dispose();
        rhythmAtlas.dispose();
        currentBeatMap.dispose();
        shapeTester.dispose();
    }
}
