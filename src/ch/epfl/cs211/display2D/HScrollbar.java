package ch.epfl.cs211.display2D;

import ch.epfl.cs211.Game;

import static ch.epfl.cs211.Game.INSTANCE;

public class HScrollbar {
  float barWidth;  //Bar's width in pixels
  float barHeight; //Bar's height in pixels
  float xPosition;  //Bar's x position in pixels
  float yPosition;  //Bar's y position in pixels
  
  float sliderPosition, newSliderPosition;    //Position of slider
  float sliderPositionMin, sliderPositionMax; //Max and min values of slider
  
  boolean mouseOver;  //Is the mouse over the slider?
  boolean locked;     //Is the mouse clicking and dragging the slider now?

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
    if (isMouseOver()) {
      mouseOver = true;
    }
    else {
      mouseOver = false;
    }
    if (INSTANCE.mousePressed && mouseOver) {
      locked = true;
    }
    if (!INSTANCE.mousePressed) {
      locked = false;
    }
    if (locked) {
      newSliderPosition = constrain(INSTANCE.mouseX - barHeight/2, sliderPositionMin, sliderPositionMax);
    }
    if (INSTANCE.abs(newSliderPosition - sliderPosition) > 1) {
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
  public float constrain(float val, float minVal, float maxVal) {
    return INSTANCE.min(INSTANCE.max(val, minVal), maxVal);
  }

  /**
   * @brief Gets whether the mouse is hovering the scrollbar
   *
   * @return Whether the mouse is hovering the scrollbar
   */
  public boolean isMouseOver() {
    if (INSTANCE.mouseX > xPosition && INSTANCE.mouseX < xPosition+barWidth &&
            INSTANCE.mouseY > yPosition && INSTANCE.mouseY < yPosition+barHeight) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * @brief Draws the scrollbar in its current state
   */ 
  public void display() {
      INSTANCE.noStroke();
      INSTANCE.fill(204);
      INSTANCE.rect(xPosition, yPosition, barWidth, barHeight);
    if (mouseOver || locked) {
        INSTANCE.fill(0, 0, 0);
    }
    else {
        INSTANCE.fill(102, 102, 102);
    }
      INSTANCE.rect(sliderPosition, yPosition, barHeight, barHeight);
  }

  /**
   * @brief Gets the slider position
   * 
   * @return The slider position in the interval [0,1] corresponding to [leftmost position, rightmost position]
   */
  public float getPos() {
    return (sliderPosition - xPosition)/(barWidth - barHeight);
  }
}
