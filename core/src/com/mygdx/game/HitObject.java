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
    int index;

    public HitObject(TextureRegion texRegion, int index, int beatNumerator, int beatDenominator, int bpm) {
        super(texRegion);
        this.index = index;
        beatFloat = (float) beatNumerator / beatDenominator;
        beatTimeMillis = (beatFloat / bpm) * 60000;
    }

    public void onHit(int hitFlag) {
        GameScreen.hitFlag = hitFlag;
        isHit = true;
        BeatMap.songIndices[index]++;
//        setRegion(400, 400, 100, 100);
    }

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

    public void update(long songTime, float millisFor4Beats) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        //if it is a certain distance below the bottom and hasn't been marked as being hit yet, do so
        if (getY() <= GameScreen.BAR_POSITION - 150) {
            onHit(3);
        }
    }

    @Override
    public void reset() {
        beatFloat = 0;
        beatTimeMillis = 0;
        isHit = false;
    }
}
