package ProcessingFiles.Test;

import processing.core.PApplet;
import processing.core.PImage;

public class BlurTest extends PApplet{

    public static final BlurTest INST = new BlurTest();

    private final float[] gaussKernel = {0.1f, 0.8f, 0.1f};

    private int[][] gaussianKernel =
            {
                    {9, 12, 9},
                    {12, 15, 12},
                    {9, 12, 9},};

    public void settings() {
        size(800, 600);
    }

    public void setup() {

        PImage img = loadImage("images/board4.jpg");
        PImage blurred = gaussianBlur(img);

        image(blurred,0,0);
        //save("blur.png");
        noLoop();
    }

    public void draw() {

    }

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ProcessingFiles.VideoStream.VideoStream"}, INST);

    }

    private PImage gaussianBlur(PImage img){
        return directedGaussianBlur(directedGaussianBlur(img, true), false);
    }
    private PImage directedGaussianBlur(PImage img, boolean performHorizontally){
        float sum;
        img.loadPixels();
        /* Convolve operation is separated in two rectangular matrices to save computations, namely 2*n instead of n^2 per image pixel  */
        //Vertical convolution
        for (int y = 1; y < img.height - 1; y++) {
            for (int x = 1; x < img.width - 1; x++) {
                sum = 0;

                if(performHorizontally){
                    int xp = y * img.width + x;
                    sum += gaussKernel[0] * brightness(img.pixels[xp - 1]);
                    sum += gaussKernel[1] * brightness(img.pixels[xp]);
                    sum += gaussKernel[2] * brightness(img.pixels[xp + 1]);
                } else{
                    sum += gaussKernel[0] * brightness(img.pixels[(y - 1) * img.width + x]);
                    sum += gaussKernel[1] * brightness(img.pixels[(y) * img.width + x]);
                    sum += gaussKernel[2] * brightness(img.pixels[(y + 1) * img.width + x]);
                }

                img.pixels[(y * img.width) + x] = color(sum);
            }
        }
        img.updatePixels();
        return img;
    }

    private float computeWeight(int[][] m) {
        int s = 0;
        for (int[] aM : m) {
            for (int anAM : aM) {
                s += anAM;
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
}
