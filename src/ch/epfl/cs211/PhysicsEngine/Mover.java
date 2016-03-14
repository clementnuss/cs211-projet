package ch.epfl.cs211.PhysicsEngine;


import processing.core.PApplet;
import processing.core.PVector;

public class Mover {

    private final PApplet parent;
    PVector location;
    PVector velocity;

    public Mover(PApplet p) {
        this.parent = p;
        location = new PVector(parent.width / 2, parent.height / 2);
        velocity = new PVector(1.5f, 1);
    }

    public void update() {
        location.add(velocity);
    }

    public void display() {
        parent.stroke(0);
        parent.strokeWeight(2);
        parent.fill(127);

        parent.ellipse(location.x, location.y, 48, 48);
    }

    public void checkEdges() {
        if (location.x > parent.width || location.x < 0) {
            velocity.x = velocity.x * -1;
        }
        if (location.y > parent.height || location.y < 0) {
            velocity.y = velocity.y * -1;
        }
    }
}

