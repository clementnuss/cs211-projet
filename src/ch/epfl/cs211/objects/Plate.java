package ch.epfl.cs211.objects;

import processing.core.PApplet;

/**
 * Created by Leandro on 08.03.2016.
 */
public class Plate {

    private int x, y, z;
    private final PApplet parent;

    public Plate(int x, int y, int z, PApplet p){
        this.x = x;
        this.y = y;
        this.z = z;
        this.parent = p;
    }

    public void display(){
        parent.pushMatrix();
        parent.translate(x, y, z);
        parent.box(100, 5, 100);
        parent.popMatrix();
    }

}
