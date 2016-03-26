package ch.epfl.cs211.objects;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.tools.Color;

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
    public final static float PLATE_THICKNESS = 20f;
    public final static float PLATE_WIDTH = 500f;

    private int x, y, z;
    private float angleX, angleY, angleZ, angleStep;
    private float savedAngleX, savedAngleY, savedAngleZ;
    private final Color color;

    public Plate(int x, int y, int z, Color color){
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
        this.angleStep = 0.02f;
        this.color = color;
    }

    public void display(){
        Game.INSTANCE.stroke(0,0,0);
        Game.INSTANCE.pushMatrix();
        Game.INSTANCE.translate(x, y, z);
        Game.INSTANCE.rotateX(angleX);
        Game.INSTANCE.rotateZ(angleZ);
        Game.INSTANCE.rotateY(angleY);
        Game.INSTANCE.fill(color.getV1(),color.getV2(), color.getV3(), color.getAlpha());
        Game.INSTANCE.box(PLATE_WIDTH, PLATE_THICKNESS, PLATE_WIDTH);
        Game.INSTANCE.popMatrix();
    }

    public void updateAngle(){

        int deltaX = Game.INSTANCE.mouseX - Game.INSTANCE.pmouseX;
        int deltaY = Game.INSTANCE.mouseY - Game.INSTANCE.pmouseY;


        if(deltaX > 0){
            angleZ = maxClamp(angleZ + angleStep, MAX_ANGLE);
        }else if(deltaX < 0){
            angleZ = minClamp(angleZ - angleStep, -MAX_ANGLE);
        }
        if(deltaY < 0){
            angleX = maxClamp(angleX + angleStep, MAX_ANGLE);
        }else if(deltaY > 0){
            angleX = minClamp(angleX - angleStep, -MAX_ANGLE);
        }
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }

    public void updateSensitivity(int count){
        angleStep = roundThreeDecimals(clamp(angleStep - (count * STEP_VALUE), MIN_STEP_VALUE, MAX_STEP_VALUE));
    }

    public void saveState(){
        savedAngleX = angleX;
        savedAngleY = angleY;
        savedAngleZ = angleZ;
    }

    public float getSavedAngleZ() {
        return savedAngleZ;
    }

    public float getSavedAngleY() {
        return savedAngleY;
    }

    public float getSavedAngleX() {
        return savedAngleX;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getAngleStep() {
        return angleStep;
    }

    public float getPlateThickness(){return PLATE_THICKNESS;}

    public float getPlateWidth(){return PLATE_WIDTH;}
}
