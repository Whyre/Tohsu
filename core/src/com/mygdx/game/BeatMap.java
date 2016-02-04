package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 *Handles the positions of the hitobjects and the sounds
 */
public class BeatMap implements Disposable, InputProcessor {
    static final double EPSILON = 0.0000001;
    private static final int[] KEYS = {Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.F};
    static boolean[] keyHeld = new boolean[4];
    static TextureRegion hitObject1, hitObject2, holdObject1;
    static int hitFlag = -1; //-1: do nothing, 0: perfect, 1: great, 2:bad, 3:miss
    static int[] spawnIndices = new int[4]; //the index of the first hitobject that has not yet been spawned
    static int[] songIndices = new int[4]; //the index of the first hitobject that has not yet reached the strum bar
    static HoldObject[] heldObjects = new HoldObject[4];
    static long visualOffsetMillis = 0;
    Array<HitObject> drawnHitObjects;
    IntMap<Array<HitObject>> hitObjectMap = new IntMap<>(4);
    long previousFrameTime, songTime;
    private float secondsFor4Beats, millisFor4Beats;
    private Music music;
    private Sound hitSound;
    private int bpm = -1;
    private int offset;
    private int beatDenominator = -1;
    private float lastReportedPlayheadPosition;
    private boolean[] arrayFinished = new boolean[4];
    private boolean isLooping = true;


    public BeatMap(Music music, Sound hitSound, IntMap<Array<HitObject>> hitObjectMap, int offset, int bpm, int beatDenominator, float secondsFor4Beats) {
        this.music = music;
        this.hitSound = hitSound;
        this.hitObjectMap = hitObjectMap;
        this.bpm = bpm;
        this.offset = offset;
        this.beatDenominator = beatDenominator;
        this.secondsFor4Beats = secondsFor4Beats;
        millisFor4Beats = secondsFor4Beats * 1000;
    }

    public BeatMap(File beatMapFile) {
        this.initialize(beatMapFile);
    }

    public void initialize(File beatMapFile) {
        try {
            Scanner scanner = new Scanner(beatMapFile);
            this.bpm = scanner.nextInt();
            this.secondsFor4Beats = 120f / this.bpm;
            this.millisFor4Beats = secondsFor4Beats * 1000;
            this.offset = Integer.parseInt(scanner.next().substring(1));
            boolean snapToBeat = scanner.nextBoolean();
            if (snapToBeat)
                this.beatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                int index = scanner.nextInt() - 1;
                this.hitObjectMap.put(index, new Array<>());
                while (!scanner.hasNextInt() && scanner.hasNext()) {
                    String str = scanner.next();
                    HitObject ho;
                    if (str.contains("d")) {
                        if (index == 0 || index == 3) {
                            ho = new HoldObject(GameScreen.hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)), secondsFor4Beats * 1000);
                        } else {
                            ho = new HoldObject(GameScreen.hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)), secondsFor4Beats * 1000);
                        }
                    } else if (index == 0 || index == 3) {
                        ho = new HitObject(GameScreen.hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm);
                    } else {
                        ho = new HitObject(GameScreen.hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm);
                    }
                    ho.setScale(.5f);
                    ho.setX(GameScreen.XPOSITIONS[index]);
                    ho.setY(GameScreen.YPOSITION);
                    this.hitObjectMap.get(index).add(ho);
                    this.music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
                    this.hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("broken");
        }
    }

    public void play() {
        initialize(new File("colors.txt"));
        songTime = 0;
        hitFlag = -1;
        for (int i = 0; i < 4; i++) {
            spawnIndices[i] = 0;
            songIndices[i] = 0;
            arrayFinished[i] = false;
            keyHeld[i] = false;
        }
        drawnHitObjects = new Array<>(false, 32);
//        musicWait = false;
        float minTime = 1000;
        for (Array<HitObject> hoArray : hitObjectMap.values()) {
            float firstTime = hoArray.get(0).beatTimeMillis;
//            musicWait |= firstTime < millisFor4Beats;
            minTime = Math.min(minTime, firstTime);
        }

        for (int i = 0; i < 4; i++) {
            if (Math.abs(hitObjectMap.get(i).get(0).beatTimeMillis - minTime) < EPSILON) {
                drawnHitObjects.add(hitObjectMap.get(i).get(0));
                spawnIndices[i]++;
            }
        }
        music.setOnCompletionListener(music1 -> {
            if (isLooping) {
                music.setPosition(0);
                this.play();
            }
        });
        previousFrameTime = TimeUtils.millis();
        lastReportedPlayheadPosition = 0;
        music.play();
    }

    public void update() {
        songTime += TimeUtils.timeSinceMillis(previousFrameTime);
        previousFrameTime = TimeUtils.millis();
        if (music.getPosition() != lastReportedPlayheadPosition) {
            songTime = (long) ((songTime + music.getPosition() * 1000) / 2);
            lastReportedPlayheadPosition = music.getPosition();
        }

        for (Iterator<HitObject> iter = drawnHitObjects.iterator(); iter.hasNext(); ) {
            HitObject ho = iter.next();
            ho.update(songTime, millisFor4Beats);
            if (ho.isHit) {
                iter.remove();
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!arrayFinished[i]) {
                try {
                    HitObject ho = hitObjectMap.get(i).get(spawnIndices[i]);
                    if (ho.beatTimeMillis <= songTime + millisFor4Beats) {
                        drawnHitObjects.add(ho);
                        spawnIndices[i]++;
                    }
                } catch (IndexOutOfBoundsException e) {
                    arrayFinished[i] = true;
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (HitObject ho : drawnHitObjects) {
            ho.draw(batch);
        }
    }

    public void pause() {
        music.pause();
    }

    public void resume() {
        songTime = 0;
        hitFlag = -1;
        previousFrameTime = TimeUtils.millis();
        lastReportedPlayheadPosition = music.getPosition();
        music.play();
    }

    public void dispose() {
        music.dispose();
        hitSound.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        for (int i = 0; i < 4; i++) {
            if (keycode == KEYS[i] && songIndices[i] < hitObjectMap.get(i).size) {
                HitObject ho = hitObjectMap.get(i).get(songIndices[i]);
                float difference = Math.abs(ho.beatTimeMillis - songTime);
                if (ho.calculateHit(difference))
                    hitSound.play();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (int i = 0; i < 4; i++) {
            if (keycode == KEYS[i] && keyHeld[i]) {
                HoldObject ho = heldObjects[i];
                float difference = ho.beatTimeMillis + ho.holdDurationMillis - songTime;
                if (ho.calculateRelease(difference))
                    hitSound.play();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
