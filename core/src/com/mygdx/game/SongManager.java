package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by William on 3/7/2016.
 */
public class SongManager {
    private final static String[] HITFLAGSTRINGS = {
        "Perfect!", "Excellent!", "Great!", "Bad!", "Miss!"
    };
    private boolean[] keyHeld;
    private TextureRegion hitObject1, hitObject2, holdObject1;
    private int[] songIndices; //the index of the first hitobject that has not yet reached the strum bar
    private HoldObject[] heldObjects;
    private String hitFlagString;

    public SongManager() {
        keyHeld = new boolean[4];
        songIndices = new int[4];
        heldObjects  = new HoldObject[4];
    }

    public int getSongIndex(int i) {
        return songIndices[i];
    }

    public void incrementSongIndex(int i) {
        songIndices[i]++;
    }

    public void resetSongIndices() {
        for (int i = 0; i < 4; i++) {
            songIndices[i] = 0;
        }
    }

    public void holdKey(int i) {
        keyHeld[i] = true;
    }

    public void releaseKey(int i) {
        keyHeld[i] = false;
    }

    public boolean isKeyHeld(int i) {
        return keyHeld[i];
    }

    public void setHeldObject(HoldObject ho, int i) {
        heldObjects[i] = ho;
    }

    public HoldObject getHeldObject(int i) {
        return heldObjects[i];
    }

    public void setHitFlagString(int i) {
        hitFlagString = HITFLAGSTRINGS[i];
    }

    public String getHitFlagString() {
        return hitFlagString;
    }
}
