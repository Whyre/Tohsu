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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.io.File;

/**
 * Work in progress
 */
public class GameScreen implements Screen {
    static final int[] XPOSITIONS = {50, 250, 450, 650};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 200;
    static final int HIT_OBJECT_DISTANCE = YPOSITION - BAR_POSITION;
    public static final int HEIGHT = 1080;

    static TextureRegion hitObject1, hitObject2, holdObject1;
    static String hitFlagString = "";
    static long visualOffsetMillis = 0;
    static float hitTimeElapsedMillis;
    private static int score = 0;
    private static int combo;
    private static int accuracy = 100;
    private static int hitObjectsPassed;
    final ButtonHero game;
    private InputMultiplexer inputMultiplexer = new InputMultiplexer();
    private OrthographicCamera camera;
    private ShapeRenderer shapeTester;
    private BeatMap currentBeatMap;
    private BulletHell bulletHell;
    private TextureAtlas atlas;
    private Label hitStateLabel, scoreLabel;

    public GameScreen(final ButtonHero game) {
        this.game = game;
        atlas = new TextureAtlas(Gdx.files.internal("packed/game.atlas"));
        hitObject1 = atlas.findRegion("mania-note1");
        hitObject2 = atlas.findRegion("mania-note2");
        holdObject1 = atlas.findRegion("mania-note1");
        Table uitable = new Table();
        uitable.setFillParent(true);
        uitable.pad(100);
        scoreLabel = new Label(Integer.toString(score), game.uiskin);
        hitStateLabel = new Label(hitFlagString, game.uiskin);
        hitStateLabel.addListener(event -> {
            hitTimeElapsedMillis = 0;
            hitStateLabel.setVisible(true);
            System.out.println("something changed");
            return true;
        });
        uitable.add(scoreLabel);
        uitable.row();
        uitable.add(hitStateLabel);
        uitable.right().top();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        currentBeatMap = new BeatMap(new File("colors.txt"));
        bulletHell = new BulletHell(1920/2, 1920);
        inputMultiplexer.addProcessor(game.uiStage);
        inputMultiplexer.addProcessor(currentBeatMap);
        Gdx.input.setInputProcessor(inputMultiplexer);
        shapeTester = new ShapeRenderer();
        game.uiStage.addActor(uitable);
        currentBeatMap.play();
    }

    public static void incrementScore(HitObject.HitState hitFlag) {
        hitObjectsPassed++;
        double x = (double) hitObjectsPassed;
        double ratio = (x-1.0)/x;

        switch (hitFlag) {
            case MISS:
                accuracy = (int) Math.round(accuracy * ratio);
                score -= 10000 * (1 + combo/10);
                combo = 0;
                break;
            case BAD:
                accuracy = (int) Math.round((accuracy + 16) * ratio);
                combo++;
                score -= 5000 * (100 - accuracy);
                break;
            case GREAT:
                accuracy = (int)Math.round((accuracy + 33) * ratio);
                combo++;
                score += 500 * accuracy;
                break;
            case EXCELLENT:
                accuracy = (int)Math.round((accuracy + 90) * ratio);
                combo++;
                score += 800 * accuracy;
                break;
            case PERFECT:
                accuracy = (int)Math.round((accuracy + 100) * ratio);
                combo++;
                score += 1000 * accuracy;
                break;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentBeatMap.update();
        bulletHell.update();
        game.uiStage.act(delta);
        game.uiStage.draw();
        scoreLabel.setText(Integer.toString(score));
        hitStateLabel.setText(hitFlagString);
        hitTimeElapsedMillis += delta * 1000;
        if (hitTimeElapsedMillis > 300) {
            hitStateLabel.setText("");
            hitTimeElapsedMillis = 0;
        }
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

        bulletHell.draw(game.batch);

        currentBeatMap.draw(game.batch);

//        game.font.draw(game.batch, accuracy / 100 + "." + accuracy % 100, 100, 800);
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
        atlas.dispose();
        currentBeatMap.dispose();
        shapeTester.dispose();
    }
}


