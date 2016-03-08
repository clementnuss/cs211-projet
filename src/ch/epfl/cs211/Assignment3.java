package ch.epfl.cs211;

import processing.core.PApplet;


/**
 * Created by Clément Nussbaumer on 08.03.2016.
 */
public class Assignment3 extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[]{"ch.epfl.cs211.Assignment3"});
    }

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
        noStroke();
    }

    public void draw() {
        background(200);
        pushMatrix();
        translate(width / 2, height / 2, 0);
        rotateX(PI / 8);
        rotateY(PI / 8);
        box(100, 80, 60);
        fill(0xff9a00);
        translate(100, 0, 0);
        sphere(50);
        popMatrix();
        box(100, 80, 60);

        camera(mouseX, mouseY, 450, 250, 250, 0, 0, 1, 0);
        translate(width/2, height/2, 0);
        rotateX(PI/8);
        rotateY(PI/8);
        box(100, 80, 60);
        translate(100, 0, 0);
        sphere(50);

    }

}
