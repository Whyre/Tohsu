package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
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
    static final double EPSILON = 0.0000001;
    static final int[] XPOSITIONS= {50, 250, 450, 650};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 200;
    static final int HIT_OBJECT_DISTANCE = YPOSITION - BAR_POSITION;
    static final int[] KEYS = {Keys.A, Keys.S, Keys.D, Keys.F};
    static TextureRegion hitObject1, hitObject2;
    static int hitFlag = -1; //-1: do nothing, 0: perfect, 1: great, 2:bad, 3:miss
    static int[] spawnIndices = new int[4]; //the index of the first hitobject that has not yet been spawned
    static int[] songIndices = new int[4]; //the index of the first hitobject that has not yet reached the strum bar
    static long visualOffsetMillis = 0;
    final ButtonHero game;
    private OrthographicCamera camera;
    private ShapeRenderer shapeTester;
    private BeatMap currentBeatMap;
    private Texture spriteSheet;
    private Array<HitObject> drawnHitObjects;
    private boolean musicWait, isLoading;
    private float lastReportedPlayheadPosition;
    private long previousFrameTime, songTime;
    private int accuracy = 10000;
    private float hitTimeElapsedMillis;


    public GameScreen(final ButtonHero game) {
        this.game = game;
        spriteSheet = new Texture(Gdx.files.internal("maniasheet.png"));
        hitObject1 = new TextureRegion(spriteSheet, 0, 0, 256, 82);
        hitObject2 = new TextureRegion(spriteSheet, 0, 82, 256, 82);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        InputProcessor inputProcessor = new MyInputProccesor();
        Gdx.input.setInputProcessor(inputProcessor);
        initializeAndPlay();
        shapeTester = new ShapeRenderer();
    }

    private void initializeAndPlay() {
        isLoading = true;
        for (int i = 0; i < 4; i++) {
            spawnIndices[i] = 0;
            songIndices[i] = 0;
        }
        IntMap<Array<HitObject>> hitObjectMap = new IntMap<>();
        drawnHitObjects = new Array<>(false, 32);
        try {
            Scanner scanner = new Scanner(new File("colors.txt"));
            int bpm = scanner.nextInt();
            float secondsFor4Beats = 120f / bpm;
            int offset = Integer.parseInt(scanner.next().substring(1));
            boolean snapToBeat = scanner.nextBoolean();
            int beatDenominator = -1;
            if (snapToBeat)
                beatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                int index = scanner.nextInt() - 1;
                hitObjectMap.put(index, new Array<>());
                while (!scanner.hasNextInt() && scanner.hasNext()) {
                    String str = scanner.next();
                    HitObject ho;
                    if (str.contains("d")) {
                        if (index == 0 || index == 3) {
                            ho = new HoldObject(hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        } else {
                            ho = new HoldObject(hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))), beatDenominator, bpm,
                                    Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        }
                    } else if (index == 0 || index == 3) {
                        ho = new HitObject(hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm);
                    } else {
                        ho = new HitObject(hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * beatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)), beatDenominator, bpm);
                    }
                    ho.setScale(.5f);
                    ho.setX(XPOSITIONS[index]);
                    ho.setY(YPOSITION);
                    hitObjectMap.get(index).add(ho);
                    currentBeatMap = new BeatMap(Gdx.audio.newMusic(Gdx.files.internal("colors.mp3")),
                            Gdx.audio.newSound(Gdx.files.internal("hitsound.wav")),
                            hitObjectMap,
                            offset,
                            bpm,
                            beatDenominator,
                            secondsFor4Beats);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("broken");
        }
        musicWait = false;
        float minTime = 1000;
        for (Array<HitObject> hoArray : currentBeatMap.hitObjectMap.values()) {
            float firstTime = hoArray.get(0).beatTimeMillis;
            musicWait |= firstTime < currentBeatMap.millisFor4Beats;
            minTime = Math.min(minTime, firstTime);
        }

        for (int i = 0; i < 4; i++) {
            if (Math.abs(currentBeatMap.hitObjectMap.get(i).get(0).beatTimeMillis - minTime) < EPSILON) {
                drawnHitObjects.add(currentBeatMap.hitObjectMap.get(i).get(0));
                spawnIndices[i]++;
            }
        }
        currentBeatMap.music.setOnCompletionListener(music1 -> {
            currentBeatMap.music.setPosition(0);
            initializeAndPlay();
        });

        isLoading = false;

        if (musicWait) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    previousFrameTime = TimeUtils.millis();
                    lastReportedPlayheadPosition = 0;
                    currentBeatMap.music.play();
                }
            }, currentBeatMap.secondsFor4Beats - minTime);
        } else {
            previousFrameTime = TimeUtils.millis();
            lastReportedPlayheadPosition = 0;
            currentBeatMap.music.play();
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
        if (currentBeatMap.music.getPosition() != lastReportedPlayheadPosition) {
            songTime = (long) Math.round((songTime + currentBeatMap.music.getPosition() * 1000) / 2);
            lastReportedPlayheadPosition = currentBeatMap.music.getPosition();
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

        if (isLoading) return;

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (HitObject ho : drawnHitObjects) {
            ho.draw(game.batch);
        }
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

        Iterator<HitObject> iter = drawnHitObjects.iterator();
        while (iter.hasNext()) {
            HitObject ho = iter.next();
            ho.update(songTime, currentBeatMap.millisFor4Beats);
            if (ho.isHit) {
                iter.remove();
            }
        }

        for (int i = 0; i < 4; i++) {
            try {
                HitObject ho = currentBeatMap.hitObjectMap.get(i).get(spawnIndices[i]);
                if (ho.beatTimeMillis <= songTime + currentBeatMap.millisFor4Beats) {
                    drawnHitObjects.add(ho);
                    spawnIndices[i]++;
                }
            } catch (IndexOutOfBoundsException e) {
                spawnIndices[i] = -1;
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
        currentBeatMap.dispose();
        shapeTester.dispose();
    }

    public class MyInputProccesor implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            for (int i = 0; i < 4; i++) {
                if (keycode == KEYS[i] && songIndices[i] < currentBeatMap.hitObjectMap.get(i).size) {
                    HitObject ho = currentBeatMap.hitObjectMap.get(i).get(songIndices[i]);
                    float difference = Math.abs(ho.beatTimeMillis - songTime);
                    if (difference < 37.5) {
                        ho.onHit(0);
                        currentBeatMap.hitSound.play();
                    } else if (difference < 83.5) {
                        ho.onHit(1);
                        currentBeatMap.hitSound.play();
                    } else if (difference < 129.5) {
                        ho.onHit(2);
                        currentBeatMap.hitSound.play();
                    } else if (difference < 400) {
                        ho.onHit(3);
                        currentBeatMap.hitSound.play();
                    }
                }
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
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
}
