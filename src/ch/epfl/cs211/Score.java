package ch.epfl.cs211;

import static java.lang.Math.abs;

/**
 * Created by clement on 05.04.2016.
 * <p>
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class Score {

    private float score;
    private final float coefficient = 3f;
    public final static float MAX_SCORE = 100f;
    public final static float MIN_SCORE = 0;

    public Score() {
        score = 0;
    }

    public void incScore(float velocity) {
        float chg = abs(velocity) * coefficient;
        if (score + chg > MAX_SCORE)
            score = MAX_SCORE;
        else
            score += chg;
    }

    public void decScore(float velocity) {
        float chg = abs(velocity) * coefficient;
        if (score - chg < MIN_SCORE)
            score = MIN_SCORE;
        else
            score -= chg;
    }

    public float getScore() {
        return score;
    }

}
