/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */


public class HUD {

    private int x, y, width, height, z;
    private int color;

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
        TangibleGame.GAME.textSize(14);
        TangibleGame.GAME.fill(color);
        TangibleGame.GAME.text(text, x, y, width, height);  // Specify a z-axis value
    }

}