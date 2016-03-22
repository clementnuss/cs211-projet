package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;
import processing.core.PShape;
import processing.core.PVector;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ClosedCylinder {


    private final PShape closedCylinder;
    private final PVector pos;

    public ClosedCylinder(float cylRadius, float cylHeight, int cylResolution, Color color, PVector pos) {
        this.pos = pos;

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] z = new float[cylResolution + 1];

        PShape bottomSurface = Game.INSTANCE.createShape();
        PShape upSurface = Game.INSTANCE.createShape();
        bottomSurface.beginShape();
        upSurface.beginShape();
//        surface.beginShape(PShape.TRIANGLE_STRIP);

        for (int i = 0; i < cylResolution + 1; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            z[i] = (float) cos(angle)  *cylRadius;
        }

        for (int i = 0; i < cylResolution + 1; i++) {
            bottomSurface.vertex(x[i], 0, z[i]);
            bottomSurface.vertex(0, 0, 0);
            upSurface.vertex(x[i], -cylHeight, z[i]);
            upSurface.vertex(0, -cylHeight, 0);
        }
        bottomSurface.endShape();
        upSurface.endShape();


        closedCylinder = Game.INSTANCE.createShape(PShape.GROUP);
        closedCylinder.beginShape(PShape.GROUP);
        closedCylinder.addChild(new OpenCylinder(cylRadius, cylHeight, cylResolution, color).getShape());

        closedCylinder.addChild(bottomSurface, 0);
        closedCylinder.addChild(upSurface, 1);

        closedCylinder.translate(pos.x, pos.y, pos.z);
    }

    public void display() {
        Game.INSTANCE.shape(closedCylinder);
    }

    public PVector getPos() {
        return pos;
    }
}
