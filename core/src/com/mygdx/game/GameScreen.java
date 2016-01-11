package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by William on 12/9/2015.
 */
public class GameScreen implements Screen{
    final ButtonHero game;
    private OrthographicCamera camera;

    private Array<Texture> hitObjectImages;
    private Array<Array<HitObject>> hitObjectArrays;
    private Sound hitSound;
    private Music music;
    private int bpm, offset, snapBeatDenominator;
    private double hoSpeed; //pixels per second
    private long lastNanoTime, currentNanoTime;
    private boolean snapToBeat;

    static final int[] XPOSITIONS= {0, 100, 200, 400};
    static final int YPOSITION = 100;
    static final int BEAT_DISTANCE = 500;


    public GameScreen(final ButtonHero game) {
        this.game = game;
        hitObjectImages = new Array<>(2);
        hitObjectImages.add(new Texture(Gdx.files.internal("mania-note1.png")));
        hitObjectImages.add(new Texture(Gdx.files.internal("mania-note2.png")));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        hitObjectArrays = new Array<>(4);
        addHitObjects(new File("colors.txt"));

        music.play();

    }


    private void addHitObjects(File file){
        try {
            Scanner scanner = new Scanner(file);
            bpm = scanner.nextInt();
            hoSpeed = 60 / bpm;
            offset = Integer.parseInt(scanner.next().substring(1));
            snapToBeat = scanner.nextBoolean();
            if (snapToBeat) snapBeatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                hitObjectArrays.add(new Array<>());
                int index = scanner.nextInt() - 1;
                while (!scanner.hasNextInt() && scanner.hasNext()) {
                    String str = scanner.next();
                    HitObject ho = null;
                    if (str.contains("d")) {
                        ho = new HitObject(Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))) * snapBeatDenominator,
                                snapBeatDenominator, Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                    } else {
                        ho = new HitObject(Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)) * snapBeatDenominator,
                                snapBeatDenominator);
                    }
                    ho.x = XPOSITIONS[index];
                    ho.y = YPOSITION;
                    ho.width = 256;
                    ho.height = 82;
                    hitObjectArrays.get(index).add(ho);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("broken");
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        currentNanoTime = TimeUtils.nanoTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(hitObjectImages.get(0), hitObjectArrays.get(1).get(0).x, hitObjectArrays.get(1).get(0).y);

        game.batch.end();

        for (int i = 0; i < hitObjectArrays.size; i++) {
            for (HitObject ho : )
        }

        lastNanoTime = currentNanoTime;
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
        hitObjectImages.forEach(texture -> dispose());
        //hitObjectArrays.forEach(hitObjects -> dispose());
    }
}
