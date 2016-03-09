package ch.epfl.cs211.tools;

import processing.core.PApplet;

/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class HUD {

    private int x, y, width, height, z;
    private Color color;
    private final PApplet p;

    /**
     *
     * @param x HUD box x position
     * @param y HUD box y position
     * @param width    HUD box width
     * @param height    HUD box height
     * @param p parent PApplet
     */
    public HUD(int x, int y, int width, int height, Color color, PApplet p) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.p = p;
        this.color = color;
    }

    public void display(String text) {
        p.textSize(14);
        p.fill(color.getV1(),color.getV2(), color.getV3(), color.getAlpha());
        p.text(text, x, y, width, height);  // Specify a z-axis value
    }

}
