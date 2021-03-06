/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.objects;

import ch.epfl.cs211.tools.Color;
import processing.core.PVector;

import static ch.epfl.cs211.Game.*;
import static ch.epfl.cs211.tools.ValueUtils.*;

public class Plate {

    public final static float PLATE_THICKNESS = 20f;
    public final static float PLATE_WIDTH = 500f;
    private final static float MAX_ANGLE = (float)(PI/6.0);
    /*  STEP_VALUE is the amount by which we increase / decrease
        the angle when the mouse is dragged
     */
    private final static float STEP_VALUE = 0.05f;
    private final static float ANGLE_VALUE = 0.02f;
    private final static float MIN_STEP_VALUE = 0.1f;
    private final static float MAX_STEP_VALUE = 0.5f;
    private final int x;
    private final int y;
    private final int z;
    private final int color;
    private float angleX, angleY, angleZ, sensitivity;
    private float savedAngleX, savedAngleY, savedAngleZ;

    public Plate(int x, int y, int z, int color){
        this.x = x;
        this.y = y;
        this.z = z;
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
        this.sensitivity = (MAX_STEP_VALUE + MIN_STEP_VALUE) / 2;
        this.color = color;
    }

    public void display(){
        GAME.stroke(Color.STROKE_COLOR);
        GAME.strokeWeight(2f);
        GAME.pushMatrix();
        GAME.translate(x, y, z);
        GAME.rotateX(angleX);
        GAME.rotateY(angleY);
        GAME.rotateZ(angleZ);

        GAME.fill(color);
        GAME.box(PLATE_WIDTH, PLATE_THICKNESS, PLATE_WIDTH);
        GAME.popMatrix();
    }

    public void updateAngle(){

        int deltaX = GAME.mouseX - GAME.pmouseX;
        int deltaY = GAME.mouseY - GAME.pmouseY;

        float amountX = abs(deltaX * sensitivity * ANGLE_VALUE);
        float amountY = abs(deltaY * sensitivity * ANGLE_VALUE);

        if(deltaX > 0){
            angleZ = maxClamp(angleZ + amountX, MAX_ANGLE);
        }else if(deltaX < 0){
            angleZ = minClamp(angleZ - amountX, -MAX_ANGLE);
        }
        if(deltaY < 0){
            angleX = maxClamp(angleX + amountY, MAX_ANGLE);
        }else if(deltaY > 0){
            angleX = minClamp(angleX - amountY, -MAX_ANGLE);
        }

    }

    public void updateSensitivity(int count){
        sensitivity = roundThreeDecimals(clamp(sensitivity - (count * STEP_VALUE), MIN_STEP_VALUE, MAX_STEP_VALUE));
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

    public float getSensitivity() {
        return sensitivity;
    }

    public void setVertical(){
        angleX = -PI/2f;
        angleY = 0;
        angleZ = 0;
    }

    public void restoreState(){
        angleX = savedAngleX;
        angleY = savedAngleY;
        angleZ = savedAngleZ;
    }

    public void saveState(){
        savedAngleX = angleX;
        savedAngleY = angleY;
        savedAngleZ = angleZ;
    }

    public void setRotation(PVector rot){
        angleX = -rot.x;
        //angleY = -rot.y;
        angleZ = -rot.y;
    }
}
