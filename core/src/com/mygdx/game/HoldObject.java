package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *A Hit Object with a duration
 */
public class HoldObject extends HitObject {
    int holdDurationBeatNumerator;
    boolean isHeld;
    float holdDurationMillis;

    public HoldObject(SongManager sm, TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm, int holdDuration, float millisFor4Beats) {
        super(sm, texRegion, index, beatNumerator, beatDenominator, bpm, millisFor4Beats);
        this.holdDurationBeatNumerator = holdDuration;
        holdDurationMillis = ((float) (holdDurationBeatNumerator * 60000) / (beatDenominator * bpm));
        this.setSize(texRegion.getRegionWidth(), (holdDurationMillis * BeatMap.HIT_OBJECT_DISTANCE) / millisFor4Beats);
    }

    @Override
    public void update(long songTime) {
        setY((BeatMap.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * BeatMap.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        if (songTime - beatTimeMillis > holdDurationMillis) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onRelease(4);
        }
    }

    @Override
    public void onHit(int i) {
        isHeld = true;
//        BeatMap.keyHeld[index] = true;
//        BeatMap.songIndices[index]++;
//        BeatMap.heldObjects[index] = this;
        songManager.holdKey(index);
        songManager.incrementSongIndex(index);
        songManager.setHeldObject(this, index);
        songManager.setHitFlagString(i);
    }

    public void onRelease(int i) {
        if (!isHeld) {
            onHit(i);
        }
//        BeatMap.keyHeld[index] = false;
        songManager.releaseKey(index);
        isHit = true;
    }

    @Override
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

    public HitState calculateRelease(float songTime) {
        float difference = Math.abs(beatTimeMillis + holdDurationMillis - songTime);
        if (difference < 16) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[0];
            onRelease(0);
            return HitState.PERFECT;
        } else if (difference < 37.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[1];
            onRelease(1);
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[2];
            onRelease(2);
            return HitState.GREAT;
        } else if (difference < 129.5) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[3];
            onRelease(3);
            return HitState.BAD;
        } else if (difference < 300) {
//            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onRelease(4);
            return HitState.MISS;
        } else {
            return HitState.IDLE;
        }
    }
}
