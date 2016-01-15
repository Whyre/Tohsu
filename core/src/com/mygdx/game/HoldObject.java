package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 */
public class HoldObject extends HitObject {
    int holdDurationBeatNumerator;
    float holdDurationMillis;

    public HoldObject(TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm, int holdDuration) {
        super(texRegion, index, beatNumerator, beatDenominator, bpm);
        this.holdDurationBeatNumerator = holdDuration;
        holdDurationMillis = ((float) (holdDurationBeatNumerator * beatDenominator) / bpm) * 60000;
    }
}
