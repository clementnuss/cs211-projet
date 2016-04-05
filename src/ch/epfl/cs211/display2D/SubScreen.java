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

    public final static int VISUALISATION_HEIGHT = 100;
    public final static int VISUALISATION_WIDTH = Game.INSTANCE.width - 50;
    public final static int VISUALISATION_OFFSET = 25;
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
        scoreBoardY = VISUALISATION_OFFSET;
        scoreChartX = scoreBoardX + SCORE_WIDTH + VISUALISATION_OFFSET;
        scoreChartY = VISUALISATION_OFFSET;
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
        Game.INSTANCE.image(backGroundView, backGroundX, backGroundY);
    }
    private void drawTopView(){
        topView.beginDraw();

        topView.endDraw();
        Game.INSTANCE.image(topView, topViewX, topViewY);
    }
    private void drawScoreView(){
        scoreBoard.beginDraw();

        scoreBoard.endDraw();
        Game.INSTANCE.image(scoreBoard, scoreBoardX, scoreBoardY);
    }
    private void drawChartView(){
        scoreChart.beginDraw();

        scoreChart.endDraw();
        Game.INSTANCE.image(scoreChart, scoreChartX, scoreChartY);
    }
}
