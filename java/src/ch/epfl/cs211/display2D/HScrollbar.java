package ch.epfl.cs211.display2D;

import processing.core.PApplet;

import static ch.epfl.cs211.Game.GAME;

class HScrollbar {
  private final float barWidth;  //Bar's width in pixels
  private final float barHeight; //Bar's height in pixels
  private float xPosition;  //Bar's x position in pixels
  private float yPosition;  //Bar's y position in pixels

  private float sliderPosition;
  private float newSliderPosition;    //Position of slider
  private float sliderPositionMin;
  private float sliderPositionMax; //Max and min values of slider

  private boolean mouseOver;  //Is the mouse over the slider?
  private boolean locked;     //Is the mouse clicking and dragging the slider now?

  /**
   * @brief Creates a new horizontal scrollbar
   * 
   * @param x The x position of the top left corner of the bar in pixels
   * @param y The y position of the top left corner of the bar in pixels
   * @param w The width of the bar in pixels
   * @param h The height of the bar in pixels
   */
  public HScrollbar (float x, float y, float w, float h) {
    barWidth = w;
    barHeight = h;
    xPosition = x;
    yPosition = y;
    
    sliderPosition = xPosition + barWidth/2 - barHeight/2;
    newSliderPosition = sliderPosition;
    
    sliderPositionMin = xPosition;
    sliderPositionMax = xPosition + barWidth - barHeight;
  }

  /**
   * @brief Updates the state of the scrollbar according to the mouse movement
   * @return A boolean indicating if the state of the scrollbar has changed
   */
  public boolean update() {
    mouseOver = isMouseOver();
    if (GAME.mousePressed && mouseOver) {
      locked = true;
    }
    if (!GAME.mousePressed) {
      locked = false;
    }
    if (locked) {
      newSliderPosition = constrain(GAME.mouseX - barHeight/2, sliderPositionMin, sliderPositionMax);
    }
    if (PApplet.abs(newSliderPosition - sliderPosition) > 1) {
      sliderPosition = sliderPosition + (newSliderPosition - sliderPosition);
        return true;
    }
      return false;
  }

  /**
   * @brief Clamps the value into the interval
   * 
   * @param val The value to be clamped
   * @param minVal Smallest value possible
   * @param maxVal Largest value possible
   * 
   * @return val clamped into the interval [minVal, maxVal]
   */
  private float constrain(float val, float minVal, float maxVal) {
    return PApplet.min(PApplet.max(val, minVal), maxVal);
  }

  /**
   * @brief Gets whether the mouse is hovering the scrollbar
   *
   * @return Whether the mouse is hovering the scrollbar
   */
  private boolean isMouseOver() {
    return (GAME.mouseX > xPosition && GAME.mouseX < xPosition+barWidth &&
            GAME.mouseY > yPosition && GAME.mouseY < yPosition+barHeight);
  }

  /**
   * @brief Draws the scrollbar in its current state
   */ 
  public void draw() {
      GAME.noStroke();
      GAME.fill(204);
      GAME.rect(xPosition, yPosition, barWidth, barHeight);
    if (mouseOver || locked) {
        GAME.fill(0, 0, 0);
    }
    else {
        GAME.fill(102, 102, 102);
    }
      GAME.rect(sliderPosition, yPosition, barHeight, barHeight);
  }

  /**
   * @brief Gets the slider position
   * 
   * @return The slider position in the interval [0,1] corresponding to [leftmost position, rightmost position]
   */
  public float getPos() {
    return (sliderPosition - xPosition)/(barWidth - barHeight);
  }

  public void setPos(float newXPos, float newYPos){
      float oldSliderPosition = getPos();
      xPosition = newXPos;
      yPosition = newYPos;
      sliderPosition = xPosition + (oldSliderPosition * (barWidth - barHeight));
      sliderPositionMin = xPosition;
      sliderPositionMax = xPosition + barWidth - barHeight;
      newSliderPosition = sliderPosition;
  }
}
