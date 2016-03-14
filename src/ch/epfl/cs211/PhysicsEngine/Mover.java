package ch.epfl.cs211.PhysicsEngine;


import processing.core.PApplet;
import processing.core.PVector;

public class Mover {

    private final PApplet parent;
    PVector location;
    PVector velocity;

    Mover(PApplet p) {
        this.parent = p;
        location = new PVector(parent.width / 2, parent.height / 2);
        velocity = new PVector(1, 1);
    }

    void update() {
        location.add(velocity);
    }

    void display() {
        parent.stroke(0);
        parent.strokeWeight(2);
        parent.fill(127);

        parent.ellipse(location.x, location.y, 48, 48);
    }

    void checkEdges() {
        if (location.x > parent.width) {
            location.x = 0;
        } else if (location.x < 0) {
            location.x = parent.width;
        }
        if (location.y > parent.height) {
            location.y = 0;
        } else if (location.y < 0) {
            location.y = parent.height;
        }
    }
}

