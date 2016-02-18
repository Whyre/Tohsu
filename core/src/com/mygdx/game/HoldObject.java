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
        super(texRegion, index, beatNumerator, beatDenominator, bpm);
        this.holdDurationBeatNumerator = holdDuration;
        holdDurationMillis = (((float) holdDurationBeatNumerator / beatDenominator) / bpm) * 60000;
        this.setSize(texRegion.getRegionWidth(), (holdDurationMillis * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats);
    }

    @Override
    public void update(long songTime, float millisFor4Beats) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        if (songTime - beatTimeMillis > holdDurationMillis) {
            onRelease(HitState.MISS);
        }
    }

    @Override
    public void onHit(HitState hitFlag) {
        BeatMap.hitFlagString = hitFlag.toString();
        isHeld = true;
        BeatMap.keyHeld[index] = true;
        BeatMap.songIndices[index]++;
        BeatMap.heldObjects[index] = this;
    }

    public HitState calculateRelease(float difference) {
        if (difference < 16) {
            onRelease(HitState.PERFECT);
            return HitState.PERFECT;
        } if (difference < 37.5) {
            onRelease(HitState.EXCELLENT);
            return HitState.EXCELLENT;
        } else if (difference < 83.5) {
            onRelease(HitState.GREAT);
            return HitState.GREAT;
        } else if (difference < 129.5) {
            onRelease(HitState.BAD);
            return HitState.BAD;
        } else if (difference < 400) {
            onRelease(HitState.MISS);
        }
        return HitState.MISS;
    }

    public void onRelease(HitState hitFlag) {
        if (!isHeld) {
            onHit(hitFlag);
        }
        BeatMap.keyHeld[index] = false;
        GameScreen.hitFlagString = hitFlag.toString();
        isHit = true;
    }
}
