package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by William on 12/8/2015.
 */
public class HitObject extends Rectangle {
    protected int beatNumerator, beatDenominator, holdDuration;
    protected double beatDouble;

    public HitObject(int beatNumerator, int beatDenominator, int holdDuration) {
        super(0, 0, 256, 82);
        this.beatNumerator = beatNumerator;
        this.beatDenominator = beatDenominator;
        this.holdDuration = holdDuration;
        beatDouble = beatNumerator / beatDenominator;
    }

    public HitObject(int beatNumerator, int beatDenominator) {
        this(beatNumerator, beatDenominator, 0);
    }





}
