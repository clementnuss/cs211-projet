
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
    if (Game.GAME.mousePressed && mouseOver) {
      locked = true;
    }
    if (!Game.GAME.mousePressed) {
      locked = false;
    }
    if (locked) {
      newSliderPosition = constrain(Game.GAME.mouseX - barHeight/2, sliderPositionMin, sliderPositionMax);
    }
    if (Game.GAME.abs(newSliderPosition - sliderPosition) > 1) {
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
    return Game.GAME.min(Game.GAME.max(val, minVal), maxVal);
  }

  /**
   * @brief Gets whether the mouse is hovering the scrollbar
   *
   * @return Whether the mouse is hovering the scrollbar
   */
  public boolean isMouseOver() {
    return (Game.GAME.mouseX > xPosition && Game.GAME.mouseX < xPosition+barWidth &&
            Game.GAME.mouseY > yPosition && Game.GAME.mouseY < yPosition+barHeight);
  }

  /**
   * @brief Draws the scrollbar in its current state
   */ 
  public void draw() {
      Game.GAME.noStroke();
      Game.GAME.fill(204);
      Game.GAME.rect(xPosition, yPosition, barWidth, barHeight);
    if (mouseOver || locked) {
        Game.GAME.fill(0, 0, 0);
    }
    else {
        Game.GAME.fill(102, 102, 102);
    }
      Game.GAME.rect(sliderPosition, yPosition, barHeight, barHeight);
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