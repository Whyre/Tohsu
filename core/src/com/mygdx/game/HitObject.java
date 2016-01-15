package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * Work in progress
 */
public class HitObject extends Sprite implements Pool.Poolable {
    float beatFloat, beatTimeMillis;
    boolean isHit = false;
    //    int beatNumerator;
    private int index;

    public HitObject(TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm) {
        super(texRegion);
        this.index = index;
        beatFloat = (float) beatNumerator / beatDenominator;
        beatTimeMillis = (beatFloat / bpm) * 60000;
    }

    public void onHit(int hitFlag) {
        GameScreen.hitFlag = hitFlag;
        isHit = true;
        GameScreen.songIndices[index]++;
        setRegion(400, 400, 100, 100);
    }

    public void update(long songTime, float millisFor4Beats) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        if (getY() <= GameScreen.BAR_POSITION - 150) {
            GameScreen.hitFlag = 3;
            if (!isHit) {
                GameScreen.songIndices[index]++;
                isHit = true;
            }
        }
    }

    @Override
    public void reset() {
        beatFloat = 0;
        beatTimeMillis = 0;
        isHit = false;
    }
}
