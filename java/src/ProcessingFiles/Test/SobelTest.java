package ProcessingFiles.Test;

import ProcessingFiles.VideoCapture.QuadGraph;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

import java.util.List;
import java.util.function.BiFunction;

public class SobelTest extends PApplet{

    public static final SobelTest INST = new SobelTest();

    private final float[] sobelKernel = {1f, 0f, -1f};

    private final float SOBEL_PERCENTAGE = 0.1f;

    private final static float sobelKernelX[][] = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}};

    private final static float sobelKernelY[][] = {
            {-1, -2, -1},
            {0,  0,  0},
            {1,  2,  1}};

    public void settings() {
        size(800, 600);
    }

    public void setup() {

        PImage img = loadImage("images/board4.jpg");
        image(trueSobel(img),0,0);
        noLoop();
    }

    public void draw() {

    }

    public static void main(String[] args) {

        PApplet.runSketch(new String[]{"ProcessingFiles.VideoStream.VideoStream"}, INST);

    }

    private PImage trueSobel(PImage img){
        BiFunction<Integer, Integer, Integer> indexAt = (Integer x, Integer y) -> (y*img.width) + x;
        float sum_h;
        float sum_v;
        float max = 0f;
        float[][] buffer = new float[img.height][img.width];
        PImage result = createImage(img.width, img.height, ALPHA);

        //Vertical convolution
        for (int y = 1; y < img.height - 1; y++) {
            for (int x = 1; x < img.width - 1; x++) {
                sum_h = 0;
                sum_v = 0;

                //Horizontal convolution
                sum_h += sobelKernelX[0][0] * brightness(img.pixels[indexAt.apply(x-1,y-1)]);
                sum_h += sobelKernelX[0][2] * brightness(img.pixels[indexAt.apply(x+1,y-1)]);

                sum_h += sobelKernelX[1][0] * brightness(img.pixels[indexAt.apply(x-1,y)]);
                sum_h += sobelKernelX[1][2] * brightness(img.pixels[indexAt.apply(x+1,y)]);

                sum_h += sobelKernelX[2][0] * brightness(img.pixels[indexAt.apply(x-1,y+1)]);
                sum_h += sobelKernelX[2][2] * brightness(img.pixels[indexAt.apply(x+1,y+1)]);

                //Vertical convolution
                sum_v += sobelKernelY[0][0] * brightness(img.pixels[indexAt.apply(x-1,y-1)]);
                sum_v += sobelKernelY[0][2] * brightness(img.pixels[indexAt.apply(x+1,y-1)]);

                sum_v += sobelKernelY[1][0] * brightness(img.pixels[indexAt.apply(x-1,y)]);
                sum_v += sobelKernelY[1][2] * brightness(img.pixels[indexAt.apply(x+1,y)]);

                sum_v += sobelKernelY[2][0] * brightness(img.pixels[indexAt.apply(x-1,y+1)]);
                sum_v += sobelKernelY[2][2] * brightness(img.pixels[indexAt.apply(x+1,y+1)]);

                //Compute de gradient
                float sum = ceil(sqrt(pow(sum_h, 2) + pow(sum_v, 2)));

                result.pixels[indexAt.apply(x,y)] = color(sum);
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
}
