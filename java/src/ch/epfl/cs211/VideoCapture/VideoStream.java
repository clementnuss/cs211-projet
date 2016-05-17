package ch.epfl.cs211.VideoCapture;

import ProcessingFiles.imageFilter.HoughComparator;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;
import static ch.epfl.cs211.Game.GAME;

import java.util.*;


public class VideoStream{

    private final static int WIDTH = 640;
    private final static int HEIGHT = 480;

    private boolean pause = false;

    // HSV bounds container
    private final HSVBounds hsvBounds = new HSVBounds();

    /*===============================================================
        Values for the Sobel operator
      ===============================================================*/

    private final float[] sobelKernel = {1f, 0f, -1f};
    private final float SOBEL_PERCENTAGE = 0.3f;

    /*===============================================================
        Values for the gauss operator
      ===============================================================*/

    private final float[] gaussKernel = {0.3f, 0.4f, 0.3f};

    /*===============================================================
        Values for the Hough transform
      ===============================================================*/
    private static float discretizationStepsPhi = 0.06f;
    private static float discretizationStepsR = 2.5f;
    private final static int MIN_VOTES = 130;
    private final static int NEIGHBORHOOD_SIZE = 10;
    private final static int N_LINES = 6;
    private int phiDim;
    private int rDim;
    private int rOffset;
    private float[] sinTable;
    private float[] cosTable;
    Comparator<Integer> houghComparator;

     /*===============================================================
        Values for the Quad selection
      ===============================================================*/

    public static final VideoStream INST = new VideoStream();
    Capture cam;
    PImage img;

