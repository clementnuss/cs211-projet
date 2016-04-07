/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */
package ch.epfl.cs211.tools;

import static ch.epfl.cs211.Game.GAME;

/**
 * Class implementing the head-up display (all the debug information that appears on the screen)
 */
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
        GAME.textSize(14);
        GAME.fill(color);
        GAME.text(text, x, y, width, height);
    }

}
