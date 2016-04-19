PImage img;
int max = 0xFFFFFF;
HScrollbar thresholdBar;

void settings() {
  size(800, 600);
}
void setup() {
  img = loadImage("board1.jpg");
  thresholdBar = new HScrollbar(0, 0, 800, 20);
}
void draw() {
  background(color(0, 0, 0));
  thresholdBar.display();
  thresholdBar.update();
  image(displayHue(img), 0, 20);
}

PImage filterThreshold(PImage img, float threshold, boolean inverted) {
  PImage result = createImage(width, height, RGB);
  // create a new, initially transparent, 'result' image
  for (int i = 0; i < img.width * img.height; i++) {
    
    if(inverted)
       result.pixels[i] = (brightness(img.pixels[i]) < threshold) ? 0xFFFFFF : 0x0;
    else
       result.pixels[i] = (brightness(img.pixels[i]) > threshold) ? 0xFFFFFF : 0x0;
  }
  return result;
}

PImage displayHue(PImage img) {
  PImage result = createImage(width, height, RGB);
  for (int i = 0; i < img.width * img.height; i++) {
    result.pixels[i] = (int)color(hue(img.pixels[i]));
  }
  return result;
}