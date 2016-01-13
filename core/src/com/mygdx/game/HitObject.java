package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Work in progress
 */
public class HitObject extends Sprite {
    protected int beatNumerator, beatDenominator, holdDuration;
    protected float beatFloat, beatTimeMillis;

    public HitObject(TextureRegion texRegion, int beatNumerator, int beatDenominator, int bpm, int holdDuration) {
        super(texRegion);
        this.beatNumerator = beatNumerator;
        this.beatDenominator = beatDenominator;
        this.holdDuration = holdDuration;
        beatFloat = beatNumerator / beatDenominator;
        beatTimeMillis = (beatFloat / bpm) * 60000;
    }

    public HitObject(TextureRegion texRegion, int beatNumerator, int beatDenominator, int bpm) {
        this(texRegion, beatNumerator, beatDenominator, bpm, 0);
    }





}
