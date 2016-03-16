package ch.epfl.cs211.physicsEngine;


import ch.epfl.cs211.Game;
import ch.epfl.cs211.objects.Plate;
import processing.core.PVector;

import static ch.epfl.cs211.tools.ValueUtils.clamp;
import static processing.core.PApplet.sin;

public class Mover {

    private final static PVector GRAVITY_VECTOR = new PVector(0, 0.5f, 0);
    private final static float GRAVITY_SCALAR = 0.05f;
    private final static float SPHERE_RADIUS = 4f;
    private float x;
    private float y;

    private float z;
    private final Plate plate;
    private final float bound;
    private PVector velocity;
    private PVector gravityForce;


    public Mover(Plate pl) {
        this.plate = pl;
        this.x = plate.getX();
        this.y = -(plate.getPlateThickness()/2f + SPHERE_RADIUS);
        this.z = plate.getZ();
        velocity = new PVector(0, 0, 0);
        gravityForce = new PVector(0, 0, 0);
        bound = plate.getPlateWidth() / 2 - SPHERE_RADIUS;
    }

    public void update() {
        gravityForce.x = sin(plate.getAngleZ()) * GRAVITY_SCALAR;
        gravityForce.z = sin(-plate.getAngleX()) * GRAVITY_SCALAR;

        velocity.add(gravityForce);

        float normalForce = 1;
        float mu = 0.01f;
        float frictionMagnitude = normalForce * mu;
        PVector friction = velocity.copy();
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMagnitude);

        velocity.add(friction);

        float phi = plate.getAngleZ();
        float theta = -plate.getAngleX();

        x += velocity.x;
        z += velocity.z;
    }

    public void display() {
        Game.INSTANCE.pushMatrix();
        Game.INSTANCE.rotateX(plate.getAngleX());
        Game.INSTANCE.rotateY(plate.getAngleY());
        Game.INSTANCE.rotateZ(plate.getAngleZ());
        Game.INSTANCE.translate(x, y, z);
        Game.INSTANCE.sphere(SPHERE_RADIUS);
        Game.INSTANCE.popMatrix();

    }

    public void checkEdges() {
        float upperBoundX = plate.getX() + bound;
        float upperBoundZ = plate.getZ() + bound;
        float lowerBoundX = plate.getX() - bound;
        float lowerBoundZ = plate.getZ() - bound;

        if (x > upperBoundX|| x < lowerBoundX) {
            x = clamp(x, lowerBoundX, upperBoundX);
            velocity.x = velocity.x * -1;
        }
        if (z > plate.getZ()+bound || z < plate.getZ()-bound) {
            z = clamp(z, -bound, bound);
            velocity.z = velocity.z * -1;
        }
    }

    public float getZ() {
        return z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}

