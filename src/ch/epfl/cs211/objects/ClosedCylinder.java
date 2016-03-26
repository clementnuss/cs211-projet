package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Class representing a closed cylinder
 */
public class ClosedCylinder {

    private final PShape closedCylinder;

    /**
     *
     * @param cylRadius The radius of the circle upon which the cylinder is built
     * @param cylHeight The height of the cylinder
     * @param cylResolution The number of faces the cylinder will have
     * @param color The color with which to draw the cylinder
     */
    public ClosedCylinder(float cylRadius, float cylHeight, int cylResolution, int color) {

        float angle;
        float[] x = new float[cylResolution + 1];
        float[] z = new float[cylResolution + 1];

        PShape bottomSurface = Game.INSTANCE.createShape();
        PShape topSurface = Game.INSTANCE.createShape();

        bottomSurface.beginShape(PShape.TRIANGLE_FAN);
        topSurface.beginShape(PShape.TRIANGLE_FAN);

        bottomSurface.fill(color);
        topSurface.fill(color);

        bottomSurface.noStroke();
        topSurface.noStroke();

        bottomSurface.vertex(0, 0, 0);
        topSurface.vertex(0, -cylHeight, 0);

        for (int i = 0; i < cylResolution + 1; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            z[i] = (float) cos(angle) * cylRadius;
        }

        for (int i = 0; i < cylResolution + 1; i++) {
            bottomSurface.vertex(x[i], 0, z[i]);
            topSurface.vertex(x[i], -cylHeight, z[i]);
        }

        bottomSurface.endShape();
        topSurface.endShape();


        closedCylinder = Game.INSTANCE.createShape(PShape.GROUP);
        closedCylinder.addChild(new OpenCylinder(cylRadius, cylHeight, cylResolution, color).getShape());
        closedCylinder.addChild(bottomSurface, 0);
        closedCylinder.addChild(topSurface, 1);
    }

    public void display(PVector pos) {
        Game.INSTANCE.pushMatrix();
        Game.INSTANCE.translate(pos.x, pos.y , pos.z);
        Game.INSTANCE.shape(closedCylinder);
        Game.INSTANCE.popMatrix();
    }

}
