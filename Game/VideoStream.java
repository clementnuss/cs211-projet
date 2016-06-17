
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.video.*;

import java.util.*;

class VideoStream extends PApplet {

    private final static int WIDTH = 640;
    private final static int HEIGHT = 480;
    private static final float SMOOTHING_STEPS =3;

    private final SynchronizedRotationValue syncRot;
    String dataPath;
    private boolean pause = false;

    // Rotation of the plate
    private PVector smoothedRotation = new PVector(0, 0, 0), rotation = new PVector(0, 0, 0);
    private long lastSmoothRotationUpdate = 0;
    private float smoothingCoeffX, smoothingCoeffY;

    private TwoDThreeD from2Dto3Dtransformer;
    private boolean newBoardValue;
    private int smoothSteps = 0;


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
    private static float discretizationStepsPhi = 0.02f;
    private static float discretizationStepsR = 1.8f;
    private final static int MIN_VOTES = 120;
    private final static int NEIGHBORHOOD_SIZE = 16;
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

    Movie mov;
    Quad capturedBoard;
    QuadGraph qGraph;

    public VideoStream(SynchronizedRotationValue r, String gamePath){
        syncRot = r;
        dataPath = gamePath;
    }

    public void settings() {
        size(WIDTH * 2, HEIGHT);
    }

