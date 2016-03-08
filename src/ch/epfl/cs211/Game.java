package ch.epfl.cs211;

import ch.epfl.cs211.objects.Plate;
import processing.core.PApplet;


/**
 * Created by Clément Nussbaumer on 08.03.2016.
 *
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
public class Game extends PApplet {

    private Plate p;

    public static void main(String[] args) {
        PApplet.main(new String[]{"ch.epfl.cs211.Game"});
    }

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
        noStroke();
        p = new Plate(width / 2, height / 2, 0, this);
    }

    public void draw() {
        camera(width / 2, height / 2 - 50, 200, 250, 250, 0, 0, 1, 0);
        directionalLight(50, 100, 125, 0, -1, 0);
        ambientLight(102, 102, 102);
        background(200);
        p.display();
    }

}
