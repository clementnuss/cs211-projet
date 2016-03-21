package ch.epfl.cs211;

import ch.epfl.cs211.objects.ClosedCylinder;
import ch.epfl.cs211.objects.OpenCylinder;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.physicsEngine.Mover;
import ch.epfl.cs211.tools.Color;
import ch.epfl.cs211.tools.HUD;
import processing.core.PApplet;
import processing.event.MouseEvent;

import static ch.epfl.cs211.tools.ValueUtils.roundThreeDecimals;


/**
 * Created by Clément Nussbaumer on 08.03.2016.
 * <p>
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 * <p>
 * <p>
 * PROCESSING 3D AXIS
 * <p>
 * <p>
 * ¬ Z
 * /
 * /
 * /
 * -------------> X
 * |
 * |
 * |
 * |
 * V  Y
 */


/**
 * Classe singleton représentant l'application principale du jeu
 */
public class Game extends PApplet {


    private Plate plate;
    private HUD hud, hudBall;
    private Mover mover;
    private OpenCylinder openCylinder;
    private ClosedCylinder closedCylinder;

    public static final Game INSTANCE = new Game();

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ch.epfl.cs211.Game"}, Game.INSTANCE);

    }

    private Game() {
    }

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
        stroke(0);
        strokeWeight(2);
        plate = new Plate(0, 0, 0, new Color(152, 202, 227));
        hud = new HUD(25, 25, 200, 100, new Color(255, 166, 0));
        hudBall = new HUD(275, 25, 200, 100, new Color(255, 166, 0));
        mover = new Mover(plate);
        openCylinder = new OpenCylinder(50, 40, 40, new Color(150, 0, 0));
        closedCylinder = new ClosedCylinder(20, 15, 30, new Color(150, 0, 0));
    }

    public void draw() {
        camera(plate.getX(), plate.getY(), plate.getZ() - 200,
                plate.getX(), plate.getY(), plate.getZ(),
                0, 1.0f, 0);
        directionalLight(50, 100, 125, 0, 1, 0);
        ambientLight(102, 102, 102);
        background(200);
        plate.display();
        mover.update();
        mover.checkEdges();
        mover.display();

        pushMatrix();
        rotateX(plate.getAngleX());
        rotateY(plate.getAngleY());
        rotateZ(plate.getAngleZ());
        translate(0,-14,0);

        closedCylinder.display();
        popMatrix();

        translate(mouseX, mouseY, 0);

        camera();   //Resets the camera in order to display 2d text
        hud.display("X: " + plate.getAngleX() +
                "\nY: " + plate.getAngleY() +
                "\nZ: " + plate.getAngleZ() +
                "\nSensitivity: " + plate.getAngleStep());

        float phi = plate.getAngleZ();
        float theta = -plate.getAngleX();


        hudBall.display("Phi (angle Z)= " + roundThreeDecimals(phi) +
                "\nTheta (angle X)= " + roundThreeDecimals(theta) +
                "\n x-contribution= " + roundThreeDecimals(-tan(phi) * mover.getX()) +
                "\n z-contribution= " + roundThreeDecimals(-tan(theta) * mover.getY()));

    }

    public void mouseDragged() {
        plate.updateAngle();
    }

    public void mouseWheel(MouseEvent event) {
        plate.updateSensitivity(event.getCount());
    }

}
