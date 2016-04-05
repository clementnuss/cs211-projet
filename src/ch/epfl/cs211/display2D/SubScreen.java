package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;
import processing.core.PGraphics;


/**
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public abstract class SubScreen {
    protected final PGraphics pGraphics;

    public SubScreen(int w, int h){
        pGraphics = Game.INSTANCE.createGraphics(w,h);
    }

    public abstract void draw();
}
