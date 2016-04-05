package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;
import processing.core.PGraphics;


/**
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public abstract class SubScreen {

    public final static int VISUALISATION_HEIGHT = 75;
    public final static int TOP_HEIGHT = 75;
    public final static int TOP_WIDTH = 75;
    public final static int SCORE_WIDTH = 75;
    public final static int SCORE_HEIGHT = 75;
    public final static int CHART_WIDTH = 75;
    public final static int CHART_HEIGHT = 75;

    private final PGraphics topView;
    private final PGraphics scoreBoard;
    private final PGraphics scoreChart;
    private final PGraphics backGroundView;

    private final float x;
    private final float y;
    private final float w;
    private final float h;

    public SubScreen(int w, int h, float x, float y){
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;

        backGroundView = Game.INSTANCE.createGraphics(w,h);
        topView = Game.INSTANCE.createGraphics(w,h);
        scoreBoard = Game.INSTANCE.createGraphics(w,h);
        scoreChart = Game.INSTANCE.createGraphics(w,h);

    }

    public void draw(){};

    private void drawBackgroundView(){
        backGroundView.beginDraw();

        backGroundView.endDraw();
        Game.INSTANCE.image(backGroundView, 0,0);
    }
    private void drawTopView(){
        topView.beginDraw();

        topView.endDraw();
        Game.INSTANCE.image(topView, 0,0);
    }
    private void drawScoreView(){
        scoreBoard.beginDraw();

        scoreBoard.endDraw();
        Game.INSTANCE.image(scoreBoard, 0,0);
    }
    private void drawChartView(){
        scoreChart.beginDraw();

        scoreChart.endDraw();
        Game.INSTANCE.image(scoreChart, 0,0);
    }
}
