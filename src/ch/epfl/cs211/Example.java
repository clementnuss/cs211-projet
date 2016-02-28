package ch.epfl.cs211;

/**
 * Sample class showing the use of the Processing library (to include in the project,
 * according to infos found here: https://processing.org/tutorials/eclipse/)
 *
 * Clément Nussbaumer, 01.03.16
 */

import processing.core.PApplet;

public class Example extends PApplet {

    Stripe[] stripes = new Stripe[50];

    public void settings() {
        size(1000, 500, P2D);
    }

    public void setup() {
        // Initialize all "stripes"
        for (int i = 0; i < stripes.length; i++) {
            stripes[i] = new Stripe(this);
        }
    }

    public void draw() {
        background(100);
        // Move and display all "stripes"
        for (int i = 0; i < stripes.length; i++) {
            stripes[i].move();
            stripes[i].display();
        }
    }

    public static void main (String[] args){
        PApplet.main(new String[]{"ch.epfl.cs211.Example"});
    }
}

