/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */

import processing.core.PShape;
import processing.core.PVector;

import static java.lang.Math.*;

/**
 * Class representing a closed cylinder
 */
public class ClosedCylinder {

    private final PShape closedCylinder;
    private final PShape closedCylinderStroked;
    private final int cylResolution;
    private final int color;
    private final float[] x;
    private final float[] z;

    /**
     *
     * @param cylRadius The radius of the circle upon which the cylinder is built
     * @param cylHeight The height of the cylinder
     * @param cylResolution The number of faces the cylinder will have
     * @param color The color with which to draw the cylinder
     */
    public ClosedCylinder(float cylRadius, float cylHeight, int cylResolution, int color) {

        this.cylResolution = cylResolution;
        this.color = color;

        float angle;
        x = new float[cylResolution + 1];
        z = new float[cylResolution + 1];

        for (int i = 0; i < cylResolution + 1; i++) {
            angle = (float) ((2 * PI) / cylResolution) * i;
            x[i] = (float) sin(angle) * cylRadius;
            z[i] = (float) cos(angle) * cylRadius;
        }

        PShape bottomSurface = createDiskSurface(0);
        PShape bottomSurfaceForStroked = createDiskSurface(0);
        PShape topSurface = createDiskSurface(-cylHeight);
        PShape topSurfaceForStroked = createDiskSurface(-cylHeight);



        closedCylinder = Game.GAME.createShape(PShape.GROUP);
        closedCylinder.addChild(new OpenCylinder(cylRadius, cylHeight, cylResolution, color, false).getShape());
        closedCylinder.addChild(bottomSurface, 0);
        closedCylinder.addChild(topSurface, 1);

        closedCylinderStroked = Game.GAME.createShape(PShape.GROUP);
        closedCylinderStroked.addChild(new OpenCylinder(cylRadius, cylHeight, cylResolution, color, true).getShape());
        closedCylinderStroked.addChild(bottomSurfaceForStroked, 0);
        closedCylinderStroked.addChild(topSurfaceForStroked, 1);

    }

    public void display(PVector pos, boolean withStroke) {
        Game.GAME.pushMatrix();
        Game.GAME.translate(pos.x, pos.y , pos.z);
        if(withStroke)
            Game.GAME.shape(closedCylinderStroked);
        else{
            Game.GAME.shape(closedCylinder);}
        Game.GAME.popMatrix();
    }

    private PShape createDiskSurface(float heightCoord){
        PShape surface = Game.GAME.createShape();
        surface.beginShape(PShape.TRIANGLE_FAN);
        surface.fill(color);
        surface.noStroke();
        surface.vertex(0, heightCoord, 0);


        for (int i = 0; i < cylResolution + 1; i++) {
            surface.vertex(x[i], heightCoord, z[i]);
        }

        surface.endShape();
        return surface;
    }

}