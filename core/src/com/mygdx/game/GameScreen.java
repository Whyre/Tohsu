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

    private Texture hitObjectImage1, hitObjectImage2;
    private Array<Array<HitObject>> hitObjectArrays;
    private Sound hitSound;
    private Music music;
    private int bpm, offset, snapBeatDenominator;
    private int[][] indices;
    private double secondsFor4Beats;
    private double hoSpeed; //pixels per second
    private long lastNanoTime, currentNanoTime, startTime;
    private boolean snapToBeat;

    static final long CONVERSION_MINUTE_TO_NANO = 60000000000L;

    static final int[] XPOSITIONS= {300, 600, 900, 1200};
    static final int YPOSITION = 900;
    static final int HIT_OBJECT_DISTANCE = 500;


    public GameScreen(final ButtonHero game) {
        this.game = game;
        hitObjectImage1 = new Texture(Gdx.files.internal("mania-note1.png"));
        hitObjectImage2 = new Texture(Gdx.files.internal("mania-note2.png"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        hitObjectArrays = new Array<>(4);
        music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
        try {
            Scanner scanner = new Scanner(new File("colors.txt"));
            bpm = scanner.nextInt();
            secondsFor4Beats = 60.0 / bpm;
            hoSpeed = HIT_OBJECT_DISTANCE / secondsFor4Beats;
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

        indices = new int[4][2];
        for (int i = 0; i < 4; i++) {
            if (hitObjectArrays.get(i).get(0).beatDouble < 4) {
                indices[i][0] = 0;
                int j = 1;
                while (hitObjectArrays.get(i).get(j).beatDouble < 4) {
                    j++;
                }
                indices[i][1] = j;
            } else {
                indices[i][0] = -1;
            }
        }

        music.play();
        startTime = TimeUtils.nanoTime();
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

        for (int i = 0; i < 4; i++) {
            if (indices[i][0] >= 0) {
                for (int j = indices[i][0]; j < indices[i][1]; j++) {
                    if (i == 0 || i == 3)
                        game.batch.draw(hitObjectImage1, hitObjectArrays.get(i).get(j).x, hitObjectArrays.get(i).get(j).y);
                    else
                        game.batch.draw(hitObjectImage2, hitObjectArrays.get(i).get(j).x, hitObjectArrays.get(i).get(j).y);
                }
            }
        }

        game.batch.end();

        double musicSecondsTimeElapsed = (currentNanoTime - startTime) / 10000000000L;
        if (lastNanoTime != 0) {
            for (int i = 0; i < 4; i++) {
                if (indices[i][0] >=0) {
                    for (int j = indices[i][0]; j < indices[i][1]; j++) {
                        hitObjectArrays.get(i).get(j).y -= hoSpeed * ((currentNanoTime - lastNanoTime) / 100000000);
                    }
                    while (hitObjectArrays.get(i).get(indices[i][0]).beatDouble < musicSecondsTimeElapsed - secondsFor4Beats) {
                        System.out.println((currentNanoTime - startTime) * 1000000000);
                        indices[i][0]++;
                    }
                    while (indices[i][1] > 0 && hitObjectArrays.get(i).get(indices[i][1]).beatDouble < musicSecondsTimeElapsed) {
                        indices[i][1]++;
                    }
                } else {
                    if (hitObjectArrays.get(i).get(0).beatDouble < 4 + bpm*(musicSecondsTimeElapsed/60)) {
                        indices[i][0] = 0;
                        int j = 1;
                        while (hitObjectArrays.get(i).get(j).beatDouble < 4 + bpm*(musicSecondsTimeElapsed/60)) {
                            j++;
                        }
                        indices[i][1] = j;
                    }
                }
            }

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
        hitObjectImage1.dispose();
        hitObjectImage2.dispose();
        hitSound.dispose();
        music.dispose();
    }
}
