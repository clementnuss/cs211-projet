/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
package ProcessingFiles.VideoCapture;

public class HSVBounds {

    HSVBounds() {

        h_min = 0;
        h_max = 360;
        s_min = 0;
        v_min = 0;
        s_max = 100;
        v_max = 100;
    }

    public int getH_min() {
        return h_min;
    }

    public int getS_min() {
        return s_min;
    }

    public int getV_min() {
        return v_min;
    }

    public int getH_max() {
        return h_max;
    }

    public int getS_max() {
        return s_max;
    }

    public int getV_max() {
        return v_max;
    }


    public void setH_min(int h_min) {
        this.h_min = h_min;
    }

    public void setS_min(int s_min) {
        this.s_min = s_min;
    }

    public void setV_min(int v_min) {
        this.v_min = v_min;
    }

    public void setH_max(int h_max) {
        this.h_max = h_max;
    }

    public void setS_max(int s_max) {
        this.s_max = s_max;
    }

    public void setV_max(int v_max) {
        this.v_max = v_max;
    }

    private int h_min, s_min, v_min,
            h_max, s_max, v_max;

    public String toString() {
        String sb = "Hue : [" + h_min + "," + h_max + "]\t" +
                "Saturation : [" + s_min + "," + s_max + "]\t" +
                "Value : [" + v_min + "," + v_max + "]";
        return sb;
    }

}
