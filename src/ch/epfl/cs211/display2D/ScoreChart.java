package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;

public class ScoreChart extends SubScreen {

    public ScoreChart(int w, int h, float x, float y) {
        super(w, h, x, y);
    }

    @Override
    public void draw() {
        pGraphics.beginDraw();

        pGraphics.endDraw();
        Game.INSTANCE.image(pGraphics, 10, 10);
    }
}
