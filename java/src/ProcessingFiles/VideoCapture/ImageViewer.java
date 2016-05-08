/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
package ProcessingFiles.VideoCapture;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Class that prints an image on a new window.
 */
public class ImageViewer extends PApplet {

    private int width = 640, height = 480;
    private PImage img;

    public ImageViewer() {
        img = new PImage(width, height);
        String[] args = {"dummyArgs"};
        runSketch(args, this);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        size(width, height);
    }

    public void drawImage(PImage img) {
        if (img == null)
            return;

        resize(img.width, img.height);
        this.img = img.copy();
        draw();
    }

    public void settings() {
        size(width, height);
    }


    public void draw() {
        image(img, 0f, 0f);

        //noLoop();
    }
}