package ch.epfl.cs211;

import ch.epfl.cs211.PhysicsEngine.Mover;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.tools.Color;
import ch.epfl.cs211.tools.HUD;
import processing.core.PApplet;
import processing.event.MouseEvent;


/**
 * Created by Clément Nussbaumer on 08.03.2016.
 * <p>
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class Game extends PApplet {

    private Plate plate;
    private HUD hud;
    private Mover mover;

    public static void main(String[] args) {
        PApplet.main(new String[]{"ch.epfl.cs211.Game"});
    }

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
        noStroke();
        plate = new Plate(0, 0, 0, new Color(152, 202, 227),this);
        hud = new HUD(25, 25, 200, 100, new Color(255, 166, 0), this);
        mover = new Mover(this);
    }

    public void draw() {
        camera(plate.getX(), plate.getY(), plate.getZ()-200,
                plate.getX(), plate.getY(), plate.getZ(),
                0, -1.0f, 0);
        directionalLight(50, 100, 125, 0, 1, 0);
        ambientLight(102, 102, 102);
        background(200);
        plate.display();

        camera();   //Resets the camera in order to display 2d text
        hud.display( "X: " +plate.getAngleX()+
                "\nY: " + plate.getAngleY() +
                "\nZ: " + plate.getAngleZ() +
                "\nSensitivity: " + plate.getAngleStep());

        mover.update();
        mover.checkEdges();
        mover.display();
    }

    public void mouseDragged() {
        plate.updateAngle();
    }

    public void mouseWheel(MouseEvent event) {
        plate.updateSensitivity(event.getCount());
    }

}
