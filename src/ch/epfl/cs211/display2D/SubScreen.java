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

    public final static int VISUALISATION_HEIGHT = 120;
    public final static int VISUALISATION_WIDTH = Game.INSTANCE.width;
    public final static int VISUALISATION_OFFSET = 20;
    public final static int TOP_HEIGHT = 75;
    public final static int TOP_WIDTH = 75;
    public final static int SCORE_WIDTH = 75;
    public final static int SCORE_HEIGHT = 75;
    public final static int CHART_WIDTH = VISUALISATION_WIDTH - TOP_WIDTH - SCORE_WIDTH - 4*VISUALISATION_OFFSET;
    public final static int CHART_HEIGHT = 80;

    private final PGraphics topView;
    private final PGraphics scoreBoard;
    private final PGraphics scoreChart;
    private final PGraphics backGroundView;

    private final float backGroundX;
    private final float backGroundY;
    private final float topViewX;
    private final float topViewY;
    private final float scoreBoardX;
    private final float scoreBoardY;
    private final float scoreChartX;
    private final float scoreChartY;

    public SubScreen(float backGroundX, float backGroundY){
        this.backGroundX = backGroundX;
        this.backGroundY = backGroundY;

        backGroundView = Game.INSTANCE.createGraphics(VISUALISATION_WIDTH, VISUALISATION_HEIGHT);
        topView = Game.INSTANCE.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = Game.INSTANCE.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = Game.INSTANCE.createGraphics(CHART_WIDTH, CHART_HEIGHT);

        topViewX = backGroundX + VISUALISATION_OFFSET;
        topViewY = backGroundY + VISUALISATION_OFFSET;
        scoreBoardX = topViewX + TOP_WIDTH + VISUALISATION_OFFSET;
        scoreBoardY = backGroundY + VISUALISATION_OFFSET;
        scoreChartX = scoreBoardX + SCORE_WIDTH + VISUALISATION_OFFSET;
        scoreChartY = backGroundY + VISUALISATION_OFFSET / 2;
    }

    public void draw(){
        Game.INSTANCE.noLights();
        drawBackgroundView();
        drawTopView();
        drawScoreView();
        drawChartView();
    };

    private void drawBackgroundView(){
        backGroundView.beginDraw();
        backGroundView.fill(Color.SUBSCREEN_BACKGROUND_COLOR);
        backGroundView.noStroke();
        backGroundView.rect(0,0, VISUALISATION_WIDTH, VISUALISATION_HEIGHT);
        backGroundView.endDraw();
        Game.INSTANCE.image(backGroundView, backGroundX, backGroundY);
    }
    private void drawTopView(){
        topView.beginDraw();
        topView.fill(Color.SUBSCREEN_TOPVIEW_COLOR);
        topView.noStroke();
        topView.rect(0,0, TOP_WIDTH, TOP_HEIGHT);
        topView.endDraw();
        Game.INSTANCE.image(topView, topViewX, topViewY);
    }
    private void drawScoreView(){
        scoreBoard.beginDraw();
        scoreBoard.fill(0);
        scoreBoard.rect(0,0, VISUALISATION_WIDTH, VISUALISATION_HEIGHT);
        scoreBoard.endDraw();
        Game.INSTANCE.image(scoreBoard, scoreBoardX, scoreBoardY);
    }
    private void drawChartView(){
        scoreChart.beginDraw();
        scoreChart.fill(Color.SUBSCREEN_CHART_COLOR);
        scoreChart.noStroke();
        scoreChart.rect(0,0, CHART_WIDTH, CHART_HEIGHT);
        scoreChart.endDraw();
        Game.INSTANCE.image(scoreChart, scoreChartX, scoreChartY);
    }
}
