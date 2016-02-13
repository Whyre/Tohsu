package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.Map;

/**
 * Work in progress
 */
public class HitObject extends Sprite implements Pool.Poolable {
    final static Map<HitState, Integer> hitStateMap = new HashMap<>();
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

    public void onHit(HitState hitFlag) {
        GameScreen.hitFlag = hitFlag;
        GameScreen.hitTimeElapsedMillis = 0;
        isHit = true;
        BeatMap.songIndices[index]++;
//        setRegion(400, 400, 100, 100);
    }

    public boolean calculateHit(float difference) {
        if (difference < 37.5) {
            onHit(HitState.PERFECT);
            return true;
        } else if (difference < 83.5) {
            onHit(HitState.GREAT);
            return true;
        } else if (difference < 129.5) {
            onHit(HitState.BAD);
            return true;
        } else if (difference < 400) {
            onHit(HitState.MISS);
        }
        return false;
    }

    public void update(long songTime, float millisFor4Beats) {
        setY((GameScreen.BAR_POSITION + ((beatTimeMillis - songTime + GameScreen.visualOffsetMillis) * GameScreen.HIT_OBJECT_DISTANCE) / millisFor4Beats));
        //if it is a certain distance below the bottom and hasn't been marked as being hit yet, do so
        if (getY() <= GameScreen.BAR_POSITION - 150) {
            onHit(HitState.MISS);
        }
    }

    @Override
    public void reset() {
        beatFloat = 0;
        beatTimeMillis = 0;
        isHit = false;
    }

    public enum HitState {
        IDLE, MISS, BAD, GREAT, PERFECT
    }
}
