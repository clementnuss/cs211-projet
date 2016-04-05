package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;
import processing.core.PGraphics;
import ch.epfl.cs211.tools.Color;


/**
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public class SubScreen {

    public final static int VISUALISATION_HEIGHT = 75;
    public final static int VISUALISATION_WIDTH = Game.INSTANCE.width - 50;
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

    public SubScreen(float x, float y){
        this.x = x;
        this.y = y;

        backGroundView = Game.INSTANCE.createGraphics(VISUALISATION_WIDTH, VISUALISATION_HEIGHT);
        topView = Game.INSTANCE.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = Game.INSTANCE.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = Game.INSTANCE.createGraphics(CHART_WIDTH, CHART_HEIGHT);

    }

    public void draw(){
        drawBackgroundView();
        drawTopView();
        drawScoreView();
        drawChartView();
    };

    private void drawBackgroundView(){
        backGroundView.beginDraw();
        backGroundView.background(Color.SUBSCREEN_BACKGROUND_COLOR);
        backGroundView.fill(0);
        backGroundView.rect(0,0, VISUALISATION_WIDTH, VISUALISATION_HEIGHT);

        backGroundView.endDraw();
        Game.INSTANCE.image(backGroundView, x,y);
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
