package ch.epfl.cs211;

import ch.epfl.cs211.objects.ClosedCylinder;
import ch.epfl.cs211.objects.OpenCylinder;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.physicsEngine.Mover;
import ch.epfl.cs211.tools.Color;
import ch.epfl.cs211.tools.HUD;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import ch.epfl.cs211.objects.GameModes;

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
    private GameModes mode;
    private List<PVector> obstacleList;
    private final static float RECT_OFFSET = 150f;
    private final static float SHIFT_TO_REG_RATIO = Plate.PLATE_WIDTH / RECT_OFFSET;
    private final static float OBSTACLE_SIZE = 25f;

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
        closedCylinder = new ClosedCylinder(Mover.CYLINDER_RADIUS, 10, 30, new Color(150, 0, 0), new PVector(-30,0,-30));
        mode = GameModes.REGULAR;
        obstacleList = new ArrayList<>();
    }

    public void draw() {

        switch(mode) {
            case REGULAR:
                    drawRegularMode();
                break;

            case SHIFTED:
                    drawShiftedMode();
                break;
        }
    }

    private void drawRegularMode(){
        camera(plate.getX(), plate.getY(), plate.getZ() - 200,
                plate.getX(), plate.getY(), plate.getZ(),
                0, 1.0f, 0);
        directionalLight(50, 100, 125, 0, 1, 0);
        ambientLight(102, 102, 102);
        background(200);
        plate.display();
        mover.update();
        mover.checkCollisions(obstacleList);
        mover.display();

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

    private void drawShiftedMode(){
        camera();   //Resets the camera in order to display 2d text
        background(200);
        fill(100,100,100);
        stroke(50,50,50);

        rect(width/2 - RECT_OFFSET, height/2 - RECT_OFFSET, 2 * RECT_OFFSET, 2 * RECT_OFFSET);

        for(PVector p : obstacleList){
            ellipse(p.x / SHIFT_TO_REG_RATIO, p.y / SHIFT_TO_REG_RATIO, OBSTACLE_SIZE,OBSTACLE_SIZE);
        }

    }

    public void mouseDragged() {
        plate.updateAngle();
    }

    public void mouseWheel(MouseEvent event) {
        plate.updateSensitivity(event.getCount());
    }

    public void mouseClicked(MouseEvent event){
        switch(event.getButton()){
            case LEFT:
                if(mode == GameModes.SHIFTED){

                    obstacleList.add(
                            new PVector(mouseX * SHIFT_TO_REG_RATIO, mouseY * SHIFT_TO_REG_RATIO, 0)
                    );
                }
                break;
        }
    }

    public void keyPressed(KeyEvent event){
        switch(event.getKey()){
            case SHIFT:
                mode = GameModes.SHIFTED;
                System.out.println("Game mode was changed!");
                break;
        }
    }

    public void keyReleased(KeyEvent event){
        switch(event.getKey()){
            case SHIFT:
                break;
        }
    }

}
