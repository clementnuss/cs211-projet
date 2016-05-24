package ch.epfl.cs211.VideoCapture;

import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
        // First pass: label the pixels and store labels' equivalences
        int[] labels = new int[input.width * input.height];
        List<TreeSet<Integer>> labelsEquivalences = new ArrayList<TreeSet<Integer>>();
        int currentLabel = 1;
        // TODO!
        // Second pass: re-label the pixels by their equivalent class
        // TODO!
        // Finally, output an image with each blob colored in one uniform color.
        // TODO!

        return null;
    }
}