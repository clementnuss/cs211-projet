package processing_files.InteractiveProjections;

import processing.core.PApplet;

/**
 * <p>
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class InteractiveProjections extends PApplet {

    float depth = 500;
    float rx = 0, ry = 0;
    float MAX_ANGLE = PI / 2;
    float MIN_ANGLE = -PI / 2;

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
    }

    public void draw() {
        camera(0, 0, depth, 0, 0, 0, 0, 1, 0);

        ambientLight(205, 181, 153);
        background(200);

        rx = clamp(rx, MIN_ANGLE, MAX_ANGLE);
        ry = clamp(ry, MIN_ANGLE, MAX_ANGLE);

        rotateX(rx);
        rotateY(ry);

        box(160);
    }

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP) {
                rx += PI / 16;
            } else if (keyCode == DOWN) {
                rx -= PI / 16;
            } else if (keyCode == RIGHT) {
                ry += PI / 16;
            } else if (keyCode == LEFT) {
                ry -= PI / 16;
            }
        }
    }

    float clamp(float input, float minValue, float maxValue) {
        if (input > maxValue) return maxValue;
        else if (input < minValue) return minValue;
        else return input;
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{"processing_files.InteractiveProjections.InteractiveProjections"});
    }


}
