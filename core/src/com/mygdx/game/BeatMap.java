package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;

/**
 *
 */
public class BeatMap implements Disposable {
    IntMap<Array<HitObject>> hitObjectMap;
    float secondsFor4Beats, millisFor4Beats;
    Music music;
    Sound hitSound;
    private int bpm = -1;
    private int offset;
    private int beatDenominator = -1;


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

    public int getBPM() {
        return bpm;
    }

    public int getOffset() {
        return offset;
    }

    public int getBeatDenominator() {
        return beatDenominator;
    }

    public void dispose() {
        music.dispose();
        hitSound.dispose();
    }
}
