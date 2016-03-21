package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;
import processing.core.PShape;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
public class OpenCylinder {

    private final PShape cylinder;

    public OpenCylinder(float cylRadius, float cylHeight, int cylResolution, Color color) {

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] y = new float[cylResolution + 1];

        for (int i = 0; i < x.length; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            y[i] = (float) cos(angle) * cylRadius;
        }

        cylinder = Game.INSTANCE.createShape();
        cylinder.beginShape(PShape.QUAD_STRIP);

        for (int i = 0; i < x.length; i++) {
            cylinder.vertex(x[i], y[i], 0);
            cylinder.vertex(x[i], y[i], cylHeight);
        }
        cylinder.fill(color.getV1(), color.getV2(), color.getV3(), color.getAlpha());
        cylinder.endShape();
    }

    public void display() {
        Game.INSTANCE.shape(cylinder);
    }


    public PShape getShape() {
        return cylinder;
    }
}
