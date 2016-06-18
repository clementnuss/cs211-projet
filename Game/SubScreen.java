
import processing.core.PGraphics;
import processing.core.PVector;

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
    public static int visualisationWidth;
    public final static int VISUALISATION_OFFSET = 10;
    public final static int TOP_HEIGHT = 100;
    public final static int TOP_WIDTH = 100;
    public final static int SCORE_WIDTH = 100;
    public final static int SCORE_HEIGHT = 100;
    public static int chartWidth;
    public final static int CHART_HEIGHT = 100;
    private final static int CHART_ELEM_WIDTH = 5;
    private final static int PLOT_MAX_ELEMENTS = 25; //Should be a number that divides CHART_ELEM_WIDTH

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

    private float elementWidth;

    public SubScreen() {
        hScrollbar = new HScrollbar(0, 0, 400, 8);
        updateDimensions();
    }

    public void draw() {
        Game.GAME.noLights();
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
        Game.GAME.image(backGroundView, backGroundX, backGroundY);
    }

    private void drawTopView() {
        topView.beginDraw();
        topView.fill(Color.SUBSCREEN_TOPVIEW_COLOR);
        topView.noStroke();
        topView.rect(0, 0, TOP_WIDTH, TOP_HEIGHT);
        float plateBound = Game.GAME.getMover().getBound();

        float sphereRadius = map(Mover.SPHERE_RADIUS, 0, Plate.PLATE_WIDTH, 0, TOP_HEIGHT);
        float cylRadius = map(Mover.CYLINDER_RADIUS, 0, Plate.PLATE_WIDTH, 0, TOP_HEIGHT);

        PVector pos = Game.GAME.getMover().getPosition();
        float px = map(pos.x, -plateBound, plateBound, sphereRadius, TOP_WIDTH - sphereRadius);
        float py = map(pos.z, -plateBound, plateBound, sphereRadius, TOP_HEIGHT - sphereRadius);

        topView.fill(Color.BALL_COLOR);
        topView.ellipse(px, py, 2 * sphereRadius, 2 * sphereRadius);

        topView.fill(Color.CYLINDER_COLOR);
        for (PVector cyl : Game.GAME.getObstacleList()) {
            px = map(cyl.x, -plateBound, plateBound, cylRadius, TOP_WIDTH - cylRadius);
            py = map(cyl.z, -plateBound, plateBound, cylRadius, TOP_HEIGHT - cylRadius);
            topView.ellipse(px, py, 2 * cylRadius, 2 * cylRadius);
        }

        topView.endDraw();
        Game.GAME.image(topView, topViewX, topViewY);
    }

    private void drawScoreView() {
        scoreBoard.beginDraw();
        scoreBoard.fill(0xFFbeeaff);
        scoreBoard.rect(0, 0, SCORE_WIDTH, SCORE_HEIGHT);

        scoreBoard.fill(0xFF000000);
        scoreBoard.textSize(9.5f);
        scoreBoard.text("Total score: \n   -> " + ValueUtils.roundThreeDecimals(Game.GAME.getScore()) +
                        "\nVelocity: \n   -> " + ValueUtils.roundThreeDecimals(Game.GAME.getMover().getVelocity().mag())+
                        "\nLast score: \n    -> " + Game.GAME.getLastChange(),
                7, 7, SCORE_WIDTH - 7, SCORE_HEIGHT - 7);

        scoreBoard.endDraw();
        Game.GAME.image(scoreBoard, scoreBoardX, scoreBoardY);
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

        for (float scoreAtTime : Game.GAME.scoresList) {
            drawBar(scoreAtTime, 1 + (elementWidth + 1) * i++);
        }

        scoreChart.endDraw();
        Game.GAME.image(scoreChart, scoreChartX, scoreChartY);
    }

    private void drawBar(float score, float pos) {
        float elementHeight = CHART_HEIGHT / PLOT_MAX_ELEMENTS;
        scoreChart.fill(Color.SUBSCREEN_CHART_ELEMENT_COLOR);
        int nElems = Math.round((score / Game.GAME.maxScore) * PLOT_MAX_ELEMENTS);
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

    public void updateChart() {
        elementWidth = (1.5f * hScrollbar.getPos() + 0.5f) * CHART_ELEM_WIDTH;
    }

    /**
     * This method should be called whenever the main window has been resized
     * so we can update the elements positions accordingly
     */
    public void updateDimensions() {
        backGroundX = 0;
        backGroundY = Game.GAME.height - VISUALISATION_HEIGHT;
        visualisationWidth = Game.GAME.width;
        chartWidth = visualisationWidth - TOP_WIDTH - SCORE_WIDTH - 4 * VISUALISATION_OFFSET;
        backGroundView = Game.GAME.createGraphics(visualisationWidth, VISUALISATION_HEIGHT);
        topView = Game.GAME.createGraphics(TOP_WIDTH, TOP_HEIGHT);
        scoreBoard = Game.GAME.createGraphics(SCORE_WIDTH, SCORE_HEIGHT);
        scoreChart = Game.GAME.createGraphics(chartWidth, CHART_HEIGHT);

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