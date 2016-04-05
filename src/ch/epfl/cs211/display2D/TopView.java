package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;

public final class TopView extends SubScreen {

    public TopView(int w, int h, float x, float y) {
        super(w, h, x, y);
    }

    @Override
    public void draw() {
        pGraphics.beginDraw();

        pGraphics.endDraw();
        Game.INSTANCE.image(pGraphics, 10, 10);
    }
}
