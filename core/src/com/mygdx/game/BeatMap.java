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
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
    static final int[] XPOSITIONS = {50, 250, 450, 650};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 200;
    static final int HIT_OBJECT_DISTANCE = YPOSITION - BAR_POSITION;
    private static final int[] KEYS = {Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.F};
    static TextureRegion hitObject1, hitObject2, holdObject1;
    static long visualOffsetMillis = 0;
    Array<HitObject> drawnHitObjects;
    Array<Array<HitObject>> hitObjectArrays;
    long previousFrameTime, songTime;
    private int[] spawnIndices = new int[4]; //the index of the first hitobject that has not yet been spawned
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
    private ProgressBar scoreBar;
    private Pool<Event> eventPool;
    private Label scoreLabel, hitStateLabel;
    private Skin uiskin;
    private float hitStateElapsedMillis;
    private SongManager songManager;

    public BeatMap(File beatMapFile, ScoreManager scoreManager, Skin skin) {
        this.scoreManager = scoreManager;
        songManager = new SongManager();
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
        scoreBar = new ProgressBar(0, 1000, 5, true, uiskin);
        scoreBar.setAnimateDuration(0.1f);

        Table uitable = new Table();
        uitable.setFillParent(true);
        uitable.pad(100);
        scoreLabel = new Label(Integer.toString(scoreManager.getScore()), uiskin);
        scoreLabel.setAlignment(Align.right);
        hitStateLabel = new Label("test", uiskin);
        hitStateLabel.addListener(event -> {
            hitStateLabel.setText(songManager.getHitFlagString());
            //hitStateLabel.setVisible(true);
            hitStateElapsedMillis = 0;
            return true;
        });
        uitable.left().top();
        uitable.add(scoreLabel);
        uitable.row();
        uitable.add(hitStateLabel);
        uitable.row();
//        uitable.add(scoreBar);
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
                            ho = new HoldObject(songManager, GameScreen.hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)), secondsFor4Beats * 1000);
                        } else {
                            ho = new HoldObject(songManager, GameScreen.hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)), secondsFor4Beats * 1000);
                        }
                    } else if (index == 0 || index == 3) {
                        ho = new HitObject(songManager, GameScreen.hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm, millisFor4Beats);
                    } else {
                        ho = new HitObject(songManager, GameScreen.hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm, millisFor4Beats);
                    }
                    ho.setScale(.5f);
                    ho.setX(XPOSITIONS[index]);
                    ho.setY(YPOSITION);
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
        songManager.resetSongIndices();
        for (int i = 0; i < 4; i++) {
            spawnIndices[i] = 0;
            arrayFinished[i] = false;
            songManager.releaseKey(i);
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

    int i = 0;
    public void update(float delta) {
//        songTime += TimeUtils.timeSinceMillis(previousFrameTime);
//        previousFrameTime = TimeUtils.millis();
        songTime += delta * 1000;
        float musicPosition = music.getPosition();
//        if (musicPosition != lastReportedPlayheadPosition) {
        if (Math.abs(songTime - musicPosition) > EPSILON) {
            songTime = (long) ((songTime + musicPosition * 1000) / 2);
//            lastReportedPlayheadPosition = musicPosition;
        }
        scoreLabel.setText(Integer.toString(scoreManager.getScore()));
        System.out.println(scoreBar.setValue(i));
        i+= 100;
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
            int songIndex = songManager.getSongIndex(i);
            if (keycode == KEYS[i] && songIndex < hitObjectArrays.get(i).size) {
//                HitObject ho = hitObjectArrays.get(i).get(songIndices[i]);
                HitObject ho = hitObjectArrays.get(i).get(songIndex);
                HitObject.HitState hitState = ho.calculateHit(songTime);
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
            if (keycode == KEYS[i] && songManager.isKeyHeld(i)) {
//                HoldObject ho = heldObjects[i];
                HoldObject ho = songManager.getHeldObject(i);
                HitObject.HitState hitState = ho.calculateRelease(songTime);
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
