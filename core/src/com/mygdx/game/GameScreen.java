package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Work in progress
 */
public class GameScreen implements Screen{
    final ButtonHero game;
    private OrthographicCamera camera;

    private Texture spriteSheet;
    private TextureRegion hitObject1, hitObject2;
    private Array<Array<HitObject>> hitObjectArrays;
    private Array<HitObject> drawnHitObjects;
    private Sound hitSound;
    private Music music;
    private int bpm, offset, snapBeatDenominator;
    private int[] indices = new int[4];
    private float secondsFor4Beats, millisFor4Beats;
    private float hoSpeed; //pixels per second
    private long lastNanoTime, currentNanoTime, startTime;
    private boolean snapToBeat;

    static final double EPSILON = 0.0000001;

    static final int[] XPOSITIONS= {300, 600, 900, 1200};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 100;
    static final int HIT_OBJECT_DISTANCE = 500;

    private float lastReportedPlayheadPosition;
    private long previousFrameTime, songTime;


    public GameScreen(final ButtonHero game) {
        this.game = game;
        spriteSheet = new Texture(Gdx.files.internal("maniasheet.png"));
        hitObject1 = new TextureRegion(spriteSheet, 0, 0, 256, 82);
        hitObject2 = new TextureRegion(spriteSheet, 0, 82, 256, 82);
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        hitObjectArrays = new Array<>(4);
        drawnHitObjects = new Array<>(false, 32);
        music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
        try {
            Scanner scanner = new Scanner(new File("colors.txt"));
            bpm = scanner.nextInt();
            secondsFor4Beats = 60f / bpm;
            millisFor4Beats = secondsFor4Beats * 1000;
            hoSpeed = HIT_OBJECT_DISTANCE / secondsFor4Beats;
            offset = Integer.parseInt(scanner.next().substring(1));
            snapToBeat = scanner.nextBoolean();
            if (snapToBeat) snapBeatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                hitObjectArrays.add(new Array<>());
                int index = scanner.nextInt() - 1;
                while (!scanner.hasNextInt() && scanner.hasNext()) {
                    String str = scanner.next();
                    HitObject ho;
                    if (str.contains("d")) {
                        if (index == 0 || index == 3) {
                            ho = new HitObject(hitObject1, Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))) * snapBeatDenominator,
                                    snapBeatDenominator, bpm, Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        } else {
                            ho = new HitObject(hitObject2, Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))) * snapBeatDenominator,
                                    snapBeatDenominator, bpm, Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        }
                    } else {
                        if (index == 0 || index == 3) {
                            ho = new HitObject(hitObject1, Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1)) * snapBeatDenominator,
                                    snapBeatDenominator, bpm);
                        } else {
                            ho = new HitObject(hitObject2, Integer.parseInt(str.substring(0, str.indexOf("n"))) +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1)) * snapBeatDenominator,
                                    snapBeatDenominator, bpm);
                        }
                    }
                    ho.setX(XPOSITIONS[index]);
                    ho.setY(YPOSITION);
                    hitObjectArrays.get(index).add(ho);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("broken");
        }

        boolean musicWait = false;
        float minTime = 1000;
        for (Array<HitObject> hoArray : hitObjectArrays) {
            float firstTime = hoArray.get(0).beatTimeMillis;
            musicWait |= firstTime < millisFor4Beats;
            minTime = Math.min(minTime, firstTime);
        }

        for (int i = 0; i < hitObjectArrays.size; i++) {
            if (Math.abs(hitObjectArrays.get(i).get(0).beatTimeMillis - minTime) < EPSILON) {
                drawnHitObjects.add(hitObjectArrays.get(i).get(0));
                indices[i]++;
            }
        }

        if (musicWait) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    previousFrameTime = TimeUtils.millis();
                    lastReportedPlayheadPosition = 0;
                    music.play();
                }
            }, secondsFor4Beats - minTime);
        } else {
            previousFrameTime = TimeUtils.millis();
            lastReportedPlayheadPosition = 0;
            music.play();
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        songTime += TimeUtils.timeSinceMillis(previousFrameTime);
        previousFrameTime = TimeUtils.millis();
        if (music.getPosition() != lastReportedPlayheadPosition) {
            songTime = (long) (songTime + music.getPosition() * 1000) / 2;
            lastReportedPlayheadPosition = music.getPosition();
        }
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (HitObject ho : drawnHitObjects) {
            ho.draw(game.batch);
        }
        game.batch.end();
        Iterator<HitObject> iter = drawnHitObjects.iterator();
        while (iter.hasNext()) {
            HitObject ho = iter.next();
            ho.setY(((millisFor4Beats + ho.beatTimeMillis - songTime) * HIT_OBJECT_DISTANCE) / millisFor4Beats);
            if (ho.getY() < BAR_POSITION) {
                iter.remove();
            }
        }
        for (int i = 0; i < hitObjectArrays.size; i++) {
            HitObject ho = hitObjectArrays.get(i).get(indices[i]);
            if (ho.beatTimeMillis <= songTime + millisFor4Beats) {
                drawnHitObjects.add(ho);
                indices[i]++;
                if (indices[i] >= hitObjectArrays.get(i).size) {
                    indices[i] = 0;
                }
            }
        }
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
        spriteSheet.dispose();
        hitSound.dispose();
        music.dispose();
    }
}
