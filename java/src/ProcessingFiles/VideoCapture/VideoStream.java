package ProcessingFiles.VideoCapture;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.*;

public class VideoStream extends PApplet {

    private final static int WIDTH = 640;
    private final static int HEIGHT = 480;

    /*===============================================================
        Values for the Sobel operator
      ===============================================================*/

    private final float[] sobelKernel = {1f, 0f, -1f};
    final int SOBEL_LENGTH = sobelKernel.length;
    final float SOBEL_WEIGHT = 1f;
    private final float SOBEL_PERCENTAGE = 0.3f;

    /*===============================================================
        Values for the Hough transform
      ===============================================================*/
    private static float discretizationStepsPhi = 0.06f;
    private static float discretizationStepsR = 2.5f;
    private int phiDim;
    private int rDim;
    private int rOffset;
    private float[] sinTable;
    private float[] cosTable;

    static final VideoStream INST = new VideoStream();
    Capture cam;
    PImage img;

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public void setup() {

        // dimensions of the accumulator
        phiDim = (int) (Math.PI / discretizationStepsPhi);
        rDim = (int) ((Math.hypot(WIDTH, HEIGHT) * 2 + 1) / discretizationStepsR);
        rOffset = ((rDim - 1) / 2) + 2; //The +2 from the formula was added directly to the offset for performance purposes

        sinTable = new float[phiDim];
        cosTable = new float[phiDim];
        for (int theta = 0; theta < phiDim; theta++) {
            double thetaRadians = theta * Math.PI / phiDim;
            sinTable[theta] = (float) Math.sin(thetaRadians);
            cosTable[theta] = (float) Math.cos(thetaRadians);
        }


        String[] cameras = Capture.list();
        if (cameras.length == 0) {
            System.err.println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(cameras[i]);
            }
            cam = new Capture(this, cameras[1]);
            cam.start();
        }
    }

    public void draw() {
        if (cam.available() == true) {
            cam.read();

            //IMAGE TREATMENT PIPELINE
            // 1. saturation threshold
            // 2. hue threshold
            // 3. brightness threshold
            // 4. sobel operator
            PImage toDisplay = sobel(
                                brightnessThreshold(
                                    hueThreshold(
                                        saturationThreshold(cam.copy(), 80, 255)
                                    , 100, 140)
                                , 1, false)
                                );

            image(hough(toDisplay, cam), 0, 0);
        }
    }

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ProcessingFiles.VideoStream.VideoStream"}, INST);

    }


    //A good value to extract the board would be 87 to 255
    private PImage saturationThreshold(PImage img, float t1, float t2) {
        img.loadPixels();

        for (int i = 0; i < img.width * img.height; i++) {
            int originalColor = img.pixels[i];
            float sat = saturation(originalColor);
            img.pixels[i] = (t1 <= sat && sat <= t2) ? originalColor : 0x0;
        }
        img.updatePixels();
        return img;
    }


    //Takes an image, a threshold between 0 and 1 and it will set all pixels above the threshold to WHITE and the others to BLACK
    private PImage brightnessThreshold(PImage img, float t, boolean inverted) {
        img.loadPixels();
        for (int i = 0; i < img.width * img.height; i++) {

            if (inverted)
                img.pixels[i] = (brightness(img.pixels[i]) < t) ? 0xFFFFFFFF : 0x0;
            else
                img.pixels[i] = (brightness(img.pixels[i]) > t) ? 0xFFFFFFFF : 0x0;
        }
        img.updatePixels();
        return img;
    }

    //A good value to extract the board would be 115 to 132
    private PImage hueThreshold(PImage img, float t1, float t2) {
        img.loadPixels();
        int originalColor;
        float originalColorHue;

        for (int i = 0; i < img.width * img.height; i++) {
            originalColor = img.pixels[i];
            originalColorHue = hue(originalColor);
            img.pixels[i] = (t1 <= originalColorHue && originalColorHue <= t2) ? originalColor : 0x0;
        }
        img.updatePixels();
        return img;
    }

    private PImage sobel(PImage img) {
        float sum_h;
        float sum_v;
        float max = 0f;
        float[][] buffer = new float[img.height][img.width];
        PImage result = createImage(img.width, img.height, ALPHA);

/* Convolve operation is separeted in two rectangular matrices to save computations, namely 2*n instead of n^2 per image pixel  */
        //Vertical convolution
        for (int y = 1; y < img.height - 1; y++) {
            for (int x = 1; x < img.width - 1; x++) {
                sum_h = 0;
                sum_v = 0;

                //Horizontal convolution
                int xp = y * img.width + x;
                sum_h += sobelKernel[0] * brightness(img.pixels[xp - 1]);
                sum_h += sobelKernel[2] * brightness(img.pixels[xp + 1]);

                //Vertical convolution
                sum_v += sobelKernel[0] * brightness(img.pixels[(y - 1) * img.width + x]);
                sum_v += sobelKernel[2] * brightness(img.pixels[(y + 1) * img.width + x]);

                //Compute de gradient
                float sum = sqrt(pow(sum_h, 2) + pow(sum_v, 2));
                if (sum > max) {
                    max = sum;
                }
                buffer[y][x] = sum;
            }
        }

        for (int y = 2; y < img.height - 2; y++) {
            for (int x = 2; x < img.width - 2; x++) {
                if (buffer[y][x] > (max * SOBEL_PERCENTAGE))
                    result.pixels[y * img.width + x] = 0xFFFFFFFF;
                else
                    result.pixels[y * img.width + x] = 0xFF000000;
            }
        }
        return result;
    }

    private PImage hough(PImage edgeImg, PImage originalImage) {

        // our accumulator (with a 1 pix margin around)
        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
        // Fill the accumulator: on edge points (ie, white pixels of the edge
        // image), store all possible (r, phi) pairs describing lines going
        // through the point.
        for (int y = 0; y < edgeImg.height; y++) {
            for (int x = 0; x < edgeImg.width; x++) {

                // Are we on an edge? Since image is BLACK/WHITE we can just check the LSB
                if ((edgeImg.pixels[y * edgeImg.width + x] & 0b1) != 0) {

                    // ...determine here all the lines (r, phi) passing through
                    // pixel (x,y), convert (r,phi) to coordinates in the
                    // accumulator, and increment accordingly the accumulator.
                    for (int phi = 0; phi < phiDim; phi++) {
                        double r = (x * cosTable[phi] + y * sinTable[phi]) / discretizationStepsR;
                        r += rOffset;
                        int idx = ((int) r) + (phi + 1) * (rDim + 2);
                        accumulator[idx]++;
                    }
                }
            }
        }

        /*PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        // You may want to resize the accumulator to make it easier to see:
        houghImg.resize(400, 400);
        houghImg.updatePixels();
        System.out.println("hough image computed");*/


        PGraphics pg = createGraphics(originalImage.width, originalImage.height);

        pg.beginDraw();
        pg.image(originalImage, 0, 0);
        //pg.image(houghImg, 0,0);

        for (int idx = 0; idx < accumulator.length; idx++) {
            if (accumulator[idx] > 180) {
                // first, compute back the (r, phi) polar coordinates:
                int accPhi = (idx / (rDim + 2)) - 1;
                int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
                float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
                float phi = accPhi * discretizationStepsPhi;

                // Cartesian equation of a line: y = ax + b
                // in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
                // => y = 0 : x = r / cos(phi)
                // => x = 0 : y = r / sin(phi)
                // compute the intersection of this line with the 4 borders of
                // the image

                int x0 = 0;
                int y0 = (int) (r / sin(phi));
                int x1 = (int) (r / cos(phi));
                int y1 = 0;
                int x2 = edgeImg.width;
                int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
                int y3 = edgeImg.height;
                int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));

                // Finally, plot the lines
                pg.stroke(204, 102, 0);
                if (y0 > 0) {
                    if (x1 > 0)
                        pg.line(x0, y0, x1, y1);
                    else if (y2 > 0)
                        pg.line(x0, y0, x2, y2);
                    else
                        pg.line(x0, y0, x3, y3);
                } else {
                    if (x1 > 0) {
                        if (y2 > 0)
                            pg.line(x1, y1, x2, y2);
                        else
                            pg.line(x1, y1, x3, y3);
                    } else
                        pg.line(x2, y2, x3, y3);
                }
            }
        }

        pg.endDraw();

        return pg.get();
    }
}
