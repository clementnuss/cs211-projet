package ch.epfl.cs211.VideoCapture;

import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.*;
import java.util.List;

import static ch.epfl.cs211.Game.GAME;

public class BlobDetection {
    Polygon quad = new Polygon();

    /**
     * Create a blob detection instance with the four corners of the Lego board.
     */
    BlobDetection(PVector c1, PVector c2, PVector c3, PVector c4) {
        quad.addPoint((int) c1.x, (int) c1.y);
        quad.addPoint((int) c2.x, (int) c2.y);
        quad.addPoint((int) c3.x, (int) c3.y);
        quad.addPoint((int) c4.x, (int) c4.y);
    }

    /**
     * Returns true if a (x,y) point lies inside the quad
     */
    boolean isInQuad(int x, int y) {
        return quad.contains(x, y);
    }

    PImage findConnectedComponents(PImage input) {
        int imageLength = input.width * input.height;
        // First pass: label the pixels and store labels' equivalences
        int[] labels = new int[imageLength];
        List<TreeSet<Integer>> labelsEquivalences = new ArrayList<TreeSet<Integer>>();
        List<Integer> neighborsList = new ArrayList<>(4);
        List<Integer> labelsList = new ArrayList<>(4);
        int currentLabel = 1;
        int yp, prevYp;
        //The first row and first column always have different labels
        for(int y=0; y<input.height; y++) {
            yp = y * input.width;
            prevYp = (y-1) * input.width;
            for (int x = 0; x < input.width; x++) {
                neighborsList.clear();
                labelsList.clear();
                if(x==0 || y ==0)
                    labels[yp + x] = currentLabel++;
                else {
                    int C = yp + x;
                    float cH = GAME.hue(input.pixels[C]);

                    int W = yp + x - 1;
                    labelsList.add(labels[W]);
                    float hW = GAME.hue(input.pixels[W]);

                    int NW = prevYp + x -1;
                    labelsList.add(labels[NW]);
                    float hNW = GAME.hue(input.pixels[NW]);

                    int N = prevYp + x;
                    labelsList.add(labels[N]);
                    float hN = GAME.hue(input.pixels[N]);

                    int NE = prevYp + x + 1;
                    labelsList.add(labels[NE]);
                    float hNE = (x==input.width) ? -1 : GAME.hue(input.pixels[NE]);

                    if(cH == hW)
                        neighborsList.add(W);
                    if(cH == hNW)
                        neighborsList.add(NW);
                    if(cH == hN)
                        neighborsList.add(N);
                    if(cH == hNE)
                        neighborsList.add(NE);

                    if(neighborsList.isEmpty()){
                        labels[C] = currentLabel++;
                    } else {
                        Collections.sort(labelsList);
                        labels[C] = labelsList.get(0);
                        boolean found = false;
                        for(TreeSet set : labelsEquivalences){

                        }
                    }

                }
            }
        }

        // TODO!
        // Second pass: re-label the pixels by their equivalent class
        // TODO!
        // Finally, output an image with each blob colored in one uniform color.
        // TODO!

        return null;
    }
}