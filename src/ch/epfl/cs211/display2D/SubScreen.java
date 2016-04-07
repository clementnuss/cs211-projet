package ch.epfl.cs211.display2D;

import processing.core.PGraphics;
import ch.epfl.cs211.tools.Color;

import static ch.epfl.cs211.Game.GAME;
import static ch.epfl.cs211.Game.maxScore;


/**
 * This class implements all the data display features such as the minimap, the current score and
 * the chart plotting the score over time
 *
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public class SubScreen {

    public final static int VISUALISATION_HEIGHT = 120;
    public static int visualisationWidth;
    public final static int VISUALISATION_OFFSET = 20;
    public final static int TOP_HEIGHT = 75;
    public final static int TOP_WIDTH = 75;
    public final static int SCORE_WIDTH = 75;
    public final static int SCORE_HEIGHT = 75;
    public static int chartWidth;
    public final static int CHART_HEIGHT = 100;
    private final static int CHART_ELEM_WIDTH = 5;
    private final static int PLOT_MAX_ELEMENTS = 20;

    private PGraphics topView;
    private PGraphics scoreBoard;
    private PGraphics scoreChart;
    private PGraphics backGroundView;

    private HScrollbar hScrollbar;

    private float backGroundX;
    private float backGroundY;
    private float topViewX;
    private float topViewY;
    private float scoreBoardX;
    private float scoreBoardY;
    private float scoreChartX;
    private float scoreChartY;

    private float elementWidth = CHART_ELEM_WIDTH;

    public SubScreen(){
        hScrollbar = new HScrollbar(0,0, 400, 8);
        updateDimensions();
    }

    public void draw(){
        GAME.noLights();
        drawBackgroundView();
        drawTopView();
        drawScoreView();
        drawChartView();
    };

    private void drawBackgroundView(){
        backGroundView.beginDraw();
        backGroundView.fill(Color.SUBSCREEN_BACKGROUND_COLOR);
        backGroundView.noStroke();
        backGroundView.rect(0,0, visualisationWidth, VISUALISATION_HEIGHT);
        backGroundView.endDraw();
        GAME.image(backGroundView, backGroundX, backGroundY);
    }
    private void drawTopView(){
        topView.beginDraw();
        topView.fill(Color.SUBSCREEN_TOPVIEW_COLOR);
        topView.noStroke();
        topView.rect(0,0, TOP_WIDTH, TOP_HEIGHT);
        topView.endDraw();
        GAME.image(topView, topViewX, topViewY);
    }
    private void drawScoreView(){
        scoreBoard.beginDraw();
        scoreBoard.fill(0);
        scoreBoard.rect(0,0, SCORE_WIDTH, SCORE_HEIGHT);

        scoreBoard.fill(0xFFFF0450);
        scoreBoard.textSize(10);
        scoreBoard.text("Total score: " + GAME.getScore() +
                        "\n Velocity: " + GAME.getMover().getVelocity().mag() +
                        "\nLast score: " + GAME.getLastChange(),
                0, 0, visualisationWidth, VISUALISATION_HEIGHT);

        scoreBoard.endDraw();
        GAME.image(scoreBoard, scoreBoardX, scoreBoardY);
    }
    private void drawChartView(){

        if(hScrollbar.update())
            updateChart();
        hScrollbar.draw();
        scoreChart.beginDraw();
        scoreChart.fill(Color.SUBSCREEN_CHART_COLOR);
        scoreChart.noStroke();
        scoreChart.rect(0,0, chartWidth, CHART_HEIGHT);
        int i = 0;

        for(float scoreAtTime : GAME.getScoresList()){
            drawBar(scoreAtTime, 1 + (elementWidth + 1) * i++);
        }

        scoreChart.endDraw();
        GAME.image(scoreChart, scoreChartX, scoreChartY);
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
    public int getMaxPlottableElements(){ return Math.round(chartWidth / (elementWidth+1));}

    public void updateChart(){
        elementWidth = (1.5f * hScrollbar.getPos() + 0.5f) * CHART_ELEM_WIDTH;
    }

    /**
     * This method should be called whenever the main window has been resized 
     * so we can update the elements positions accordingly
     */
    public void updateDimensions(){
        backGroundX = 0;
        backGroundY = GAME.height - VISUALISATION_HEIGHT;
        visualisationWidth = GAME.width;
        chartWidth = visualisationWidth - TOP_WIDTH - SCORE_WIDTH - 4*VISUALISATION_OFFSET;
        backGroundView = GAME.createGraphics(visualisationWidth, VISUALISATION_HEIGHT);
        topView = GAME.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = GAME.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = GAME.createGraphics(chartWidth, CHART_HEIGHT);

        topViewX = backGroundX + VISUALISATION_OFFSET;
        topViewY = backGroundY + VISUALISATION_OFFSET;
        scoreBoardX = topViewX + TOP_WIDTH + VISUALISATION_OFFSET;
        scoreBoardY = backGroundY + VISUALISATION_OFFSET;
        scoreChartX = scoreBoardX + SCORE_WIDTH + VISUALISATION_OFFSET;
        scoreChartY = backGroundY + VISUALISATION_OFFSET / 3;
        hScrollbar.setPos(scoreChartX, scoreChartY + CHART_HEIGHT + 2);
    }

    public float getScoreChartX(){return scoreChartX;}
    public float getScoreChartY(){return scoreChartY;}
    
}
