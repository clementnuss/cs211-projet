/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */

import processing.core.PVector;

public class Plate {

    private final static float MAX_ANGLE = (float)(TangibleGame.GAME.PI/6.0);

    /*  STEP_VALUE is the amount by which we increase / decrease
        the angle when the mouse is dragged
     */
    private final static float STEP_VALUE = 0.05f;
    private final static float ANGLE_VALUE = 0.02f;
    private final static float MIN_STEP_VALUE = 0.1f;
    private final static float MAX_STEP_VALUE = 0.5f;
    public final static float PLATE_THICKNESS = 20f;
    public final static float PLATE_WIDTH = 500f;

    private final int x;
    private final int y;
    private final int z;
    private float angleX, angleY, angleZ, sensitivity;
    private float savedAngleX, savedAngleY, savedAngleZ;
    private final int color;

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
        TangibleGame.GAME.stroke(Color.STROKE_COLOR);
        TangibleGame.GAME.strokeWeight(2f);
        TangibleGame.GAME.pushMatrix();
        TangibleGame.GAME.translate(x, y, z);
        TangibleGame.GAME.rotateX(angleX);
        TangibleGame.GAME.rotateY(angleY);
        TangibleGame.GAME.rotateZ(angleZ);

        TangibleGame.GAME.fill(color);
        TangibleGame.GAME.box(PLATE_WIDTH, PLATE_THICKNESS, PLATE_WIDTH);
        TangibleGame.GAME.popMatrix();
    }

    public void updateAngle(){

        int deltaX = TangibleGame.GAME.mouseX - TangibleGame.GAME.pmouseX;
        int deltaY = TangibleGame.GAME.mouseY - TangibleGame.GAME.pmouseY;

        float amountX = TangibleGame.GAME.abs(deltaX * sensitivity * ANGLE_VALUE);
        float amountY = TangibleGame.GAME.abs(deltaY * sensitivity * ANGLE_VALUE);

        if(deltaX > 0){
            angleZ = ValueUtils.maxClamp(angleZ + amountX, MAX_ANGLE);
        }else if(deltaX < 0){
            angleZ = ValueUtils.minClamp(angleZ - amountX, -MAX_ANGLE);
        }
        if(deltaY < 0){
            angleX = ValueUtils.maxClamp(angleX + amountY, MAX_ANGLE);
        }else if(deltaY > 0){
            angleX = ValueUtils.minClamp(angleX - amountY, -MAX_ANGLE);
        }

    }

    public void updateSensitivity(int count){
        sensitivity = ValueUtils.roundThreeDecimals(ValueUtils.clamp(sensitivity - (count * STEP_VALUE), MIN_STEP_VALUE, MAX_STEP_VALUE));
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
        angleX = -TangibleGame.GAME.PI/2f;
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