PImage img;
void settings() {
size(800, 600);
}
void setup() {
img = loadImage("board1.jpg");
noLoop();
// no interactive behaviour: draw() will be called only once.
}
void draw() {
image(img, 0, 0);
}