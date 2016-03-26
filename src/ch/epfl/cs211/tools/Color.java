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


    public final static int BALL_COLOR = 0xFFffed5d;
    public final static int CYLINDER_COLOR = 0xffd0d0ce;
    public final static int STROKE_COLOR = 0xff1C1300;
    public final static int PLATE_COLOR = 0x7f24C500;

    /**
     *
     * @param v1    First color value (either the Red component or the Hue component)
     * @param v2    Second color value (either the Green component or the Saturation component)
     * @param v3    Third color value (either the Blue component or the Value component)
     * @param alpha Transparency [0 - 255]
     */
    public Color(float v1, float v2, float v3, float alpha) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.alpha = alpha;
    }

    /**
     * This constructor creates a color with alpha value 255.
     *
     * @param v1    First color value (either the Red component or the Hue component)
     * @param v2    Second color value (either the Green component or the Saturation component)
     * @param v3    Third color value (either the Blue component or the Value component)
     */
    public Color(float v1, float v2, float v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.alpha = 255f;
    }

    public int rgb() {
        int rgb = Math.round(alpha);
        rgb = (rgb << 8) | Math.round(v1);
        rgb = (rgb << 8) | Math.round(v2);
        rgb = (rgb << 8) | Math.round(v3);
        return rgb;
    }

    /**
     *
     * @return  the first color value
     */
    public float getV1() {
        return v1;
    }

    /**
     *
     * @return the second  color value
     */
    public float getV2() {
        return v2;
    }

    /**
     *
     * @return the third color value
     */
    public float getV3() {
        return v3;
    }

    /**
     *
     * @return the alpha value of the color
     */
    public float getAlpha() {
        return alpha;
    }

}

