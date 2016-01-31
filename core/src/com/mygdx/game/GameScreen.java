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
    static final int[] XPOSITIONS = {50, 250, 450, 650};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 200;
    static final int HIT_OBJECT_DISTANCE = YPOSITION - BAR_POSITION;

    static TextureRegion hitObject1, hitObject2, holdObject1;
    static int hitFlag = -1; //-1: do nothing, 0: perfect, 1: great, 2:bad, 3:miss
    static long visualOffsetMillis = 0;

    final ButtonHero game;
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private OrthographicCamera camera;
    private ShapeRenderer shapeTester;
    private BeatMap currentBeatMap;
    private TextureAtlas atlas;
    private float hitTimeElapsedMillis;

    public GameScreen(final ButtonHero game) {
        this.game = game;
        atlas = new TextureAtlas(Gdx.files.internal("packed/game.atlas"));
        hitObject1 = atlas.findRegion("mania-note1");
        hitObject2 = atlas.findRegion("mania-note2");
        holdObject1 = atlas.findRegion("mania-note1");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        currentBeatMap = new BeatMap(new File("colors.txt"));
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
        currentBeatMap.update();
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        shapeTester.setProjectionMatrix(camera.combined);
        shapeTester.begin(ShapeRenderer.ShapeType.Line);
        shapeTester.setColor(Color.WHITE);
        shapeTester.line(0, BAR_POSITION + 45, 1920, BAR_POSITION + 45);
        for (int i = 0; i < 3; i++) {
            shapeTester.line(XPOSITIONS[i + 1] + 25, 0, XPOSITIONS[i + 1] + 25, 1080);
        }
        shapeTester.end();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        currentBeatMap.draw(game.batch);
        if (hitFlag >= 0) {
            switch (hitFlag) {
                case 0:
                    game.font.draw(game.batch, "PERFECT!", 450, 500);
                    break;
                case 1:
                    game.font.draw(game.batch, "GREAT!", 450, 500);
                    break;
                case 2:
                    game.font.draw(game.batch, "BAD!", 450, 500);
                    break;
                case 3:
                    game.font.draw(game.batch, "MISS!", 450, 500);
                    break;
            }
            hitTimeElapsedMillis += Gdx.graphics.getDeltaTime() * 1000;
        }

        if (hitTimeElapsedMillis > 300) {
            hitFlag = -1;
            hitTimeElapsedMillis = 0;
        }
//        game.font.draw(game.batch, accuracy / 100 + "." + accuracy % 100, 100, 800);
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
        game.dispose();
        atlas.dispose();
        currentBeatMap.dispose();
        shapeTester.dispose();
    }

    public void myDispose() {
        currentBeatMap.dispose();
//        shapeTester.dispose();
    }

    public BeatMap getCurrentBeatMap() {
        return currentBeatMap;
    }
}


