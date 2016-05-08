package ProcessingFiles.VideoCapture;

import ProcessingFiles.imageFilter.HoughComparator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.video.Capture;

import java.util.*;

public class VideoStream extends PApplet {

    private final static int WIDTH = 640;
    private final static int HEIGHT = 480;

    // HSV bounds container
    private final HSVBounds hsvBounds = new HSVBounds();

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
    private final static int MIN_VOTES = 140;
    private final static int NEIGHBORHOOD_SIZE = 15;
    private final static int N_LINES = 16;
    private int phiDim;
    private int rDim;
    private int rOffset;
    private float[] sinTable;
    private float[] cosTable;
    Comparator<Integer> houghComparator;

    static final VideoStream INST = new VideoStream();
    Capture cam;
    PImage img;

    public void settings() {
        size(WIDTH * 2, HEIGHT);
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

    public void draw() {
        if (cam.available()) {
            cam.read();

            //IMAGE TREATMENT PIPELINE
            // 1. saturation threshold
            // 2. hue threshold
            // 3. brightness threshold
            // 4. sobel operator

            PImage hsvFiltered = brightnessThreshold(
                    hueThreshold(
                            saturationThreshold(cam.copy(), hsvBounds.getS_min(), hsvBounds.getS_max())
                            , hsvBounds.getH_min(), hsvBounds.getH_max())
                    , hsvBounds.getV_min(), false);

            image(hsvFiltered.copy(),WIDTH,0);

            PImage toDisplay = sobel(hsvFiltered);

            image(cam, 0, 0);
            getIntersections(hough(toDisplay, N_LINES));
        }
    }

    public void keyPressed(KeyEvent event) {
        switch (event.getKey()) {
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
        }

        println(hsvBounds);
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

    private List<PVector> hough(PImage edgeImg, int nLines) {

        /*============================================================
                                     LINE VOTING
          ============================================================*/

        Set<Integer> bestCandidates = new HashSet<>();
        List<Integer> bestCandidatesFiltered = new ArrayList<>();
        List<PVector> resultingLines = new ArrayList<>(4);

        // our accumulator (with a 1 pix margin around)
        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
        houghComparator = new HoughComparator(accumulator);
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
        bestCandidatesFiltered = new ArrayList<>();
        Iterator<Integer> it = bestCandidates.iterator();
        while (it.hasNext()) {
            int idx = it.next();
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


        /*PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        // You may want to resize the accumulator to make it easier to see:
        houghImg.resize(400, 400);
        houghImg.updatePixels();
        System.out.println("hough image computed");*/

        PGraphics pg = createGraphics(edgeImg.width, edgeImg.height);

        pg.beginDraw();
        //pg.image(houghImg, 0,0);

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
        pg.endDraw();
        image(pg.get(), 0, 0);
        return resultingLines;
    }

    public ArrayList<PVector> getIntersections(List<PVector> lines) {
        ArrayList<PVector> intersections = new ArrayList<PVector>();

        //TODO: Voir si on peu optimiser en réutilisant les sinTable et cosTable
        for (int i = 0; i < lines.size() - 1; i++) {
            PVector line1 = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                PVector line2 = lines.get(j);
                float r1 = line1.x;
                float phi1 = line1.y;
                float r2 = line2.x;
                float phi2 = line2.y;
                float d = cos(phi2) * sin(phi1) - cos(phi1) * sin(phi2);
                if (d != 0) {
                    PVector inter = new PVector(
                            ((r2 * sin(phi1)) - (r1 * sin(phi2))) / d,
                            ((r1 * cos(phi2)) - (r2 * cos(phi1))) / d
                    );
                    intersections.add(inter);
                    // draw the intersection
                    fill(255, 128, 0);
                    ellipse(inter.x, inter.y, 10, 10);
                }
            }
        }
        return intersections;
    }
}
