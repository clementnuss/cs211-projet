package ch.epfl.cs211.objects;

import ch.epfl.cs211.tools.Color;
import processing.core.PApplet;
import static java.lang.Math.*;
import static ch.epfl.cs211.tools.ValueUtils.*;

public class Plate {

    private final static float MAX_ANGLE = (float)(PI/6.0);

    /*  STEP_VALUE is the amount by which we increase / decrease
        the angle when the mouse is dragged
     */
    private final static float STEP_VALUE = 0.005f;
    private final static float MIN_STEP_VALUE = 0.01f;
    private final static float MAX_STEP_VALUE = 0.05f;

    private int x, y, z;
    private float angleX, angleY, angleZ, angleStep;
    private final Color color;
    private final PApplet parent;

    public Plate(int x, int y, int z, Color color, PApplet p){
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = p;
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
        this.angleStep = 0.02f;
        this.color = color;
    }

    public void display(){
        parent.pushMatrix();
        parent.translate(x, y, z);
        parent.rotateX(angleX);
        parent.rotateZ(angleZ);
        parent.rotateY(angleY);
        parent.fill(color.getV1(),color.getV2(), color.getV3(), color.getAlpha());
        parent.box(100, 5, 100);
        parent.popMatrix();
    }

    public void updateAngle(){

        int deltaX = parent.mouseX - parent.pmouseX;
        int deltaY = parent.mouseY - parent.pmouseY;


        if(deltaX < 0){
            angleZ = minClamp(angleZ - angleStep, -MAX_ANGLE);
        }else if(deltaX > 0){
            angleZ = maxClamp(angleZ + angleStep, MAX_ANGLE);
        }
        if(deltaY < 0){
            angleX = maxClamp(angleX + angleStep, MAX_ANGLE);
        }else if(deltaY > 0){
            angleX = minClamp(angleX - angleStep, -MAX_ANGLE);
        }
    }

    public void updateSensitivity(int count){
        angleStep = clamp(angleStep + (count * STEP_VALUE), MIN_STEP_VALUE, MAX_STEP_VALUE);
    }

    public float getAngleX() {
        return angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public float getAngleStep() {
        return angleStep;
    }
}
