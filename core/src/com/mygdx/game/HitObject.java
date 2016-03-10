package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Work in progress
 */
public class HitObject extends Sprite {
    SongManager songManager;
    float beatFloat, beatTimeMillis;
    boolean isHit = false;
    //    int beatNumerator;
    int index;
    float millisFor4Beats;


    public HitObject(SongManager sm, TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm, float millisFor4Beats) {
        super(texRegion);
        songManager = sm;
        this.millisFor4Beats = millisFor4Beats;
        this.index = index;
        beatFloat = (float) beatNumerator / beatDenominator;
        beatTimeMillis = (beatFloat / bpm) * 60000;
    }

    public void onHit(int i) {
        isHit = true;
//        BeatMap.songIndices[index]++;
        songManager.incrementSongIndex(index);
        songManager.setHitFlagString(i);
    }

    public HitState calculateHit(float songTime) {
        float difference = Math.abs(beatTimeMillis - songTime);
        if (difference < 16) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[0];
            onHit(0);
            return HitState.PERFECT;
        } else if (difference < 37.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[1];
            onHit(1);
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[2];
            onHit(2);
            return HitState.GREAT;
        } else if (difference < 129.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[3];
            onHit(3);
            return HitState.BAD;
        } else if (difference < 300) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onHit(4);
            return HitState.MISS;
        } else {
            return HitState.IDLE;
        }
    }

    public void update(long songTime) {
        setY((BeatMap.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * BeatMap.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        //if it is a certain distance below the bottom and hasn't been marked as being hit yet, do so
        if (getY() <= BeatMap.BAR_POSITION - 150) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onHit(4);
        }
    }

    public enum HitState {
        IDLE, MISS, BAD, GREAT, EXCELLENT, PERFECT
    }
}
