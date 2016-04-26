package ProcessingFiles.imageFilter;

import static ProcessingFiles.imageFilter.imageFilter.INST;

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
     * @param x The x position of the top left corner of the bar in pixels
     * @param y The y position of the top left corner of the bar in pixels
     * @param w The width of the bar in pixels
     * @param h The height of the bar in pixels
     * @brief Creates a new horizontal scrollbar
     */
    HScrollbar(float x, float y, float w, float h) {
        barWidth = w;
        barHeight = h;
        xPosition = x;
        yPosition = y;

        sliderPosition = xPosition + barWidth / 2 - barHeight / 2;
        newSliderPosition = sliderPosition;

        sliderPositionMin = xPosition;
        sliderPositionMax = xPosition + barWidth - barHeight;
    }

    /**
     * @brief Updates the state of the scrollbar according to the mouse movement
     */
    void update() {
        if (isMouseOver()) {
            mouseOver = true;
        } else {
            mouseOver = false;
        }
        if (INST.mousePressed && mouseOver) {
            locked = true;
        }
        if (!INST.mousePressed) {
            locked = false;
        }
        if (locked) {
            newSliderPosition = constrain(INST.mouseX - barHeight / 2, sliderPositionMin, sliderPositionMax);
        }
        if (INST.abs(newSliderPosition - sliderPosition) > 1) {
            sliderPosition = sliderPosition + (newSliderPosition - sliderPosition);
        }
    }

    /**
     * @param val    The value to be clamped
     * @param minVal Smallest value possible
     * @param maxVal Largest value possible
     * @return val clamped into the interval [minVal, maxVal]
     * @brief Clamps the value into the interval
     */
    float constrain(float val, float minVal, float maxVal) {
        return INST.min(Math.max(val, minVal), maxVal);
    }

    /**
     * @return Whether the mouse is hovering the scrollbar
     * @brief Gets whether the mouse is hovering the scrollbar
     */
    boolean isMouseOver() {
        if (INST.mouseX > xPosition && INST.mouseX < xPosition + barWidth &&
                INST.mouseY > yPosition && INST.mouseY < yPosition + barHeight) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @brief Draws the scrollbar in its current state
     */
    void display() {
        INST.noStroke();
        INST.fill(204);
        INST.rect(xPosition, yPosition, barWidth, barHeight);
        if (mouseOver || locked) {
            INST.fill(0, 0, 0);
        } else {
            INST.fill(102, 102, 102);
        }
        INST.rect(sliderPosition, yPosition, barHeight, barHeight);
    }

    /**
     * @return The slider position in the interval [0,1] corresponding to [leftmost position, rightmost position]
     * @brief Gets the slider position
     */
    float getPos() {
        return (sliderPosition - xPosition) / (barWidth - barHeight);
    }
}