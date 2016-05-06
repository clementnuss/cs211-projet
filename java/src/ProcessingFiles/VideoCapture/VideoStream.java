package ProcessingFiles.VideoCapture;


import processing.core.PApplet;
import processing.core.PImage;

public class VideoStream extends PApplet{

    static final VideoStream INST = new VideoStream();
    Capture cam;
    PImage img;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        String[] cameras = Capture.list();
        if (cameras.length == 0) {
            System.err.println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(cameras[i]);
            }
            cam = new Capture(this, cameras[0]);
            cam.start();
        }
    }

    public void draw() {
        if (cam.available() == true) {
            cam.read();
        }
        img = cam.get();
        image(img, 0, 0);
    }

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ProcessingFiles.VideoStream.VideoStream"}, INST);

    }
}
