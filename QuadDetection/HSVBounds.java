/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */

public class HSVBounds {

    HSVBounds() {

        h_min = 91;
        h_max = 141;
        s_min = 62;
        s_max = 255;
        v_min = 31;
        v_max = 183;
        intensity = 195;
    }

    public float getH_min() {
        return h_min;
    }

    public float getS_min() {
        return s_min;
    }

    public float getV_min() {
        return v_min;
    }

    public float getH_max() {
        return h_max;
    }

    public float getS_max() {
        return s_max;
    }

    public float getV_max() {
        return v_max;
    }

    public float getIntensity(){ return intensity; }


    public void setH_min(float h_min) {
        if (h_min >= 0 && h_min <= h_max)
            this.h_min = h_min;
    }

    public void setS_min(float s_min) {
        if (s_min >= 0 && s_min <= s_max)
            this.s_min = s_min;
    }

    public void setV_min(float v_min) {
        if (v_min >= 0 && v_min <= v_max)
            this.v_min = v_min;
    }

    public void setH_max(float h_max) {
        if (h_max <= 255 && h_max > h_min)
            this.h_max = h_max;
    }

    public void setS_max(float s_max) {
        if (s_max <= 255 && s_max > s_min)
            this.s_max = s_max;
    }

    public void setV_max(float v_max) {
        if (v_max <= 255 && v_max > v_min)
            this.v_max = v_max;
    }

    public void set_intensity(float intensity){
        if(0 < intensity && intensity < 255)
            this.intensity = intensity;
    }

    private float h_min, s_min, v_min,
            h_max, s_max, v_max, intensity;

    public String toString() {
        return "Hue : [" + h_min + "," + h_max + "]\t" +
                "Saturation : [" + s_min + "," + s_max + "]\t" +
                "Value : [" + v_min + "," + v_max + "]" +
                "Intensity: ["+intensity+"]";
    }

}