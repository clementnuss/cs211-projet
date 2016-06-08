package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;
import ch.epfl.cs211.objects.Plate;
import ch.epfl.cs211.physicsEngine.Mover;
import ch.epfl.cs211.tools.Color;
import ch.epfl.cs211.tools.ValueUtils;
import processing.core.PGraphics;
import processing.core.PVector;

import static ch.epfl.cs211.Game.GAME;
import static ch.epfl.cs211.Game.maxScore;
import static processing.core.PApplet.map;


/**
 * This class implements all the data display features such as the minimap, the current score and
 * the chart plotting the score over time
 * <p>
 * The createGraphics() function makes a surface with the size defined by the parameters. beginDraw() and
 * endDraw() are needed each time you want to edit this surface. Finally, the image() function puts the surface on
 * the display window, at the position defined by its parameters.
 */
public class SubScreen {

    public final static int VISUALISATION_HEIGHT = 120;
    private final static int VISUALISATION_OFFSET = 10;
    private final static int TOP_HEIGHT = 100;
    private final static int TOP_WIDTH = 100;
    private final static int SCORE_WIDTH = 100;
    private final static int SCORE_HEIGHT = 100;
    private final static int CHART_HEIGHT = 100;
    private final static int CHART_ELEM_WIDTH = 5;
    private final static int PLOT_MAX_ELEMENTS = 25; //Should be a number that divides CHART_ELEM_WIDTH
    private static int visualisationWidth;
    private static int chartWidth;
    private final HScrollbar hScrollbar;
    private PGraphics topView;
    private PGraphics scoreBoard;
    private PGraphics scoreChart;
    private PGraphics backGroundView;
    private float backGroundX;
    private float backGroundY;
    private float topViewX;
    private float topViewY;
    private float scoreBoardX;
    private float scoreBoardY;
    private float scoreChartX;
    private float scoreChartY;

    private float elementWidth;

    public SubScreen() {
        hScrollbar = new HScrollbar(0, 0, 400, 8);
        updateDimensions();
    }

    public void draw() {
        GAME.noLights();
        drawBackgroundView();
        drawTopView();
        drawScoreView();
        drawChartView();
    }


    private void drawBackgroundView() {
        backGroundView.beginDraw();
        backGroundView.fill(Color.SUBSCREEN_BACKGROUND_COLOR);
        backGroundView.noStroke();
        backGroundView.rect(0, 0, visualisationWidth, VISUALISATION_HEIGHT);
        backGroundView.endDraw();
        GAME.image(backGroundView, backGroundX, backGroundY);
    }

    private void drawTopView() {
        topView.beginDraw();
        topView.fill(Color.SUBSCREEN_TOPVIEW_COLOR);
        topView.noStroke();
        topView.rect(0, 0, TOP_WIDTH, TOP_HEIGHT);
        float plateBound = GAME.getMover().getBound();

        float sphereRadius = map(Mover.SPHERE_RADIUS, 0, Plate.PLATE_WIDTH, 0, TOP_HEIGHT);
        float cylRadius = map(Mover.CYLINDER_RADIUS, 0, Plate.PLATE_WIDTH, 0, TOP_HEIGHT);

        PVector pos = GAME.getMover().getPosition();
        float px = map(pos.x, -plateBound, plateBound, sphereRadius, TOP_WIDTH - sphereRadius);
        float py = map(pos.z, -plateBound, plateBound, sphereRadius, TOP_HEIGHT - sphereRadius);

        topView.fill(Color.BALL_COLOR);
        topView.ellipse(px, py, 2 * sphereRadius, 2 * sphereRadius);

        topView.fill(Color.CYLINDER_COLOR);
        for (PVector cyl : GAME.getObstacleList()) {
            px = map(cyl.x, -plateBound, plateBound, cylRadius, TOP_WIDTH - cylRadius);
            py = map(cyl.z, -plateBound, plateBound, cylRadius, TOP_HEIGHT - cylRadius);
            topView.ellipse(px, py, 2 * cylRadius, 2 * cylRadius);
        }

        topView.endDraw();
        GAME.image(topView, topViewX, topViewY);
    }

