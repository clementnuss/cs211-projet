package ProcessingFiles.Test;

import ProcessingFiles.VideoCapture.QuadGraph;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Leandro on 10.05.2016.
 */
public class SobelTest extends PApplet{

    public void settings() {
        size(800, 600);
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
            for (String camera : cameras)
                println(camera);

            cam = new Capture(this, cameras[1]);
            cam.start();
            println("===========================================");
        }
    }

    // 44-141   57-255  87-255 246
    public void draw() {
        if (pause) {
            System.out.println("The program is paused .. press p to start it again");
        } else {
            if (cam.available()) {
                cam.read();

                //IMAGE TREATMENT PIPELINE
                // 1. saturation threshold
                // 2. hue threshold
                // 3. brightness threshold
                // 4. sobel operator

                /*PImage hsvFiltered =
                        intensityFilter(
                           brightnessExtract(
                            hueThreshold(
                                saturationThreshold(cam.copy(), hsvBounds.getS_min(), hsvBounds.getS_max())
                                , hsvBounds.getH_min(), hsvBounds.getH_max())
                            , hsvBounds.getV_min(), hsvBounds.getV_max())
                        , hsvBounds.getIntensity()); */

                PImage hsvFiltered =
                        convolve(
                                brightnessExtract(
                                        hueThreshold(
                                                saturationThreshold(cam.copy(), hsvBounds.getS_min(), hsvBounds.getS_max())
                                                , hsvBounds.getH_min(), hsvBounds.getH_max())
                                        , hsvBounds.getV_min(), hsvBounds.getV_max())
                                , gaussianKernel);
                background(0);

                image(hsvFiltered, WIDTH, 0);
                PImage toDisplay = sobel(hsvFiltered);

                image(cam, 0, 0);

//                List<PVector> intersections = getIntersections(hough(toDisplay, N_LINES));
                List<PVector> lines = hough(toDisplay, N_LINES);

                if (lines != null && !lines.isEmpty()) {
                    QuadGraph.build(lines, cam.width, cam.height);

                    List<int []> quads = QuadGraph.findCycles();


                    for (int[] quad : quads) {
                        PVector l1 = lines.get(quad[0]);
                        PVector l2 = lines.get(quad[1]);
                        PVector l3 = lines.get(quad[2]);
                        PVector l4 = lines.get(quad[3]);

                        PVector c12 = intersection(l1, l2);
                        PVector c23 = intersection(l2, l3);
                        PVector c34 = intersection(l3, l4);
                        PVector c41 = intersection(l4, l1);

                        if(QuadGraph.validArea(c12,c23,c34,c41, QuadGraph.QUAD_MAX_AREA, QuadGraph.QUAD_MIN_AREA)
                                && QuadGraph.isConvex(c12,c23,c34,c41)
                                && QuadGraph.nonFlatQuad(c12,c23,c34,c41)){
                            fill(color(250, 102, 7));
                            quad(c12.x, c12.y, c23.x, c23.y, c34.x, c34.y, c41.x, c41.y);
                        }
                    }
                }
            }
        }
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
}
