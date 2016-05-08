PImage img;
int max = 0xFFFFFF;
HScrollbar thresholdBar1;
HScrollbar thresholdBar2;
PImage displayedImage;

float oldBarValue1;
float oldBarValue2;

int[][] kernel1 = 
{ {0,0,0},
  {0,2,0},
  {0,0,0},
};



void settings() {
  size(800, 600);
}
void setup() {
  img = loadImage("board1.jpg");
  thresholdBar1 = new HScrollbar(0, 0, 800, 20);
  thresholdBar2 = new HScrollbar(0, 20, 800, 20);
  oldBarValue1 = 0;
  oldBarValue2 = 0;
}
void draw() {
  background(color(0, 0, 0));
  if(oldBarValue1 != thresholdBar1.getPos() || oldBarValue2 != thresholdBar2.getPos())
      displayedImage = convolve(img, 5, thresholdBar1.getPos());
  image(displayedImage,0,0);
  thresholdBar1.display();
  thresholdBar1.update();
  thresholdBar2.display();
  thresholdBar2.update();
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

PImage hueAsGrayLevel(PImage img) {
  PImage result = createImage(width, height, RGB);
  for (int i = 0; i < img.width * img.height; i++) {
    result.pixels[i] = color(round(hue(img.pixels[i])));
  }
  return result;
}

PImage selectedHue(PImage img, float threshold1, float threshold2){
  PImage result = createImage(width, height, RGB);
  int originalColor;
  float originalColorHue;
  // create a new, initially transparent, 'result' image
  for (int i = 0; i < img.width * img.height; i++) { 
       originalColor = img.pixels[i];
       originalColorHue = hue(originalColor);
       result.pixels[i] = (threshold1 <= originalColorHue && originalColorHue <= threshold2) ? originalColor : 0x0;
  }
  return result;
}

public PImage convolve(PImage img, int N, float coeff) {
  PImage result = createImage(width, height, ALPHA);
  float sum;
  float weight = 2.2 * coeff+0.3;
  int halfN = N/2;
  
  float[][] kernel2 = 
{ {0,0,0,0,0},
  {0,0,0,0,0},
  {0.3*coeff,0.8*coeff,0.3,0.8*coeff,0.3*coeff},
  {0,0,0,0,0},
  {0,0,0,0,0}
};

  // kernel size N = 3
  //
  // for each (x,y) pixel in the image:
  //     - multiply intensities for pixels in the range
  //       (x - N/2, y - N/2) to (x + N/2, y + N/2) by the
  //       corresponding weights in the kernel matrix
  //     - sum all these intensities and divide it by the weight
  //     - set result.pixels[y * img.width + x] to this value

  for (int y=0; y < img.height; y++) {
    for (int x = 0; x < img.width; x++) {
      sum = 0;
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < N; i++) {
          int xp = x - halfN + i;
          int yp = y - halfN + j;
          if(xp >= 0 && yp >= 0 && xp < img.width && yp < img.height)
            sum += brightness(img.pixels[(yp * img.width) + xp]) * kernel2[j][i];
        }
      }
      sum /= weight;
      result.pixels[(y * result.width) + x] = color(sum);
    }
  }
  return result;
} 