    public VideoStream(){


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
            GAME.exit();
        } else {
            GAME.println("Available cameras:");
            for (String camera : cameras)
                GAME.println(camera);

            cam = new Capture(GAME, cameras[1]);
            cam.start();
            GAME.println("===========================================");
        }
    }


    public Quad captureQuad() {
        if (pause) {
            System.out.println("The program is paused .. press p to start it again");
        } else {
            if (cam.available()) {
                cam.read();

                //IMAGE TREATMENT PIPELINE
                // 1. saturation threshold
                // 2. hue threshold
                // 3. brightness B/W extraction
                // 4. Gaussian blurring
                // 5. intensity filtering
                // 6. Sobel
                // 7. Hough
                // 8. Quad selection

                PImage hsvFiltered =
                        intensityFilter(
                            gaussianBlur(
                                brightnessExtract(
                                        hueThreshold(
                                                saturationThreshold(cam.copy(), hsvBounds.getS_min(), hsvBounds.getS_max())
                                                , hsvBounds.getH_min(), hsvBounds.getH_max())
                                        , hsvBounds.getV_min(), hsvBounds.getV_max())
                            )
                        , hsvBounds.getIntensity());
                GAME.background(0);
                GAME.image(hsvFiltered, WIDTH, GAME.WINDOW_HEIGHT);
                PImage toDisplay = sobel(hsvFiltered);

                GAME.image(cam, 0, GAME.WINDOW_HEIGHT);


                List<PVector> lines = hough(toDisplay, N_LINES);

                if (lines != null && !lines.isEmpty()) {
                    QuadGraph.build(lines, cam.width, cam.height);

                    List<Quad> quads = QuadGraph.getQuads(lines);
                    int i = QuadGraph.indexOfBestQuad(quads);
                    if(i != -1){
                        Quad bestQuad = quads.get(i);
                        bestQuad.drawSurface();
                        bestQuad.drawCorners();
                        return bestQuad;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Turn black every pixel outside the thresholds
     * @param img
     * @param t1 Lower threshold
     * @param t2 Upper threshold
     * @return The reference to the input image (the input is modified)
     */
    private PImage saturationThreshold(PImage img, float t1, float t2) {
        img.loadPixels();

        for (int i = 0; i < img.width * img.height; i++) {
            int originalColor = img.pixels[i];
            float sat = GAME.saturation(originalColor);
            img.pixels[i] = (t1 <= sat && sat <= t2) ? originalColor : 0x0;
        }
        img.updatePixels();
        return img;
    }

    /**
     * Filter the image based on its brightness. Every pixel
     * whose brightness is between the lower threshold and the upper threshold is painted
     * WHITE, otherwise it is painted BLACK.
     *
     * @param img
     * @param t1  Lower threshold
     * @param t2  Upper threshold
     * @return A reference to the input image (the input is modified)
     */
    private PImage brightnessExtract(PImage img, float t1, float t2) {
        img.loadPixels();
        for (int i = 0; i < img.width * img.height; i++) {
            float b = GAME.brightness(img.pixels[i]);
            img.pixels[i] = (t1 < b && b < t2) ? 0xFFFFFFFF : 0x0;
        }
        img.updatePixels();
        return img;
    }

    /**
     * Turn black every pixel lower than the given threshold
     * @param img
     * @param t Threshold
     * @return The reference to the input image (the input is modified)
     */
    private PImage intensityFilter(PImage img, float t) {
        img.loadPixels();
        for (int i = 0; i < img.width * img.height; i++) {
            img.pixels[i] = (GAME.brightness(img.pixels[i]) > t) ? 0xFFFFFFFF : 0x0;
        }
        img.updatePixels();
        return img;
    }

    /**
     * Turn black every pixel outside the thresholds
     * @param img
     * @param t1 Lower threshold
     * @param t2 Upper threshold
     * @return The reference to the input image (the input is modified)
     */
    private PImage hueThreshold(PImage img, float t1, float t2) {
        img.loadPixels();
        int originalColor;
        float originalColorHue;

        for (int i = 0; i < img.width * img.height; i++) {
            originalColor = img.pixels[i];
            originalColorHue = GAME.hue(originalColor);
            img.pixels[i] = (t1 <= originalColorHue && originalColorHue <= t2) ? originalColor : 0x0;
        }
        img.updatePixels();
        return img;
    }

    /**
     * Perform separately a horizontal and vertical gaussian blur
     * @param img
     * @return The reference to the input image (the input is modified)
     */
    private PImage gaussianBlur(PImage img) {
        return directedGaussianBlur(directedGaussianBlur(img, true), false);
    }

    /**
     * Perform a gaussian blur along one direction
     * @param img
     * @param performHorizontally
     * @return The reference to the input image (the input is modified)
     */
    private PImage directedGaussianBlur(PImage img, boolean performHorizontally) {
        float sum;
        img.loadPixels();

        /* Convolve operation is separated in two rectangular matrices to save computations,
        namely 2*n instead of n^2 per image pixel  */
        for (int y = 1; y < img.height - 1; y++) {
            for (int x = 1; x < img.width - 1; x++) {
                sum = 0;

                if (performHorizontally) {
                    int xp = y * img.width + x;
                    sum += gaussKernel[0] * GAME.brightness(img.pixels[xp - 1]);
                    sum += gaussKernel[1] * GAME.brightness(img.pixels[xp]);
                    sum += gaussKernel[2] * GAME.brightness(img.pixels[xp + 1]);
                } else {
                    sum += gaussKernel[0] * GAME.brightness(img.pixels[(y - 1) * img.width + x]);
                    sum += gaussKernel[1] * GAME.brightness(img.pixels[(y) * img.width + x]);
                    sum += gaussKernel[2] * GAME.brightness(img.pixels[(y + 1) * img.width + x]);
                }

                img.pixels[(y * img.width) + x] = GAME.color(sum);
            }
        }
        img.updatePixels();
        return img;
    }

    /**
     * Perform the sobel operator
     * @param img
     * @return A new PImage containing the result of the sobel operator
     */
    private PImage sobel(PImage img) {
        float sum_h;
        float sum_v;
        float max = 0f;
        float[][] buffer = new float[img.height][img.width];
        PImage result = GAME.createImage(img.width, img.height, GAME.ALPHA);

        /* Convolve operation is separeted in two rectangular matrices to save computations,
        namely 2*n instead of n^2 per image pixel  */
        //Vertical convolution
        for (int y = 1; y < img.height - 1; y++) {
            for (int x = 1; x < img.width - 1; x++) {
                sum_h = 0;
                sum_v = 0;

                //Horizontal convolution
                int xp = y * img.width + x;
                sum_h += sobelKernel[0] * GAME.brightness(img.pixels[xp - 1]);
                sum_h += sobelKernel[2] * GAME.brightness(img.pixels[xp + 1]);

                //Vertical convolution
                sum_v += sobelKernel[0] * GAME.brightness(img.pixels[(y - 1) * img.width + x]);
                sum_v += sobelKernel[2] * GAME.brightness(img.pixels[(y + 1) * img.width + x]);

                //Compute de gradient
                float sum = GAME.sqrt(GAME.pow(sum_h, 2) + GAME.pow(sum_v, 2));
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

    /**
     * Perform a Hough transform
     * @param edgeImg A sobel-transformed image
     * @param nLines The number of lines to pick amoung the best returned by the transform
     * @return  A list of lines in polar coordinates
     */
    private List<PVector> hough(PImage edgeImg, int nLines) {

        /*============================================================
                                     LINE VOTING
          ============================================================*/

        Set<Integer> bestCandidates = new HashSet<Integer>();
        List<Integer> bestCandidatesFiltered = new ArrayList<Integer>();
        List<PVector> resultingLines = new ArrayList<>(N_LINES);

        // our accumulator (with a 1 pix margin around)
        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
        houghComparator = new HoughComparator(accumulator);
        for (int y = 0; y < edgeImg.height; y++) {
            for (int x = 0; x < edgeImg.width; x++) {

                // Are we on an edge? Since image is BLACK/WHITE we can just check the LSB
                if ((edgeImg.pixels[y * edgeImg.width + x] & 1) != 0) {
                    for (int phi = 0; phi < phiDim; phi++) {
                        double r = (x * cosTable[phi] + y * sinTable[phi]) / discretizationStepsR;
                        r += rOffset;
                        int idx = ((int) r) + (phi + 1) * (rDim + 2);
                        accumulator[idx]++;

                        if (accumulator[idx] == MIN_VOTES) {
                            bestCandidates.add(idx);
                        }
                    }
                }
            }
        }

        /*============================================================
                     LOCAL MAXIMA SELECTION USING SET<INTEGER>
          ============================================================*/
        for (Integer idx : bestCandidates) {
            int accPhi = (idx / (rDim + 2)) - 1;
            int accR = (idx % (rDim + 2)) - 1;
            boolean bestCandidate = true;
            // iterate over the neighbourhood
            for (int dPhi = -NEIGHBORHOOD_SIZE / 2; dPhi < NEIGHBORHOOD_SIZE / 2 + 1; dPhi++) {
                // check we are not outside the image
                if (accPhi + dPhi < 0 || accPhi + dPhi >= phiDim) continue;
                for (int dR = -NEIGHBORHOOD_SIZE / 2; dR < NEIGHBORHOOD_SIZE / 2 + 1; dR++) {
                    // check we are not outside the image
                    if (accR + dR < 0 || accR + dR >= rDim) continue;
                    int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2) + accR + dR + 1;
                    if (accumulator[idx] < accumulator[neighbourIdx]) {
                        // the current idx is not a local maximum!
                        bestCandidate = false;
                        break;
                    }
                }
                if (!bestCandidate) break;
            }
            if (bestCandidate) {
                // the current idx *is* a local maximum
                bestCandidatesFiltered.add(idx);
            }

        }

        Collections.sort(bestCandidatesFiltered, houghComparator);

        PImage houghImg = GAME.createImage(rDim + 2, phiDim + 2, GAME.ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = GAME.color(GAME.min(255, accumulator[i]));
        }

        // You may want to resize the accumulator to make it easier to see:
        houghImg.resize(200, HEIGHT);
        houghImg.updatePixels();

        PGraphics pg = GAME.createGraphics(edgeImg.width, edgeImg.height);
        pg.beginDraw();

        //This is to ensure we can indeed draw nLines
        int candidatesLength = bestCandidatesFiltered.size();
        if (candidatesLength < nLines) nLines = candidatesLength;

        for (int i = 0; i < nLines; i++) {
            int idx = bestCandidatesFiltered.get(i);
            // first, compute back the (r, phi) polar coordinates:
            int accPhi = (idx / (rDim + 2)) - 1;
            //int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
            int accR = (idx % (rDim + 2)) - 1;
            float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
            float phi = accPhi * discretizationStepsPhi;

            resultingLines.add(new PVector(r, phi));

            int x0 = 0;
            int y0 = (int) (r / GAME.sin(phi));
            int x1 = (int) (r / GAME.cos(phi));
            int y1 = 0;
            int x2 = edgeImg.width;
            int y2 = (int) (-GAME.cos(phi) / GAME.sin(phi) * x2 + r / GAME.sin(phi));
            int y3 = edgeImg.height;
            int x3 = (int) (-(y3 - r / GAME.sin(phi)) * (GAME.sin(phi) / GAME.cos(phi)));

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
        pg.endDraw();
        GAME.image(img, 0, 0);
        GAME.image(pg.get(), 0, 0);
        GAME.image(houghImg, WIDTH, 0);
        return resultingLines;
    }

}
