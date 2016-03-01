package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 *Handles the positions of the hitobjects and the sounds
 */
public class BeatMap extends Stage implements Disposable, InputProcessor {
    static final double EPSILON = 0.0000001;
    static final String[] HITFLAGSTRINGS = {
            "Perfect!", "Excellent!", "Great!", "Bad!", "Miss!"
    };
    private static final int[] KEYS = {Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.F};
    static boolean[] keyHeld = new boolean[4];
    static TextureRegion hitObject1, hitObject2, holdObject1;
    static int[] spawnIndices = new int[4]; //the index of the first hitobject that has not yet been spawned
    static int[] songIndices = new int[4]; //the index of the first hitobject that has not yet reached the strum bar
    static HoldObject[] heldObjects = new HoldObject[4];
    static long visualOffsetMillis = 0;
    static String hitFlagString;
    Array<HitObject> drawnHitObjects;
    Array<Array<HitObject>> hitObjectArrays;
    long previousFrameTime, songTime;
    private float secondsFor4Beats, millisFor4Beats;
    private Music music;
    private Sound hitSound;
    private Image track, barLeft, barRight;
    private int bpm = -1;
    private int offset;
    private int beatDenominator = -1;
    private float lastReportedPlayheadPosition;
    private boolean[] arrayFinished = new boolean[4];
    private boolean isLooping = true;
    private ScoreManager scoreManager;
    private Pool<Event> eventPool;
    private Label scoreLabel, hitStateLabel;
    private Skin uiskin;
    private float hitStateElapsedMillis;

    public BeatMap(File beatMapFile, ScoreManager scoreManager, Skin skin) {
        this.scoreManager = scoreManager;
        uiskin = skin;
        eventPool = new Pool<Event>() {
            @Override
            protected Event newObject() {
                Event e = new Event();
                e.setStage(BeatMap.this);
                e.setTarget(hitStateLabel);
                e.setListenerActor(hitStateLabel);
                e.setBubbles(false);
                e.setCapture(false);
                return e;
            }
        };

        Table uitable = new Table();
        uitable.setFillParent(true);
        uitable.pad(100);
        scoreLabel = new Label(Integer.toString(scoreManager.score), uiskin);
        scoreLabel.setAlignment(Align.right);
        hitStateLabel = new Label("test", uiskin);
        hitStateLabel.addListener(event -> {
            hitStateLabel.setText(hitFlagString);
            //hitStateLabel.setVisible(true);
            hitStateElapsedMillis = 0;
            return true;
        });
        uitable.add(scoreLabel);
        uitable.row();
        uitable.add(hitStateLabel);
        uitable.left().top();
        this.addActor(uitable);

        track = new Image(skin, "Track");
//        this.addActor(track);
        this.initialize(beatMapFile);
    }

    public void initialize(File beatMapFile) {
        try {
            hitObjectArrays = new Array<>(4);
            Scanner scanner = new Scanner(beatMapFile);
            this.bpm = scanner.nextInt();
            this.secondsFor4Beats = 90f / this.bpm;
            this.millisFor4Beats = secondsFor4Beats * 1000;
            this.offset = Integer.parseInt(scanner.next().substring(1));
            boolean snapToBeat = scanner.nextBoolean();
            if (snapToBeat)
                this.beatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                int index = scanner.nextInt() - 1;
                hitObjectArrays.add(new Array<>());
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
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm, millisFor4Beats);
                    } else {
                        ho = new HitObject(GameScreen.hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm, millisFor4Beats);
                    }
                    ho.setScale(.5f);
                    ho.setX(GameScreen.XPOSITIONS[index]);
                    ho.setY(GameScreen.YPOSITION);
                    this.hitObjectArrays.get(index).add(ho);
                    this.music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
                    this.hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("broken");
        }

    }

    public void play() {
//        initialize(new File("colors.txt"));
        songTime = 0;
        for (int i = 0; i < 4; i++) {
            spawnIndices[i] = 0;
            songIndices[i] = 0;
            arrayFinished[i] = false;
            keyHeld[i] = false;
        }
        drawnHitObjects = new Array<>(false, 32);
//        musicWait = false;
        float minTime = 1000;
        for (Array<HitObject> hoArray : hitObjectArrays) {
            float firstTime = hoArray.get(0).beatTimeMillis;
//            musicWait |= firstTime < millisFor4Beats;
            minTime = Math.min(minTime, firstTime);
        }

        for (int i = 0; i < 4; i++) {
            if (Math.abs(hitObjectArrays.get(i).get(0).beatTimeMillis - minTime) < EPSILON) {
                drawnHitObjects.add(hitObjectArrays.get(i).get(0));
                spawnIndices[i]++;
            }
        }
        music.setOnCompletionListener(music-> {
            if (isLooping) {
                music.setPosition(0);
                this.initialize(new File("colors.txt"));
                this.play();
            }
        });
//        previousFrameTime = TimeUtils.millis();
//        lastReportedPlayheadPosition = 0;
        music.play();
    }

    public void update(float delta) {
//        songTime += TimeUtils.timeSinceMillis(previousFrameTime);
//        previousFrameTime = TimeUtils.millis();
        songTime += delta * 1000;
        float musicPosition = music.getPosition();
//        if (musicPosition != lastReportedPlayheadPosition) {
        if (Math.abs(songTime - musicPosition) < EPSILON) {
            songTime = (long) ((songTime + musicPosition * 1000) / 2);
//            lastReportedPlayheadPosition = musicPosition;
        }
        scoreLabel.setText(Integer.toString(scoreManager.score));
//        hitStateLabel.setText(hitFlagString);
        hitStateElapsedMillis += delta * 1000;
        if (hitStateElapsedMillis > 300) {
            hitStateLabel.setText("");
        }

        for (Iterator<HitObject> iter = drawnHitObjects.iterator(); iter.hasNext(); ) {
            HitObject ho = iter.next();
            ho.update(songTime);
            if (ho.isHit) {
                iter.remove();
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!arrayFinished[i]) {
                try {
                    HitObject ho = hitObjectArrays.get(i).get(spawnIndices[i]);
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
            if (keycode == KEYS[i] && songIndices[i] < hitObjectArrays.get(i).size) {
                HitObject ho = hitObjectArrays.get(i).get(songIndices[i]);
                HitObject.HitState hitState = ho.calculateHit(Math.abs(ho.beatTimeMillis - songTime));
                if (hitState != HitObject.HitState.MISS)
                    hitSound.play();
                scoreManager.incrementScore(hitState);
                hitStateLabel.fire(eventPool.obtain());
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
                HitObject.HitState hitState = ho.calculateRelease(Math.abs(ho.beatTimeMillis + ho.holdDurationMillis - songTime));
                if (hitState != HitObject.HitState.MISS)
                    hitSound.play();
                scoreManager.incrementScore(hitState);
                hitStateLabel.fire(eventPool.obtain());
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
