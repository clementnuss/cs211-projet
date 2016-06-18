/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;

public class HUD {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int color;

    /**
     *
     * @param x HUD box x position
     * @param y HUD box y position
     * @param width    HUD box width
     * @param height    HUD box height
     */
    public HUD(int x, int y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    /**
     *  renders the given text on the HUD
     *
     * @param text  the text to be drawn in the HUD
     */
    public void display(String text) {
        Game.GAME.textSize(14);
        Game.GAME.fill(color);
        Game.GAME.text(text, x, y, width, height);  // Specify a z-axis value
    }

}