PImage img;
PImage result;
float THRESHOLD = 128;
boolean INVERTED = true;

void settings() {
  size(800, 600);
}
void setup() {
  img = loadImage("board1.jpg");
  
  final float THRESHOLD = 100;
  result = createImage(img.width, img.height, RGB);
  
  // create a new, initially transparent, 'result' image
  for (int i = 0; i < img.width * img.height; i++) {
    if(INVERTED)
      result.pixels[i] = (brightness(img.pixels[i]) < THRESHOLD) ? 0xFFFFFF : 0x0;
      else
      result.pixels[i] = (brightness(img.pixels[i]) > THRESHOLD) ? 0xFFFFFF : 0x0;
  }
  noLoop();
  // no interactive behaviour: draw() will be called only once.
}
void draw() {
  image(result, 0, 0);
}  