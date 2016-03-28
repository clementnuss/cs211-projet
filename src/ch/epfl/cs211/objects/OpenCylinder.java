/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;
import processing.core.PShape;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class OpenCylinder {

    private final PShape cylinder;

    public OpenCylinder(float cylRadius, float cylHeight, int cylResolution, int color) {

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] z = new float[cylResolution + 1];

        for (int i = 0; i < x.length; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            z[i] = (float) cos(angle) * cylRadius;
        }

        cylinder = Game.INSTANCE.createShape();
        cylinder.beginShape(PShape.QUAD_STRIP);
        cylinder.fill(color);
        cylinder.noStroke();

        for (int i = 0; i < x.length; i++) {
            cylinder.vertex(x[i], 0, z[i]);
            cylinder.vertex(x[i], -cylHeight, z[i]);
        }

        cylinder.endShape();
    }

    public void display() {
        Game.INSTANCE.shape(cylinder);
    }


    public PShape getShape() {
        return cylinder;
    }
}