    private void drawScoreView() {
        scoreBoard.beginDraw();
        scoreBoard.fill(0xFFbeeaff);
        scoreBoard.rect(0, 0, SCORE_WIDTH, SCORE_HEIGHT);

        scoreBoard.fill(0xFF000000);
        scoreBoard.textSize(9.5f);
        scoreBoard.text("Total score: \n   -> " + ValueUtils.roundThreeDecimals(GAME.getScore()) +
                        "\nVelocity: \n   -> " + ValueUtils.roundThreeDecimals(GAME.getMover().getVelocity().mag())+
                        "\nLast score: \n    -> " + GAME.getLastChange(),
                7, 7, SCORE_WIDTH - 7, SCORE_HEIGHT - 7);

        scoreBoard.endDraw();
        GAME.image(scoreBoard, scoreBoardX, scoreBoardY);
    }

    private void drawChartView() {

        if (hScrollbar.update())
            updateChart();
        hScrollbar.draw();
        scoreChart.beginDraw();
        scoreChart.fill(Color.SUBSCREEN_CHART_COLOR);
        scoreChart.noStroke();
        scoreChart.rect(0, 0, chartWidth, CHART_HEIGHT);
        int i = 0;

        for (float scoreAtTime : GAME.getScoresList()) {
            drawBar(scoreAtTime, 1 + (elementWidth + 1) * i++);
        }

        scoreChart.endDraw();
        GAME.image(scoreChart, scoreChartX, scoreChartY);
    }

    private void drawBar(float score, float pos) {
        float elementHeight = CHART_HEIGHT / PLOT_MAX_ELEMENTS;
        scoreChart.fill(Color.SUBSCREEN_CHART_ELEMENT_COLOR);
        int nElems = Math.round((score / maxScore) * PLOT_MAX_ELEMENTS);
        for (int y = 1; y <= nElems; y++) {
            scoreChart.rect(pos, CHART_HEIGHT - (y * (elementHeight)), elementWidth, elementHeight - 1);
        }
    }

    public float getChartElementWidth() {
        return elementWidth;
    }

    /**
     * Method used by the main game instance to calculate whether it needs to delete an element from
     * the deque where the scores are stored
     *
     * @return the number of elements that can be plotted using all the width available from the chart
     */
    public int getMaxPlottableElements() {
        return Math.round(chartWidth / (elementWidth + 1));
    }

    private void updateChart() {
        elementWidth = (1.5f * hScrollbar.getPos() + 0.5f) * CHART_ELEM_WIDTH;
    }

    /**
     * This method should be called whenever the main window has been resized
     * so we can update the elements positions accordingly
     */
    public void updateDimensions() {
        backGroundX = 0;
        backGroundY = Game.WINDOW_HEIGHT - VISUALISATION_HEIGHT;
        visualisationWidth = Game.WINDOW_WIDTH;
        chartWidth = visualisationWidth - TOP_WIDTH - SCORE_WIDTH - 4 * VISUALISATION_OFFSET;
        backGroundView = GAME.createGraphics(visualisationWidth, VISUALISATION_HEIGHT);
        topView = GAME.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = GAME.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = GAME.createGraphics(chartWidth, CHART_HEIGHT);

        topViewX = backGroundX + VISUALISATION_OFFSET;
        topViewY = backGroundY + VISUALISATION_OFFSET;
        scoreBoardX = topViewX + TOP_WIDTH + VISUALISATION_OFFSET;
        scoreBoardY = backGroundY + VISUALISATION_OFFSET;
        scoreChartX = scoreBoardX + SCORE_WIDTH + VISUALISATION_OFFSET;
        scoreChartY = backGroundY + 2 * VISUALISATION_OFFSET / 3;
        hScrollbar.setPos(scoreChartX, scoreChartY + CHART_HEIGHT + 2);
        elementWidth = (1.5f * hScrollbar.getPos() + 0.5f) * CHART_ELEM_WIDTH;
    }

    public float getScoreChartX() {
        return scoreChartX;
    }

    public float getScoreChartY() {
        return scoreChartY;
    }


}
