/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.physicsEngine;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.tools.Color;
import processing.core.PVector;

import java.util.List;

import static ch.epfl.cs211.tools.ValueUtils.clamp;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.sqrt;

public class Mover {

    private final static float GRAVITY_SCALAR = 0.5f * 9.81f / Game.INSTANCE.frameRate;
    private final static float FRICTION_FACTOR = 0.02f;
    public final static float SPHERE_RADIUS = 20f;
    public final static float CYLINDER_RADIUS = 25f;
    private final static float COLLISION_THRESHOLD = 0.00005f;

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
        bound = plate.getPlateWidth() / 2f - SPHERE_RADIUS;
    }

    public void update() {

        previousPos = pos.copy();

        gravityForce.x = sin(plate.getAngleZ()) * GRAVITY_SCALAR;
        gravityForce.z = sin(-plate.getAngleX()) * GRAVITY_SCALAR;

        velocity.add(gravityForce);
        PVector friction = velocity.copy();
        friction.mult(-FRICTION_FACTOR);

        velocity.add(friction);

        pos.add(velocity);
    }

    public void display() {

        Game.INSTANCE.noStroke();
        Game.INSTANCE.fill(Color.BALL_COLOR);
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

    private void checkEdges() {
        float upperBoundX = plate.getX() + bound;
        float upperBoundZ = plate.getZ() + bound;
        float lowerBoundX = plate.getX() - bound;
        float lowerBoundZ = plate.getZ() - bound;

        if (pos.x > upperBoundX || pos.x < lowerBoundX) {
            pos.x = clamp(pos.x, lowerBoundX, upperBoundX);
            velocity.x = velocity.x * -0.9f;
        }
        if (pos.z > upperBoundZ || pos.z < lowerBoundZ) {
            pos.z = clamp(pos.z, -bound, bound);
            velocity.z = velocity.z * -0.9f;
        }
    }

    private void checkCylinders(List<PVector> cylinders) {
        boolean collisionOccured = false;
        PVector correctedPos = pos.copy();
        PVector correctedVel = velocity.copy();

        float minimumDistance = CYLINDER_RADIUS + SPHERE_RADIUS;

        for (PVector cylinderBaseLocation : cylinders) {

            PVector cyl = cylinderBaseLocation.copy().add(0, -SPHERE_RADIUS, 0);
            float distance = cyl.dist(pos);
            if (distance <= minimumDistance) {
                collisionOccured = true;
                PVector cylToBall = PVector.sub(pos, cyl);
                correctedPos.add(PVector.mult(cylToBall.copy().normalize(), (minimumDistance - cylToBall.mag())));

                PVector collisionNormal = new PVector(pos.x - cyl.x, 0, pos.z - cyl.z).normalize();
                PVector updatedVel = PVector.mult(collisionNormal, 2f * velocity.dot(collisionNormal));
                correctedVel.sub(updatedVel.x, 0, updatedVel.z);
            }
        }

        if(collisionOccured){
            pos.x = correctedPos.x;
            pos.z = correctedPos.z;
            velocity = correctedVel.normalize().mult(velocity.mag()*0.9f);
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

    public PVector getVelocity() {
        return velocity;
    }
}