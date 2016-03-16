package ch.epfl.cs211.tools;

import ch.epfl.cs211.Game;

/**
 * Visual Computing project (CS211) - 2016
 * Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 */
public class HUD {

    private int x, y, width, height, z;
    private Color color;

    /**
     *
     * @param x HUD box x position
     * @param y HUD box y position
     * @param width    HUD box width
     * @param height    HUD box height
     */
    public HUD(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public void display(String text) {
        Game.INSTANCE.textSize(14);
        Game.INSTANCE.fill(color.getV1(),color.getV2(), color.getV3(), color.getAlpha());
        Game.INSTANCE.text(text, x, y, width, height);  // Specify a z-axis value
    }

}
