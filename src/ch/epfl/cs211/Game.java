package ch.epfl.cs211;

import ch.epfl.cs211.objects.ClosedCylinder;
import ch.epfl.cs211.objects.GameModes;
import ch.epfl.cs211.objects.OpenCylinder;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.physicsEngine.Mover;
import ch.epfl.cs211.tools.Color;
import ch.epfl.cs211.tools.HUD;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

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
 *       ¬ Z
 *     /
 *   /
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
    private HUD hudPlate, hudBall, hudMouse;
    private Mover mover;
    private OpenCylinder openCylinder;
    private ClosedCylinder closedCylinder;
    private GameModes mode;
    private List<PVector> obstacleList;
    private final static float PLATE_OFFSET = Plate.PLATE_WIDTH/2;
    private final static float OBSTACLE_SIZE = 25f;

    public static final Game INSTANCE = new Game();

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ch.epfl.cs211.Game"}, Game.INSTANCE);

    }

    private Game() {
    }

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        stroke(Color.STROKE_COLOR);
        plate = new Plate(0, 0, 0, Color.PLATE_COLOR);
        hudPlate = new HUD(25,25,150,300, Color.HUD_COLOR);
        hudBall = new HUD(200, 25, 200, 300, Color.HUD_COLOR);
        hudMouse = new HUD(25, 25, 250, 300, Color.HUD_COLOR);
        mover = new Mover(plate);
        openCylinder = new OpenCylinder(50, 40, 40, Color.CYLINDER_COLOR);
        closedCylinder = new ClosedCylinder(Mover.CYLINDER_RADIUS, 75, 30, Color.CYLINDER_COLOR);
        mode = GameModes.REGULAR;
        obstacleList = new ArrayList<>();
    }

    public void draw() {

        background(210);
        ambientLight(80,80,80);
        spotLight(255, 255, 255, 0,-500,0,0,1,0,PI/4f,2);

        switch (mode) {
            case REGULAR:
                drawRegularMode();
                break;

            case SHIFTED:
                drawShiftedMode();
                break;
        }

    }

    private void drawObstacles(){
        pushMatrix();
        rotateX(plate.getAngleX());
        rotateY(plate.getAngleY());
        rotateZ(plate.getAngleZ());
        translate(plate.getX(), plate.getY()-Plate.PLATE_THICKNESS/2, plate.getZ());

        for (PVector obst : obstacleList)
            closedCylinder.display(obst);

        popMatrix();
    }

    private void drawRegularMode() {
        camera(plate.getX(), plate.getY()-500, plate.getZ() + 700,
                plate.getX(), plate.getY(), plate.getZ(),
                0, 1.0f, 0);

        plate.display();
        mover.update();
        mover.checkCollisions(obstacleList);
        mover.display();

        drawObstacles();

        translate(mouseX, mouseY, 0);

        camera();   //Resets the camera in order to display 2d text
        hudPlate.display("X: " + plate.getAngleX() +
                "\nY: " + plate.getAngleY() +
                "\nZ: " + plate.getAngleZ() +
                "\nSensitivity: " + plate.getAngleStep());

        hudBall.display("Ball x= " + roundThreeDecimals(mover.getX()) +
                "\nBall y= " + roundThreeDecimals(mover.getY()) +
                "\nBall z= " + roundThreeDecimals(mover.getZ()) +
                "\nVel x= " + roundThreeDecimals(mover.getVelocity().x) +
                        "\nVel y= " + roundThreeDecimals(mover.getVelocity().y) +
                        "\nVel z= " + roundThreeDecimals(mover.getVelocity().z));
    }

    private void drawShiftedMode() {

        camera(plate.getX(), plate.getY(), plate.getZ() + 100,
                plate.getX(), plate.getY(), plate.getZ(),
                0, 1.0f, 0);

        directionalLight(210, 210, 210, 0,0.2f,-1);

        ortho();
        plate.saveState();
        plate.setAngleX(-PI/2 -0.001f);
        plate.setAngleY(0);
        plate.setAngleZ(0);
        plate.display();
        strokeWeight(10f);
        stroke(0);

        drawObstacles();
        plate.setAngleX(plate.getSavedAngleX());
        plate.setAngleY(plate.getSavedAngleY());
        plate.setAngleZ(plate.getSavedAngleZ());

        perspective();
        camera();
        hudMouse.display("You are currently in shift mode !\n" +
                "Click on the plate to add obstacles." +
                "\nMouse X: " + mouseX+
                "\nMouse Y: " + mouseY );

    }

    public void mouseDragged() {
        if(mode == GameModes.REGULAR) plate.updateAngle();
    }

    public void mouseWheel(MouseEvent event) {
        plate.updateSensitivity(event.getCount());
    }

    public void mouseClicked(MouseEvent event) {
        switch (event.getButton()) {
            case LEFT:
                if (mode == GameModes.SHIFTED) {
                    //check if click occured above the plate and not outside boundaries
                    if ((width / 2 - PLATE_OFFSET + OBSTACLE_SIZE / 2)    < mouseX
                                                                        && mouseX < (width / 2 + PLATE_OFFSET - OBSTACLE_SIZE / 2)
                       && (height / 2 - PLATE_OFFSET + OBSTACLE_SIZE / 2) < mouseY
                                                                        && mouseY < (height / 2 + PLATE_OFFSET - OBSTACLE_SIZE / 2)) {
                        obstacleList.add(
                                new PVector((mouseX - width/2), 0, (mouseY - height/2))
                        );
                    }
                }
                break;
        }
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case SHIFT:
                mode = GameModes.SHIFTED;
                break;
        }
    }

    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case SHIFT:
                mode = GameModes.REGULAR;
                break;
        }
    }

}
