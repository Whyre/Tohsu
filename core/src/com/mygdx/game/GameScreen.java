package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.*;

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
    private ShapeRenderer shapeTester;

    private Texture spriteSheet;
    private TextureRegion hitObject1, hitObject2;
    private IntMap<Array<HitObject>> hitObjectMap;
    private Array<HitObject> drawnHitObjects;
    private OrderedMap<Integer, HitObject> drawnHitObjectsv2;
    private Sound hitSound;
    private Music music;
    private int bpm, offset, snapBeatDenominator;
    private int[] spawnIndices = new int[4]; //the index of the first hitobject that has not yet been spawned
    private int[] songIndices = new int[4]; //the index of the first hitobject that has not yet reached the strum bar
    private float secondsFor4Beats, millisFor4Beats;
    private boolean snapToBeat;

    static final double EPSILON = 0.0000001;

    static final int[] XPOSITIONS= {50, 250, 450, 650};
    static final int YPOSITION = 900;
    static final int BAR_POSITION = 200;
    static final int HIT_OBJECT_DISTANCE = YPOSITION - BAR_POSITION;
    static final int[] KEYS = {Keys.D, Keys.F, Keys.J, Keys.K};

    private float lastReportedPlayheadPosition;
    private long previousFrameTime, songTime;
    private long visualOffsetMillis = 0;


    public GameScreen(final ButtonHero game) {
        this.game = game;
        spriteSheet = new Texture(Gdx.files.internal("maniasheet.png"));
        hitObject1 = new TextureRegion(spriteSheet, 0, 0, 256, 82);
        hitObject2 = new TextureRegion(spriteSheet, 0, 82, 256, 82);
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitsound.wav"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        InputProcessor inputProcessor = new MyInputProccesor();
        Gdx.input.setInputProcessor(inputProcessor);

        hitObjectMap = new IntMap<>();
        drawnHitObjects = new Array<>(false, 32);
        music = Gdx.audio.newMusic(Gdx.files.internal("colors.mp3"));
        try {
            Scanner scanner = new Scanner(new File("colors.txt"));
            bpm = scanner.nextInt();
            secondsFor4Beats = 240f / bpm;
            millisFor4Beats = secondsFor4Beats * 1000;
            offset = Integer.parseInt(scanner.next().substring(1));
            snapToBeat = scanner.nextBoolean();
            if (snapToBeat) snapBeatDenominator = scanner.nextInt();
            while (scanner.hasNextLine()) {
                int index = scanner.nextInt() - 1;
                hitObjectMap.put(index, new Array<>());
                while (!scanner.hasNextInt() && scanner.hasNext()) {
                    String str = scanner.next();
                    HitObject ho;
                    if (str.contains("d")) {
                        if (index == 0 || index == 3) {
                            ho = new HitObject(hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * snapBeatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))),
                                    snapBeatDenominator, bpm, Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        } else {
                            ho = new HitObject(hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * snapBeatDenominator +
                                    Integer.parseInt(str.substring(str.indexOf("n") + 1, str.indexOf("d"))),
                                    snapBeatDenominator, bpm, Integer.parseInt(str.substring(str.indexOf("d") + 1)));
                        }
                    }
                    else if (index == 0 || index == 3) {
                        ho = new HitObject(hitObject1, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * snapBeatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)),
                                snapBeatDenominator, bpm);
                    } else {
                        ho = new HitObject(hitObject2, index, Integer.parseInt(str.substring(0, str.indexOf("n"))) * snapBeatDenominator +
                                Integer.parseInt(str.substring(str.indexOf("n") + 1)),
                                snapBeatDenominator, bpm);
                    }
                    ho.setScale(.5f);
                    ho.setX(XPOSITIONS[index]);
                    ho.setY(YPOSITION);
                    hitObjectMap.get(index).add(ho);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("broken");
        }

        for (Array<HitObject> hoArray : hitObjectMap.values()) {
            for (HitObject ho : hoArray) {
                System.out.println("Array: " + hitObjectMap.findKey(hoArray, true, -1) + " Beat Float: " + ho.beatFloat + " " + ho.beatTimeMillis);
            }
        }

        boolean musicWait = false;
        float minTime = 1000;
        for (Array<HitObject> hoArray : hitObjectMap.values()) {
            float firstTime = hoArray.get(0).beatTimeMillis;
            musicWait |= firstTime < millisFor4Beats;
            minTime = Math.min(minTime, firstTime);
        }

        for (int i = 0; i < 4; i++) {
            if (Math.abs(hitObjectMap.get(i).get(0).beatTimeMillis - minTime) < EPSILON) {
                drawnHitObjects.add(hitObjectMap.get(i).get(0));
                spawnIndices[i]++;
            }
        }

        shapeTester = new ShapeRenderer();

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
            songTime = (long) Math.round((songTime + music.getPosition() * 1000) / 2);
            lastReportedPlayheadPosition = music.getPosition();
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
        for (HitObject ho : drawnHitObjects) {
            ho.draw(game.batch);
        }
  /*      for (HitObject ho : drawnHitObjectsv2.values()) {
            ho.draw(game.batch);
        }*/
        game.batch.end();
        Iterator<HitObject> iter = drawnHitObjects.iterator();
        while (iter.hasNext()) {
            HitObject ho = iter.next();
            ho.setY((BAR_POSITION + ((ho.beatTimeMillis - songTime + visualOffsetMillis) * HIT_OBJECT_DISTANCE) / millisFor4Beats));
            if (ho.getY() <= BAR_POSITION - 150) {
                songIndices[ho.index]++;
                iter.remove();
            }
        }

        for (int i = 0; i < 4; i++) {
            try {
                HitObject ho = hitObjectMap.get(i).get(spawnIndices[i]);
                if (ho.beatTimeMillis <= songTime + millisFor4Beats) {
                    drawnHitObjects.add(ho);
                    // drawnHitObjectsv2.put(i, ho);
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
        hitSound.dispose();
        music.dispose();
    }

    public class MyInputProccesor implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            for (int i = 0; i < 4; i++) {
                if (keycode == KEYS[i]) {
                    float difference = Math.abs(hitObjectMap.get(i).get(songIndices[i]).beatTimeMillis - songTime);
                    System.out.println(difference);
                    if (difference < 50) {
                        hitSound.play();
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