    public void setup() {

        println("Inner's dataPath: \t\"" + dataPath("") + "\"\n");
        println("Game's dataPath: \t\"" + dataPath + "\"\n");

      
        mov = new Movie(this, "testvideo.mp4");
        mov.loop();

        from2Dto3Dtransformer = new TwoDThreeD(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

        qGraph = new QuadGraph(this);
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

    }


    public void draw() {
        if (pause) {
            System.out.println("The program is paused .. press p to start it again");
        } else {
            if (mov.available()) {
                mov.read();

                //IMAGE TREATMENT PIPELINE
                // 1. saturation threshold
                // 2. hue threshold
                // 3. brightness B/W extraction
                // 4. Gaussian blurring
                // 5. intensity filtering
                // 6. Sobel
                // 7. Hough
                // 8. Quad selection

                PImage hsvFiltered = intensityFilter(
                        gaussianBlur(
                                hsvFilter(mov.copy(), hsvBounds)
                        )
                        , hsvBounds.getIntensity());

                background(0);
                image(hsvFiltered, WIDTH, 0);
                PImage toDisplay = sobel(hsvFiltered);

                image(mov, 0, 0);


                List<PVector> lines = hough(toDisplay, N_LINES);

                if (lines != null && !lines.isEmpty()) {
                    qGraph.build(lines, mov.width, mov.height);

                    List<Quad> quads = qGraph.getQuads(lines);
                    int i = qGraph.indexOfBestQuad(quads);
                    if (i != -1) {
                        capturedBoard = quads.get(i);
                        capturedBoard.drawSurface();
                        capturedBoard.drawCorners();
                        newBoardValue = true;
                    }
                }

            }

            if (newBoardValue) {
                PVector newRotation = from2Dto3Dtransformer.get3DRotations(capturedBoard.cornersAsList());
                //            println("Got a rotation: ", boardRotation.x, boardRotation.y, boardRotation.z);
                syncRot.setRot(newRotation);
                newBoardValue = false;
            }


            // We want to (smoothly) update the position of the plate at a 20 FPS rate
            if ((System.currentTimeMillis() - lastSmoothRotationUpdate) >= 10) {

                if (smoothSteps++ < SMOOTHING_STEPS) {
                    smoothedRotation.x += smoothingCoeffX;
                    smoothedRotation.y += smoothingCoeffY;
                    lastSmoothRotationUpdate = System.currentTimeMillis();
                }

            }
        }
    }

    public Quad getCapturedBoard() {
        return capturedBoard;
    }

    /**
     * Filters img using the given HSV bounds.
     *
     * @param img
     * @param bounds the HSV bounds
     * @return The reference to the input image (the input is modified)
     */
    private PImage hsvFilter(PImage img, HSVBounds bounds) {

        float minH = bounds.getH_min();
        float maxH = bounds.getH_max();
        float minS = bounds.getS_min();
        float maxS = bounds.getS_max();
        float minV = bounds.getV_min();
        float maxV = bounds.getV_max();

        img.loadPixels();

        for (int i = 0; i < img.width * img.height; i++) {


            int originalColor = img.pixels[i];
            float s = saturation(originalColor);


            if (minS <= s && s <= maxS) {
                float h = hue(originalColor);
                if (minH <= h && h <= maxH) {
                    float v = brightness(originalColor);
                    if (minV <= v && v <= maxV) {
                        img.pixels[i] = 0xFFFFFFFF;
                        continue;
                    }
                }
            }
            img.pixels[i] = 0x0;
        }
        img.updatePixels();
        return img;
    }



    /**
     * Turn black every pixel lower than the given threshold
     *
     * @param img
     * @param t   Threshold
     * @return The reference to the input image (the input is modified)
     */
    private PImage intensityFilter(PImage img, float t) {
        img.loadPixels();
        for (int i = 0; i < img.width * img.height; i++) {
            img.pixels[i] = (brightness(img.pixels[i]) > t) ? 0xFFFFFFFF : 0x0;
        }
        img.updatePixels();
        return img;
    }


    /**
     * Perform separately a horizontal and vertical gaussian blur
     *
     * @param img
     * @return The reference to the input image (the input is modified)
     */
    private PImage gaussianBlur(PImage img) {
        return directedGaussianBlur(directedGaussianBlur(img, true), false);
    }

    /**
     * Perform a gaussian blur along one direction
     *
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
                    sum += gaussKernel[0] * brightness(img.pixels[xp - 1]);
                    sum += gaussKernel[1] * brightness(img.pixels[xp]);
                    sum += gaussKernel[2] * brightness(img.pixels[xp + 1]);
                } else {
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

    /**
     * Perform the sobel operator
     *
     * @param img
     * @return A new PImage containing the result of the sobel operator
     */
    private PImage sobel(PImage img) {
        float sum_h;
        float sum_v;
        float max = 0f;
        float[][] buffer = new float[img.height][img.width];
        PImage result = createImage(img.width, img.height, ALPHA);

        /* Convolve operation is separeted in two rectangular matrices to save computations,
        namely 2*n instead of n^2 per image pixel  */
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

                //Compute the gradient
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

    /**
     * Perform a Hough transform
     *
     * @param edgeImg A sobel-transformed image
     * @param nLines  The number of lines to pick amoung the best returned by the transform
     * @return A list of lines in polar coordinates
     */
    private List<PVector> hough(PImage edgeImg, int nLines) {

        /*============================================================
                                     LINE VOTING
          ============================================================*/

        Set<Integer> bestCandidates = new HashSet<Integer>();
        List<Integer> bestCandidatesFiltered = new ArrayList<Integer>();
        List<PVector> resultingLines = new ArrayList(N_LINES);

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

        /*
        PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        // You may want to resize the accumulator to make it easier to see:
        houghImg.resize(200, HEIGHT);
        houghImg.updatePixels();
*/
        PGraphics pg = createGraphics(edgeImg.width, edgeImg.height);
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
        pg.endDraw();
        image(mov, 0, 0);
        image(pg.get(), 0, 0);
        return resultingLines;
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKey()) {
            //Pause the webcam
            case 'p':
                if (pause)
                    loop();
                else
                    noLoop();

                pause = !pause;
                System.out.println("The program is paused, press p to resume it");
                break;

            //Sets the hue threshold
            case 'q':
                hsvBounds.setH_min(hsvBounds.getH_min() - 3);
                break;
            case 'w':
                hsvBounds.setH_min(hsvBounds.getH_min() + 3);
                break;
            case 'a':
                hsvBounds.setH_max(hsvBounds.getH_max() - 3);
                break;
            case 's':
                hsvBounds.setH_max(hsvBounds.getH_max() + 3);
                break;

            // Sets the saturation threshold
            case 'e':
                hsvBounds.setS_min(hsvBounds.getS_min() - 3);
                break;
            case 'r':
                hsvBounds.setS_min(hsvBounds.getS_min() + 3);
                break;
            case 'd':
                hsvBounds.setS_max(hsvBounds.getS_max() - 3);
                break;
            case 'f':
                hsvBounds.setS_max(hsvBounds.getS_max() + 3);
                break;

            // Sets the value threshold
            case 't':
                hsvBounds.setV_min(hsvBounds.getV_min() - 3);
                break;
            case 'z':
                hsvBounds.setV_min(hsvBounds.getV_min() + 3);
                break;
            case 'g':
                hsvBounds.setV_max(hsvBounds.getV_max() - 3);
                break;
            case 'h':
                hsvBounds.setV_max(hsvBounds.getV_max() + 3);
                break;
            case 'u':
                hsvBounds.set_intensity(hsvBounds.getIntensity() - 1);
                break;
            case 'i':
                hsvBounds.set_intensity(hsvBounds.getIntensity() + 1);
                break;
            case 'j':
                hsvBounds.set_intensity(hsvBounds.getIntensity() - 0.05f);
                break;
            case 'k':
                hsvBounds.set_intensity(hsvBounds.getIntensity() + 0.05f);
                break;
        }

        println(hsvBounds);
    }

    public PVector getRotation() {
        return smoothedRotation;
    }
}