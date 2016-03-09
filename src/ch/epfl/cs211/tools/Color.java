package ch.epfl.cs211.tools;

/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class Color {
    private final float v1;
    private final float v2;
    private final float v3;
    private final float alpha;

    /**
     *
     * @param v1
     * @param v2
     * @param v3
     * @param alpha Transparency [0 - 255]
     */
    public Color(float v1, float v2, float v3, float alpha) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.alpha = alpha;
    }

    public Color(float v1, float v2, float v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.alpha = 255f;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getV3() {
        return v3;
    }

    public float getV1() {
        return v1;
    }

    public float getV2() {
        return v2;
    }

}

