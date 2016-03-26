package ch.epfl.cs211.physicsEngine;


import ch.epfl.cs211.Game;
import ch.epfl.cs211.objects.Plate;
import processing.core.PVector;

import java.util.List;

import static ch.epfl.cs211.tools.ValueUtils.clamp;
import static processing.core.PApplet.sin;

public class Mover {

    private final static float GRAVITY_SCALAR = 0.1f;
    private final static float SPHERE_RADIUS = 20f;
    public final static float CYLINDER_RADIUS = 25f;
    private final static float COLLISION_THRESHOLD = 1f;

    private PVector pos;
    private PVector previousPos;

    private final Plate plate;
    private final float bound;
    private PVector velocity;
    private PVector gravityForce;


    public Mover(Plate pl) {
        this.plate = pl;
        this.pos = new PVector(plate.getX(), -(plate.getPlateThickness() / 2f + SPHERE_RADIUS), plate.getZ());
        velocity = new PVector(0, 0, 0);
        gravityForce = new PVector(0, 0, 0);
        bound = plate.getPlateWidth() / 2 - SPHERE_RADIUS;
    }

    public void update() {

        previousPos = pos.copy();

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

        pos.add(velocity);
    }

    public void display() {
        Game.INSTANCE.stroke(255, 174, 0);

        Game.INSTANCE.pushMatrix();
        Game.INSTANCE.rotateX(plate.getAngleX());
        Game.INSTANCE.rotateY(plate.getAngleY());
        Game.INSTANCE.rotateZ(plate.getAngleZ());
        Game.INSTANCE.translate(pos.x, pos.y, pos.z);
        Game.INSTANCE.sphere(SPHERE_RADIUS);
        Game.INSTANCE.popMatrix();

    }

    public void checkCollisions(List<PVector> cylinders) {
        checkEdges();
        checkCylinders(cylinders);
    }

    public void checkEdges() {
        float upperBoundX = plate.getX() + bound;
        float upperBoundZ = plate.getZ() + bound;
        float lowerBoundX = plate.getX() - bound;
        float lowerBoundZ = plate.getZ() - bound;

        if (pos.x > upperBoundX || pos.x < lowerBoundX) {
            pos.x = clamp(pos.x, lowerBoundX, upperBoundX);
            velocity.x = velocity.x * -1;
        }
        if (pos.z > plate.getZ() + bound || pos.z < plate.getZ() - bound) {
            pos.z = clamp(pos.z, -bound, bound);
            velocity.z = velocity.z * -1;
        }
    }

    private void checkCylinders(List<PVector> cylinders) {

        PVector ball = pos.copy();

        for (PVector cyl : cylinders) {
            float distance = cyl.dist(pos);
            if (distance < CYLINDER_RADIUS + SPHERE_RADIUS) {
                //The amount by which the ball entered the cylinder
                float illegalCrossingDistance = CYLINDER_RADIUS + SPHERE_RADIUS - distance;
                System.out.println("illegal crossing dist: " + illegalCrossingDistance);

                //Compute the point where the ball should have stopped
                PVector correctedPos = previousPos.copy().sub(pos).normalize();
                correctedPos.mult(illegalCrossingDistance);
                System.out.format("Pos was: x: %.2f, y: %.2f, z: %.2f\n", pos.x, pos.y, pos.z);
                System.out.format("Correction is x: %.2f, y: %.2f, z: %.2f\n", correctedPos.x, correctedPos.y, correctedPos.z);
                pos.add(correctedPos.x,0,correctedPos.z);
                previousPos = pos.copy();

                PVector collisionNormal = pos.copy().sub(cyl).normalize();
                PVector updatedVel = collisionNormal.copy().mult(1.9f).mult(velocity.copy().dot(collisionNormal));
                velocity.sub(updatedVel.x,0,updatedVel.y);
            }
        }
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getZ() {
        return pos.z;
    }

    public PVector getPosition() {
        return pos;
    }
}

