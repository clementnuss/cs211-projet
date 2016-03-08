package ch.epfl.cs211;

/**
 * Sample class showing the use of the Processing library (to include in the project,
 * according to infos found here: https://processing.org/tutorials/eclipse/)
 *
 * Leandro Kieliger, 01.03.16
 */

import processing.core.PApplet;

public class Example2 extends PApplet {

    float depth = 2000;

    public void settings() {
        size(500, 500, P3D);
    }
    public void setup() {
        noStroke();
    }

    public void draw() {
        camera(width/2, height/2, depth, 250, 250, 0, 0, 1, 0);
        directionalLight(50, 100, 125, 0, -1, 0);
        ambientLight(102, 102, 102);
        background(200);
        translate(width/2, height/2, 0);
        float rz = map(mouseY, 0, height, 0, PI);
        float ry = map(mouseX, 0, width, 0, PI);
        rotateZ(rz);
        rotateY(ry);
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    pushMatrix();
                    translate(100 * x, 100 * y, -100 * z);
                    box(50);
                    popMatrix();
                }
            }
        }
    }
    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP) {
                depth -= 50;
            }
            else if (keyCode == DOWN) {
                depth += 50;
            }
        }
    }

    public static void main (String[] args){
        PApplet.main(new String[]{"ch.epfl.cs211.Example2"});
    }
}

