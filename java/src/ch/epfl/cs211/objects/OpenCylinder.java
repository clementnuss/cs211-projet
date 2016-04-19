/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import processing.core.PShape;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

class OpenCylinder {

    private final PShape cylinder;

    public OpenCylinder(float cylRadius, float cylHeight, int cylResolution, int color, boolean withStroke) {

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] z = new float[cylResolution + 1];

        for (int i = 0; i < x.length; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            z[i] = (float) cos(angle) * cylRadius;
        }

        cylinder = Game.GAME.createShape();
        cylinder.beginShape(PShape.QUAD_STRIP);
        cylinder.fill(color);
        cylinder.strokeWeight(2);
        if(withStroke)
            cylinder.stroke(0);
        else
            cylinder.noStroke();

        for (int i = 0; i < x.length; i++) {
            cylinder.vertex(x[i], 0, z[i]);
            cylinder.vertex(x[i], -cylHeight, z[i]);
        }

        cylinder.endShape();
    }

    public void display() {
        Game.GAME.shape(cylinder);
    }


    public PShape getShape() {
        return cylinder;
    }
}
