package ch.epfl.cs211.physicsEngineTMP;


import ch.epfl.cs211.Game;
import ch.epfl.cs211.objects.Plate;
import processing.core.PApplet;
import processing.core.PVector;

import static ch.epfl.cs211.tools.ValueUtils.*;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.tan;

public class Mover {

    private final static PVector GRAVITY_VECTOR = new PVector(0,0.5f,0);
    private final static float GRAVITY_SCALAR = 9.81f;
    private float x;
    private float y;

    private float z;
    private final Plate plate;
    private final PApplet parent;
    private final float bound;
    private PVector location;
    private PVector velocity;
    private PVector gravityForce;


    public Mover(float x, float y, float z, Plate pl, PApplet p) {
        this.plate = pl;
        this.parent = p;
        this.x = x;
        this.y = y+100f;
        this.z = z;
        location = new PVector(x,y,z);
        velocity = new PVector(0,0,0);
        gravityForce = new PVector(0,0,0);
        bound = plate.getPlateWidth()/2;
    }

    public void update() {
        gravityForce.x = sin(plate.getAngleZ()) * GRAVITY_SCALAR;
        gravityForce.z = sin(plate.getAngleX()) * GRAVITY_SCALAR;

        velocity.add(gravityForce);

        float normalForce = 1;
        float mu = 0.01f;
        float frictionMagnitude = normalForce * mu;
        PVector friction = velocity;
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMagnitude);

        velocity.add(friction);

        float phi = plate.getAngleZ();
        float theta = -plate.getAngleX();

        if(Game.DEBUG){
        System.out.println("Phi (angle Z)= "+phi+
                            "\nTheta (angle X)= "+ theta+
                            "\n x-contribution= "+ (-tan(phi)*x) +
                            "\n z-contribution= "+(-tan(theta)*z));
        }

        x += velocity.x;
        y = (-tan(phi)*x) - (tan(theta)*z);
        z += velocity.z;
    }

    public void display() {
        parent.pushMatrix();
        parent.translate(x,y,z);
        parent.sphere(10f);
        parent.popMatrix();

    }

    public void checkEdges() {
        if (location.x > bound || location.x < -bound) {
            location.x = clamp(location.x, -bound, bound);
            velocity.x = velocity.x * -1;
        }
        if (location.z > bound || location.z < -bound) {
            location.z = clamp(location.z, -bound, bound);
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

