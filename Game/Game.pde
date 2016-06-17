import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


// Imports for the java files
import papaya.*;
import processing.video.*;


/**
 * <p>
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 * <p>
 * <p>
 * PROCESSING 3D AXIS
 * <p>
 * <p>
 * ¬ -Z
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

    //Game instance
    public static final Game GAME = new Game();

    //Game constants
    public final static int WINDOW_WIDTH = 1024;
    public final static int WINDOW_HEIGHT = 576;
    private final static float SCORE_LIMIT = 1000f;
    private final static float MIN_SCORE = 0;
    private final static float PLATE_OFFSET = Plate.PLATE_WIDTH / 2;
    private final static float OBSTACLE_SIZE = 25f;
    private final static int SCORE_UPDATE_INTERVAL = 30;
    private final static float SCORE_COEFFICIENT = 3f;

    public static float maxScore = 0f;

    //Video capture
    private VideoStream videoCaptureManager;

    //2D
    private SubScreen subView;
    private HUD hudPlate, hudBall, hudMouse;

    //Physics
    private Plate plate;
    private Mover mover;
    private ClosedCylinder closedCylinder;
    private boolean modeHasChanged;

    SynchronizedRotationValue syncRot;
    PVector absoluteRot;
    PVector progressiveRot;

    private List<PVector> obstacleList;

    //Game features
    private int oldWidth;
    private int oldHeight;
    private GameModes mode;
    private float score = 0f, prevScore = 0f, lastChange = 0f;
    public Deque<Float> scoresList;
    private int scoreInterval = 0;

    private Game(){
      scoresList = new ArrayDeque();
      oldWidth = width;
      oldHeight = height;
    }
    
   public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ch.epfl.cs211.Game"}, GAME);

    }

    public void settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
    }

    public void setup() {
        //mov = new Movie(this, "testvideo.mp4");
        
        absoluteRot = new PVector(0,0,0);
        progressiveRot = new PVector(0,0,0);
        stroke(Color.STROKE_COLOR);
        syncRot = new SynchronizedRotationValue();
        println(this.dataPath());
        videoCaptureManager = new VideoStream(syncRot, this.dataPath(""));
               

        String[] args = {"Image processing window"};
        PApplet.runSketch(args, videoCaptureManager);

        plate = new Plate(0, 0, 0, Color.PLATE_COLOR);
        hudPlate = new HUD(25, 25, 150, 300, Color.HUD_COLOR);
        hudBall = new HUD(200, 25, 200, 300, Color.HUD_COLOR);
        hudMouse = new HUD(25, 25, 250, 300, Color.HUD_COLOR);
        subView = new SubScreen();
        mover = new Mover(plate);
        closedCylinder = new ClosedCylinder(Mover.CYLINDER_RADIUS, 75, 30, Color.CYLINDER_COLOR);
        mode = GameModes.REGULAR;
        obstacleList = new ArrayList();
    }

    public void draw() {

        if (checkIfResized())
            subView.updateDimensions();

        background(210);
        ambientLight(80, 80, 80);
        spotLight(255, 255, 255, 200, -500, -250, 0, 1, 0, PI / 4f, 2);

        switch (mode) {
            case REGULAR:
                drawRegularMode();
                break;

            case SHIFTED:
                drawShiftedMode();
                break;
        }

    }

    private void drawRegularMode() {
        camera(plate.getX(), plate.getY() - 800, plate.getZ() +50,
                plate.getX(), plate.getY(), plate.getZ()+49,
                0, 1.0f, 0);

        //If the orientation of the plate was saved and modified we restore it
        if (modeHasChanged) {
            plate.restoreState();
            perspective();
            modeHasChanged = false;
        }

        plate.setRotation(videoCaptureManager.getRotation());
        PVector rotFromVideoProcessing = syncRot.getRot();

        if(!absoluteRot.equals(rotFromVideoProcessing)){
            absoluteRot = rotFromVideoProcessing;
        }

        PVector rotDiff = PVector.sub(absoluteRot,progressiveRot);
        progressiveRot.add(rotDiff.mult(0.15f));

        plate.setRotation(progressiveRot);
        plate.display();
        mover.update();
        obstacleList = mover.checkCollisions(obstacleList);
        if (scoreInterval < SCORE_UPDATE_INTERVAL)
            scoreInterval++;
        else {
            scoreInterval = 0;
            scoresList.add(score);
            while (scoresList.size() > subView.getMaxPlottableElements())
                scoresList.remove();
        }

        lastChange = score - prevScore;
        prevScore = score;

        mover.display();

        drawObstacles();

        camera();   //Resets the camera in order to display 2d text
        subView.draw();
        hudPlate.display("X: " + plate.getAngleX() +
                "\nY: " + plate.getAngleY() +
                "\nZ: " + plate.getAngleZ() +
                "\nSensitivity: " + plate.getSensitivity() +
                "\nScore: " + score);

        hudBall.display("Ball x= " + ValueUtils.roundThreeDecimals(mover.getX()) +
                "\nBall y= " + ValueUtils.roundThreeDecimals(mover.getY()) +
                "\nBall z= " + ValueUtils.roundThreeDecimals(mover.getZ()) +
                "\nVel x= " + ValueUtils.roundThreeDecimals(mover.getVelocity().x) +
                "\nVel y= " + ValueUtils.roundThreeDecimals(mover.getVelocity().y) +
                "\nVel z= " + ValueUtils.roundThreeDecimals(mover.getVelocity().z));
    }

    private void drawShiftedMode() {

        camera(plate.getX(), plate.getY(), plate.getZ() + 100,
                plate.getX(), plate.getY(), plate.getZ(),
                0, 1.0f, 0);

        directionalLight(210, 210, 210, 0, 0.2f, -1);

        if (modeHasChanged) {
            ortho();
            plate.saveState();
            modeHasChanged = false;
        }

        plate.setVertical();
        plate.display();
        mover.display();
        drawObstacles();
        camera();
        hudMouse.display("You are currently in shift mode !\n" +
                "Click on the plate to add obstacles." +
                "\nMouse X: " + mouseX +
                "\nMouse Y: " + mouseY);

    }

    private void drawObstacles() {
        pushMatrix();
        rotateX(plate.getAngleX());
        rotateY(plate.getAngleY());
        rotateZ(plate.getAngleZ());
        translate(plate.getX(), plate.getY() - Plate.PLATE_THICKNESS / 2, plate.getZ());

        for (PVector obst : obstacleList)
            closedCylinder.display(obst, mode == GameModes.SHIFTED);

        popMatrix();
    }

    public void mouseDragged() {
        if (mode == GameModes.REGULAR && mouseY < height - SubScreen.VISUALISATION_HEIGHT)
            plate.updateAngle();
    }

    public void mouseWheel(MouseEvent event) {
        plate.updateSensitivity(event.getCount());
    }

    public void mouseClicked(MouseEvent event) {
        switch (event.getButton()) {
            case LEFT:
                if (mode == GameModes.SHIFTED) {
                    float xPos = mouseX - width / 2;
                    float yPos = mouseY - height / 2;
                    //check if click occurred above a legal position
                    if ((width / 2 - PLATE_OFFSET + OBSTACLE_SIZE) < mouseX
                            && mouseX < (width / 2 + PLATE_OFFSET - OBSTACLE_SIZE)
                            && (height / 2 - PLATE_OFFSET + OBSTACLE_SIZE) < mouseY
                            && mouseY < (height / 2 + PLATE_OFFSET - OBSTACLE_SIZE)
                            && !(PVector.dist(mover.getPosition(), new PVector(xPos, Mover.GROUND_OFFSET, yPos)) < Mover.SPHERE_TO_CYLINDER_DISTANCE)) {
                        obstacleList.add(new PVector(xPos, 0, yPos));
                    }
                }
                break;
        }
    }

    public boolean checkIfResized() {
        if (oldHeight != height || oldWidth != width) {
            oldHeight = height;
            oldWidth = width;
            return true;
        }
        return false;
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case SHIFT:
                mode = GameModes.SHIFTED;
                modeHasChanged = true;
                break;
        }
    }

    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case SHIFT:
                mode = GameModes.REGULAR;
                modeHasChanged = true;
                break;
        }
    }

    public void incScore(float velocity) {
        float chg = abs(velocity) * SCORE_COEFFICIENT;

        if (score + chg > SCORE_LIMIT)
            score = SCORE_LIMIT;
        else
            score += chg;

        if (score > maxScore)
            maxScore = score;
    }

    public void decScore(float velocity) {
        float chg = abs(velocity) * SCORE_COEFFICIENT;
        if (score - chg < MIN_SCORE)
            score = MIN_SCORE;
        else
            score -= chg;
    }

    public Deque<Float> getScoresList() {
        return scoresList;
    }

    public Mover getMover() {
        return mover;
    }

    public float getScore() {
        return score;
    }

    public float getLastChange() {
        return lastChange;
    }

    public List<PVector> getObstacleList() {
        return obstacleList;
    }

}