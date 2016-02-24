package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *A Hit Object with a duration
 */
public class HoldObject extends HitObject {
    int holdDurationBeatNumerator;
    boolean isHeld;
    float holdDurationMillis;

    public HoldObject(TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm, int holdDuration, float millisFor4Beats) {
        super(texRegion, index, beatNumerator, beatDenominator, bpm, millisFor4Beats);
        this.holdDurationBeatNumerator = holdDuration;
        holdDurationMillis = (((float) holdDurationBeatNumerator / beatDenominator) / bpm) * 60000;
        this.setSize(texRegion.getRegionWidth(), (holdDurationMillis * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats);
    }

    @Override
    public void update(long songTime) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        if (songTime - beatTimeMillis > holdDurationMillis) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onRelease(HitState.MISS);
        }
    }

    public void onHit() {
        isHeld = true;
        BeatMap.keyHeld[index] = true;
        BeatMap.songIndices[index]++;
        BeatMap.heldObjects[index] = this;
    }

    @Override
    public HitState calculateHit(float difference) {
        if (difference < 16) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[0];
            onHit();
            return HitState.PERFECT;
        } else if (difference < 37.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[1];
            onHit();
            BeatMap.songIndices[index]++;
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[2];
            onHit();
            BeatMap.songIndices[index]++;
            return HitState.GREAT;
        } else if (difference < 129.5) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[3];
            onHit();
            BeatMap.songIndices[index]++;
            return HitState.BAD;
        } else if (difference < 300) {
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
            onHit();
            BeatMap.songIndices[index]++;
            return HitState.MISS;
        } else {
            return HitState.IDLE;
        }

    }

    public HitState calculateRelease(float difference) {
        if (difference < 16) {
            onRelease(HitState.PERFECT);
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[0];
            return HitState.PERFECT;
        } else if (difference < 37.5) {
            onRelease(HitState.EXCELLENT);
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[1];
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
            onRelease(HitState.GREAT);
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[2];
            return HitState.GREAT;
        } else if (difference < 129.5) {
            onRelease(HitState.BAD);
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[3];
            return HitState.BAD;
        } else if (difference < 400) {
            onRelease(HitState.MISS);
            BeatMap.hitFlagString = BeatMap.HITFLAGSTRINGS[4];
        }
        return HitState.MISS;
    }

    public void onRelease(HitState hitFlag) {
        if (!isHeld) {
            onHit(hitFlag);
        }
        BeatMap.keyHeld[index] = false;
        isHit = true;
    }
}
