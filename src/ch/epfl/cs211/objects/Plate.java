package ch.epfl.cs211.objects;

import ch.epfl.cs211.tools.AngleUtils;
import processing.core.PApplet;
import static java.lang.Math.*;
import static ch.epfl.cs211.tools.AngleUtils.*;

/**
 * Created by Leandro on 08.03.2016.
 */
public class Plate {

    private int x, y, z;
    private float angleX, angleY, angleZ;
    private final PApplet parent;

    private final static float MAX_ANGLE = (float)(PI/6.0);

    public Plate(int x, int y, int z, PApplet p){
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = p;
        this.angleX = 0;
        this.angleY = 0;
        this.angleZ = 0;
    }

    public void display(){
        parent.pushMatrix();
        parent.translate(x, y, z);
        parent.rotateX(angleX);
        parent.rotateZ(angleZ);
        parent.rotateY(angleY);
        parent.box(100, 5, 100);
        parent.popMatrix();
    }

    public void update(){

        int deltaX = parent.mouseX - parent.pmouseX;
        int deltaY = parent.mouseY - parent.pmouseY;

        System.out.println("dX: "+deltaX+"dY: "+deltaY);

        if(deltaX < 0){
            angleZ = minClampAngle((float)(angleZ - PI/100.), -MAX_ANGLE);
        }else if(deltaX > 0){
            angleZ = maxClampAngle((float)(angleZ + PI/100.), MAX_ANGLE);
        }
        if( deltaY < 0){
            angleX = maxClampAngle((float)(angleX + PI/100.), MAX_ANGLE);
        } else if(deltaY > 0){
            angleX = minClampAngle((float)(angleX - PI/100.), -MAX_ANGLE);
        }

    }
}
