package ch.epfl.cs211.VideoCapture;


import processing.core.PApplet;
import processing.core.PVector;
import java.util.Arrays;
import java.util.List;


public class Quad {

    private final static float MAX_COS = 0.55f; //In radians. Roughly 60 degrees
    private final float QUAD_MAX_AREA = 250000;
    private final float QUAD_MIN_AREA = 40000;

    private final float area;
    private final boolean nonFlat;
    private final boolean isConvex;
    private final boolean hasValidArea;
    private final PApplet parentWindow;

    public PVector c1() {
        return c1;
    }

    public PVector c2() {
        return c2;
    }

    public PVector c3() {
        return c3;
    }

    public PVector c4() {
        return c4;
    }

    public List<PVector> cornersAsList(){
        return Arrays.asList(c1,c2,c3,c4);
    }

    private final PVector c1,c2,c3,c4;

    public Quad(PVector c1, PVector c2, PVector c3, PVector c4, PApplet parentWindow) {
        this.parentWindow = parentWindow;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        PVector v21 = PVector.sub(c1, c2);
        PVector v32 = PVector.sub(c2, c3);
        PVector v43 = PVector.sub(c3, c4);
        PVector v14 = PVector.sub(c4, c1);

        // Expanded cross product
        float i1 = v21.x * v32.y - v21.y * v32.x;
        float i2 = v32.x * v43.y - v32.y * v43.x;
        float i3 = v43.x * v14.y - v43.y * v14.x;
        float i4 = v14.x * v21.y - v14.y * v21.x;
        
        // Cosines calculation
        float v21_mag = v21.mag();
        float v32_mag = v32.mag();
        float v43_mag = v43.mag();
        float v14_mag = v14.mag();

        float cos1 = Math.abs(v21.dot(v32) / (v21_mag * v32_mag));
        float cos2 = Math.abs(v32.dot(v43) / (v32_mag * v43_mag));
        float cos3 = Math.abs(v43.dot(v14) / (v43_mag * v14_mag));
        float cos4 = Math.abs(v14.dot(v21) / (v14_mag * v21_mag));

        area = Math.abs(0.5f * (i1 + i2 + i3 + i4));
        hasValidArea = (QUAD_MIN_AREA < area && area < QUAD_MAX_AREA);
        isConvex = (i1 > 0 && i2 > 0 && i3 > 0 && i4 > 0)
                || (i1 < 0 && i2 < 0 && i3 < 0 && i4 < 0);
        nonFlat = (cos1 < MAX_COS && cos2 < MAX_COS && cos3 < MAX_COS && cos4 < MAX_COS);
    }

    public float getArea() {
        return area;
    }

    public boolean isConvex() {
        return isConvex;
    }

    public boolean isNonFlat() {
        return nonFlat;
    }

    public boolean hasValidArea(){
        return hasValidArea;
    }

    public void drawSurface(){
        parentWindow.fill(parentWindow.color(250, 102, 7));
        parentWindow.quad(c1.x, c1.y, c2.x, c2.y, c3.x, c3.y, c4.x, c4.y);
    }

    public void drawCorners(){
        parentWindow.fill(255, 128, 0);
        parentWindow.ellipse(c1.x, c1.y, 10, 10);
        parentWindow.ellipse(c2.x, c2.y, 10, 10);
        parentWindow.ellipse(c3.x, c3.y, 10, 10);
        parentWindow.ellipse(c4.x, c4.y, 10, 10);
    }
}
