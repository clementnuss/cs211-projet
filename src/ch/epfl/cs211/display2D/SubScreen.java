package ch.epfl.cs211.display2D;

import processing.core.PGraphics;
import ch.epfl.cs211.tools.Color;

import static ch.epfl.cs211.Game.INSTANCE;
import static ch.epfl.cs211.Game.maxScore;


/**
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public class SubScreen {

    public final static int VISUALISATION_HEIGHT = 120;
    public final static int VISUALISATION_WIDTH = INSTANCE.width;
    public final static int VISUALISATION_OFFSET = 20;
    public final static int TOP_HEIGHT = 75;
    public final static int TOP_WIDTH = 75;
    public final static int SCORE_WIDTH = 75;
    public final static int SCORE_HEIGHT = 75;
    public final static int CHART_WIDTH = VISUALISATION_WIDTH - TOP_WIDTH - SCORE_WIDTH - 4*VISUALISATION_OFFSET;
    public final static int CHART_HEIGHT = 100;
    private final static int CHART_ELEM_WIDTH = 6;
    private final static int PLOT_MAX_ELEMENTS = 20;

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

    private float elementWidth = 10;
    private float elementHeight = CHART_HEIGHT / PLOT_MAX_ELEMENTS -1;

    public SubScreen(float backGroundX, float backGroundY){
        this.backGroundX = backGroundX;
        this.backGroundY = backGroundY;

        backGroundView = INSTANCE.createGraphics(VISUALISATION_WIDTH, VISUALISATION_HEIGHT);
        topView = INSTANCE.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = INSTANCE.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = INSTANCE.createGraphics(CHART_WIDTH, CHART_HEIGHT);

        topViewX = backGroundX + VISUALISATION_OFFSET;
        topViewY = backGroundY + VISUALISATION_OFFSET;
        scoreBoardX = topViewX + TOP_WIDTH + VISUALISATION_OFFSET;
        scoreBoardY = backGroundY + VISUALISATION_OFFSET;
        scoreChartX = scoreBoardX + SCORE_WIDTH + VISUALISATION_OFFSET;
        scoreChartY = backGroundY + VISUALISATION_OFFSET / 3;
    }

    public void draw(){
        INSTANCE.noLights();
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
        INSTANCE.image(backGroundView, backGroundX, backGroundY);
    }
    private void drawTopView(){
        topView.beginDraw();
        topView.fill(Color.SUBSCREEN_TOPVIEW_COLOR);
        topView.noStroke();
        topView.rect(0,0, TOP_WIDTH, TOP_HEIGHT);
        topView.endDraw();
        INSTANCE.image(topView, topViewX, topViewY);
    }
    private void drawScoreView(){
        scoreBoard.beginDraw();
        scoreBoard.fill(0);
        scoreBoard.rect(0,0, SCORE_WIDTH, SCORE_HEIGHT);

        scoreBoard.fill(0xFFFF0450);
        scoreBoard.textSize(10);
        scoreBoard.text("Total score: " + INSTANCE.getScore() +
                        "\n Velocity: " + INSTANCE.getMover().getVelocity().mag() +
                        "\nLast score: " + INSTANCE.getLastChange(),
                0, 0, VISUALISATION_WIDTH, VISUALISATION_HEIGHT);

        scoreBoard.endDraw();
        INSTANCE.image(scoreBoard, scoreBoardX, scoreBoardY);
    }
    private void drawChartView(){
        scoreChart.beginDraw();
        scoreChart.fill(Color.SUBSCREEN_CHART_COLOR);
        scoreChart.noStroke();
        scoreChart.rect(0,0, CHART_WIDTH, CHART_HEIGHT);
        int i = 0;

        for(float scoreAtTime : INSTANCE.getScoresList()){
            drawBar(scoreAtTime, 1 + (elementWidth + 1) * i++);
        }

        scoreChart.endDraw();
        INSTANCE.image(scoreChart, scoreChartX, scoreChartY);
    }

    private void drawBar(float score, float pos){
        float elementHeight = CHART_HEIGHT / PLOT_MAX_ELEMENTS;
        scoreChart.fill(Color.SUBSCREEN_CHART_ELEMENT_COLOR);
        int nElems = Math.round((score / maxScore) * PLOT_MAX_ELEMENTS);
        for(int y = 1; y <= nElems; y++){
            scoreChart.rect(pos, CHART_HEIGHT - (y * (elementHeight)), elementWidth, elementHeight - 1);
        }
    }

    public float getChartElementWidth(){
        return elementWidth;
    }

    /**
     * Method used by the main game instance to calculate whether it needs to delete an element from
     * the deque where the scores are stored
     * @return the number of elements that can be plotted using all the width available from the chart
     */
    public int getMaxPlottableElements(){ return Math.round(CHART_WIDTH / (elementWidth+1));}

    public void updateChart(float newElemSizeRatio){
        elementWidth = (1.5f * newElemSizeRatio + 0.5f) * CHART_ELEM_WIDTH;
    }

    public float getScoreChartX(){return scoreChartX;}
    public float getScoreChartY(){return scoreChartY;}
}
