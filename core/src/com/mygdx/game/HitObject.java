package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Work in progress
 */
public class HitObject extends Sprite {
    float beatFloat, beatTimeMillis;
    boolean isHit = false;
    //    int beatNumerator;
    int index;
    float millisFor4Beats;


    public HitObject(TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm, float millisFor4Beats) {
        super(texRegion);
        this.millisFor4Beats = millisFor4Beats;
        this.index = index;
        beatFloat = (float) beatNumerator / beatDenominator;
        beatTimeMillis = (beatFloat / bpm) * 60000;
    }

//    public void onHit(HitState hitFlag) {
//        BeatMap.hitFlagString = hitFlag.toString();
//        isHit = true;
//        BeatMap.songIndices[index]++;
//    }

    public HitState calculateHit(float difference) {
        if (difference < 16) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[0];
            isHit = true;
            BeatMap.songIndices[index]++;
            return HitState.PERFECT;
        } else if (difference < 37.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[1];
            isHit = true;
            BeatMap.songIndices[index]++;
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[2];
            isHit = true;
            BeatMap.songIndices[index]++;
            return HitState.GREAT;
        } else if (difference < 129.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[3];
            isHit = true;
            BeatMap.songIndices[index]++;
            return HitState.BAD;
        } else if (difference < 300) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            isHit = true;
            BeatMap.songIndices[index]++;
            return HitState.MISS;
        } else {
            return HitState.IDLE;
        }
    }

    public void update(long songTime) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        //if it is a certain distance below the bottom and hasn't been marked as being hit yet, do so
        if (getY() <= GameScreen.BAR_POSITION - 150) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            isHit = true;
            BeatMap.songIndices[index]++;
        }
    }

    public enum HitState {
        IDLE, MISS, BAD, GREAT, EXCELLENT, PERFECT
    }
}
