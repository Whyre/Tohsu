package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *A Hit Object with a duration
 */
public class HoldObject extends HitObject {
    int holdDurationBeatNumerator;
    boolean isReleased, isHeld;
    float holdDurationMillis;
    private boolean atBottom;

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
            onRelease(3);
        }
    }

    @Override
    public void onHit(int hitFlag) {
        GameScreen.hitFlag = hitFlag;
        isHeld = true;
        BeatMap.keyHeld[index] = true;
        BeatMap.songIndices[index]++;
        BeatMap.heldObjects[index] = this;
    }

    @Override
    public boolean calculateHit(float difference) {
        if (difference < 37.5) {
            onHit(0);
            return true;
        } else if (difference < 83.5) {
            onHit(1);
            return true;
        } else if (difference < 129.5) {
            onHit(2);
            return true;
        } else if (difference < 400) {
            onHit(3);
        }
        return false;
    }

    public boolean calculateRelease(float difference) {
        if (difference < 37.5) {
            onRelease(0);
            return true;
        } else if (difference < 83.5) {
            onRelease(1);
            return true;
        } else if (difference < 129.5) {
            onRelease(2);
            return true;
        } else if (difference < 400) {
            onRelease(3);
        }
        return false;
    }

    public void onRelease(int hitFlag) {
        if (!isHeld) {
            onHit(hitFlag);
        }
        BeatMap.keyHeld[index] = false;
        GameScreen.hitFlag = hitFlag;
        isHit = true;
    }
}
