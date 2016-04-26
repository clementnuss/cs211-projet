package ProcessingFiles.imageFilter;

import ch.epfl.cs211.display2D.HScrollbar;
import processing.core.PApplet;
import processing.core.PImage;

public class imageFilter extends PApplet {

    static final imageFilter INST = new imageFilter();
    private PImage img;
    int max = 0xFFFFFF;
    private HScrollbar thresholdBar1;
    private HScrollbar thresholdBar2;
    private PImage toDisplay, sobel;

    private float oldBarValue1;
    private float oldBarValue2;

    private final float[] sobelKernel = {1f, 0f, -1f};
    final int SOBEL_LENGTH = sobelKernel.length;
    final float SOBEL_WEIGHT = 1f;
    private final float SOBEL_PERCENTAGE = 0.3f;

    private int[][] gaussianKernel =
            {
                    {9, 12, 9},
                    {12, 15, 12},
                    {9, 12, 9},};

    private int[][] kernel1 =
            {
                    {0, 0, 0},
                    {0, 2, 0},
                    {0, 0, 0},
            };

    private int[][] kernel2 = //<>//
            {
                    {0, 1, 0},
                    {1, 0, 1},
                    {0, 1, 0},
            };

    public void settings() {
        size(800, 600, P2D);
    }

    public void setup() {
        img = loadImage("images/board1.jpg");
        thresholdBar1 = new HScrollbar(0, 0, width, 20);
        thresholdBar2 = new HScrollbar(0, 25, width, 20);
        oldBarValue1 = 0;
        oldBarValue2 = 0;
    }

    public void draw() {
      
      /*
        if (oldBarValue1 != thresholdBar1.getPos() || oldBarValue2 != thresholdBar2.getPos()) {
            background(0);
            oldBarValue1 = thresholdBar1.getPos();
            oldBarValue2 = thresholdBar2.getPos();

            
        }

        thresholdBar1.display();
        thresholdBar1.update();
        thresholdBar2.display();
        thresholdBar2.update();
      */

        background(0);
        //IMAGE TREATMENT PIPELINE
        // 1. saturation threshold
        // 2. hue threshold
        // 3. gaussian blur
        // 4. brightness threshold
        toDisplay = brightnessThreshold(
                gaussianBlur(
                        hueThreshold(
                                saturationThreshold(img.copy(), 80, 255)
                                , 100, 140)
                )
                , 1, false);

        //then sobel
        sobel = sobel(toDisplay);

        image(hough(sobel), 0, 0);


        noLoop();
    }

    //A good value to extract the board would be 87 to 255
    private PImage saturationThreshold(PImage img, float t1, float t2) {
        img.loadPixels();
        System.out.println("[INFO:] SATURATION threshold selection is " + t1 + "-" + t2 + " MIN-MAX");

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

    PImage hueAsGrayLevel(PImage img) {
        for (int i = 0; i < img.width * img.height; i++) {
            img.pixels[i] = color(hue(img.pixels[i]));
        }
        return img;
    }

    //A good value to extract the board would be 115 to 132
    private PImage hueThreshold(PImage img, float t1, float t2) {
        img.loadPixels();
        int originalColor;
        float originalColorHue;

        System.out.println("[INFO:] HUE threshold selection is " + t1 + "-" + t2 + " MIN-MAX");

        for (int i = 0; i < img.width * img.height; i++) {
            originalColor = img.pixels[i];
            originalColorHue = hue(originalColor);
            img.pixels[i] = (t1 <= originalColorHue && originalColorHue <= t2) ? originalColor : 0x0;
        }
        img.updatePixels();
        return img;
    }

    private float computeWeight(int[][] m) {
        int s = 0;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                s += m[i][j];
            }
        }
        return s;
    }

    private PImage convolve(PImage img, int[][] matrix) {
        PImage result = createImage(width, height, ALPHA);
        float sum;
        float weight = computeWeight(matrix) * 2;
        int N = matrix.length;
        int halfN = N / 2;

        for (int y = halfN; y < img.height - halfN; y++) {
            for (int x = halfN; x < img.width - halfN; x++) {
                sum = 0;
                for (int j = 0; j < N; j++) {
                    for (int i = 0; i < N; i++) {
                        int xp = x - halfN + i;
                        int yp = y - halfN + j;
                        sum += brightness(img.pixels[(yp * img.width) + xp]) * matrix[j][i];
                    }
                }
                sum /= weight;
                result.pixels[(y * result.width) + x] = color(sum);
            }
        }
        return result;
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

                //Horiontal convolution
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

    private PImage gaussianBlur(PImage img) {
        return convolve(img, gaussianKernel);
    }

    private PImage hough(PImage edgeImg) {
        float discretizationStepsPhi = 0.06f;
        float discretizationStepsR = 2.5f;

        // dimensions of the accumulator
        int phiDim = (int) (Math.PI / discretizationStepsPhi);
        int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);
        System.out.println("phi dimension : " + phiDim + " r dim " + rDim);


        // our accumulator (with a 1 pix margin around)
        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
        System.out.println("accumulator size:" + accumulator.length + " image size : h=" + edgeImg.height + " w=" + edgeImg.width);

        // Fill the accumulator: on edge points (ie, white pixels of the edge
        // image), store all possible (r, phi) pairs describing lines going
        // through the point.
        for (int y = 0; y < edgeImg.height; y++) {

            for (int x = 0; x < edgeImg.width; x++) {
                // Are we on an edge?
                if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {


                    for (float phi = 0; phi < Math.PI; phi += discretizationStepsPhi) {

                        float r = x * cos(phi) + y * sin(phi);
                        r += (rDim - 1) / 2;
                        int rAcc = Math.round(r);
                        int phiAcc = Math.round(phi);

                        //int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
                        int index = Math.round(r + (phi + 1) * (rDim + 2) + 1);

                        accumulator[index] += 1;

                    }

                    // ...determine here all the lines (r, phi) passing through
                    // pixel (x,y), convert (r,phi) to coordinates in the
                    // accumulator, and increment accordingly the accumulator.
                    // Be careful: r may be negative, so you may want to center onto
                    // the accumulator with something like: r += (rDim - 1) / 2
                }
            }
        }

        System.out.println("Accumulator computation ended");

        PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);

        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        // You may want to resize the accumulator to make it easier to see:
        houghImg.resize(400, 400);
        houghImg.updatePixels();
        System.out.println("hough image computed");

        return houghImg;
    }

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ProcessingFiles.imageFilter.imageFilter"}, INST);

    }

}