/**
 * Created by clement on 01.03.2016.
 */
package Assignments;

import processing.core.*;

public class Assignment2 extends PApplet {

    public static void main(String args[]) {
        PApplet.main(new String[]{"Assignments.Assignment2"});
    }

    public void settings() {
        size(1000, 1000);
    }

    public void setup() {
    }

    public void draw() {

        background(255, 255, 255);
        My3DPoint eye = new My3DPoint(0, 0, -5000);
        My3DPoint origin = new My3DPoint(0, 0, 0);

        //The first vertex of your cuboid
        My3DBox input3DBox = new My3DBox(origin, 100, 150, 300);

        //rotated around x
        float[][] transform1 = rotateXMatrix(PI/8);
        input3DBox = transformBox(input3DBox, transform1);
        projectBox(eye, input3DBox).render();

        //rotated and translated
        float[][] transform2 = translationMatrix(200, 200, 0);
        input3DBox = transformBox(input3DBox, transform2);
        projectBox(eye, input3DBox).render();
        //rotated, translated, and scaled
        float[][] transform3 = scaleMatrix(2, 2, 2);
        input3DBox = transformBox(input3DBox, transform3);
        projectBox(eye, input3DBox).render();

    }

    class My2DPoint {
        float x;
        float y;
        My2DPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    class My3DPoint {
        float x;
        float y;
        float z;
        My3DPoint(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    My2DPoint projectPoint(My3DPoint eye, My3DPoint p) {

        float z_Minus_ez = p.z - eye.z;
        float ez = eye.z;

        return new My2DPoint((-ez *(p.x - eye.x))/z_Minus_ez, (-ez *(p.y - eye.y))/z_Minus_ez);
    }


    class My2DBox {
        My2DPoint[] s;
        My2DBox(My2DPoint[] s) {
            this.s = s;
        }

        void render(){
            strokeWeight(2.14f);

            stroke(0xff00ff00);
            line(s[7].x, s[7].y, s[4].x, s[4].y);
            line(s[4].x, s[4].y, s[5].x, s[5].y);
            line(s[7].x, s[7].y, s[6].x, s[6].y);
            line(s[5].x, s[5].y, s[6].x, s[6].y);

            stroke(0xff0000ff);
            line(s[7].x, s[7].y, s[3].x, s[3].y);
            line(s[1].x, s[1].y, s[5].x, s[5].y);
            line(s[6].x, s[6].y, s[2].x, s[2].y);
            line(s[0].x, s[0].y, s[4].x, s[4].y);

            stroke(0xffff0000);
            line(s[0].x, s[0].y, s[3].x, s[3].y);
            line(s[0].x, s[0].y, s[1].x, s[1].y);
            line(s[2].x, s[2].y, s[3].x, s[3].y);
            line(s[2].x, s[2].y, s[1].x, s[1].y);

        }
    }

    class My3DBox {
        My3DPoint[] p;
        My3DBox(My3DPoint origin, float dimX, float dimY, float dimZ){
            float x = origin.x;
            float y = origin.y;
            float z = origin.z;
            this.p = new My3DPoint[] {new My3DPoint(x,y+dimY,z+dimZ),
                    new My3DPoint(x,y,z+dimZ),
                    new My3DPoint(x+dimX,y,z+dimZ),
                    new My3DPoint(x+dimX,y+dimY,z+dimZ),
                    new My3DPoint(x,y+dimY,z),
                    origin,
                    new My3DPoint(x+dimX,y,z),
                    new My3DPoint(x+dimX,y+dimY,z)};
        }
        My3DBox(My3DPoint[] p) {
            this.p = p;
        }
    }

    My2DBox projectBox (My3DPoint eye, My3DBox box) {
        My2DPoint[] projectedBox = new My2DPoint[8];

        for (int i = 0; i < 8; i++) {
            projectedBox[i] = projectPoint(eye, box.p[i]);
        }

        return new My2DBox(projectedBox);
    }

    float[] homogeneous3DPoint (My3DPoint p) {
        float[] result = {p.x, p.y, p.z, 1};
        return result;
    }

    float[][]  rotateXMatrix(float angle) {
        return(new float[][] {{1, 0, 0, 0},
                {0, cos(angle), sin(angle), 0},
                {0, -sin(angle), cos(angle), 0},
                {0, 0, 0, 1}});
    }
    float[][]  rotateYMatrix(float angle) {
        return(new float[][] {{cos(angle), 0, sin(angle), 0},
                {0, 1, 0, 0},
                {-sin(angle), 0, cos(angle), 0},
                {0, 0, 0, 1}});
    }

    float[][]  rotateZMatrix(float angle) {
        return(new float[][] {{cos(angle), -sin(angle), 0, 0},
                {sin(angle), cos(angle), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}});
    }

    float[][]  scaleMatrix(float x, float y, float z) {
        return(new float[][] {{x, 0, 0, x},
                {0, y, 0, y},
                {0, 0, z, z},
                {0, 0, 0, 1}});
    }

    float[][]  translationMatrix(float x, float y, float z) {
        return(new float[][] {{1, 0, 0, x},
                {0, 1, 0, y},
                {0, 0, 1, z},
                {0, 0, 0, 1}});
    }

    float[] matrixProduct(float[][] a, float[] b) {
        float[] back = new float[4];

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                back[i] += a[i][j] * b[j];
            }
        }

        return back;
    }

    My3DBox transformBox(My3DBox box, float[][] transformMatrix) {
        My3DPoint[] back = new My3DPoint[8];

        for(int k = 0; k < 8; k++) {
            float[] homogeneousVertex = homogeneous3DPoint(box.p[k]);
            homogeneousVertex = matrixProduct(transformMatrix, homogeneousVertex);
            back[k] = euclidian3DPoint(homogeneousVertex);
        }
        return new My3DBox(back);
    }


    My3DPoint euclidian3DPoint (float[] a) {
        My3DPoint result = new My3DPoint(a[0]/a[3], a[1]/a[3], a[2]/a[3]);
        return result;
    }

}
