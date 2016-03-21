package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;
import processing.core.PShape;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ClosedCylinder {


    private final PShape closedCylinder;

    public ClosedCylinder(float cylRadius, float cylHeight, int cylResolution, Color color) {

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] y = new float[cylResolution + 1];

        PShape surface = Game.INSTANCE.createShape();
        surface.beginShape();

        for (int i = 0; i < cylResolution + 1; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            y[i] = (float) cos(angle) * cylRadius;
        }

        for (int i = 0; i < cylResolution + 1; i++) {
            surface.vertex(x[i], y[i], 0);
            surface.vertex(0, 0, 0);
        }
        surface.endShape();

        closedCylinder = Game.INSTANCE.createShape(PShape.GROUP);
        closedCylinder.beginShape(PShape.GROUP);
        closedCylinder.addChild(new OpenCylinder(cylRadius, cylHeight, cylResolution, color).getShape());

        closedCylinder.addChild(surface, 0);

        surface.translate(0,0,cylHeight);
        closedCylinder.addChild(surface, 1);

        closedCylinder.rotateX((float) (PI/2));

    }

    public void display() {
        Game.INSTANCE.shape(closedCylinder);
    }
}
