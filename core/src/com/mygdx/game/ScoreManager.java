package com.mygdx.game;

/**
 * Created by William on 2/13/2016.
 */
public class ScoreManager {
    static float hitTimeElapsedMillis;
    int score = 0;
    private int combo;
    private int accuracy = 100;
    private int hitObjectsPassed;

    public ScoreManager() {
        score = 0;
        combo = 0;
        accuracy = 100;
        hitObjectsPassed = 0;
    }

    public void incrementScore(HitObject.HitState hitFlag) {
        hitObjectsPassed++;
        switch (hitFlag) {
            case MISS:
                accuracy = (accuracy * (hitObjectsPassed - 1)) / hitObjectsPassed;
                score -= 2000;
                combo = 0;
                break;
            case BAD:
                accuracy = (accuracy * (hitObjectsPassed - 1) + 16) / hitObjectsPassed;
                combo++;
                score += 100;
                break;
            case GREAT:
                accuracy = (accuracy * (hitObjectsPassed - 1) + 33) / hitObjectsPassed;
                combo++;
                score += 300;
                break;
            case EXCELLENT:
                accuracy = (accuracy * (hitObjectsPassed - 1) + 90) / hitObjectsPassed;
                combo++;
                score += 500;
                break;
            case PERFECT:
                accuracy = (accuracy * (hitObjectsPassed - 1) + 100) / hitObjectsPassed;
                combo++;
                score += 1000;
                break;
        }
    }

}