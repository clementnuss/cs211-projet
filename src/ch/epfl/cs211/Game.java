package ch.epfl.cs211;

import processing.core.PApplet;


/**
 * Created by Clément Nussbaumer on 08.03.2016.
 *
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
public class Game extends PApplet {

    public static void main(String[] args) {
        PApplet.main(new String[]{"ch.epfl.cs211.Game"});
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        noStroke();
    }

    public void draw() {
    }

}
