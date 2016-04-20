PImage img;
int max = 0xFFFFFF;
HScrollbar thresholdBar1;
HScrollbar thresholdBar2;
PImage displayedImage;

float oldBarValue1;
float oldBarValue2;

float[][] hKernel =
{ { 0,  1, 0  },
{ 0,  0, 0 },
{ 0, -1, 0 } };

float[][] vKernel =
{ { 0,  0,  0  },
{ 1,  0, -1 },
{ 0,  0,  0  } };

int[][] gaussianKernel =
{ {9,12,9},
  {12,15,12},
  {9,12,9},
};

int[][] kernel1 = 
{ {0,0,0},
  {0,2,0},
  {0,0,0},
};

int[][] kernel2 = 
{ {0,1,0},
  {1,0,1},
  {0,1,0},
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
      //Possibility to switch filter to different Kernels (1,2, and Gaussian defined above)
      displayedImage = convolve(img, kernel2.length, kernel2);
      image(displayedImage, 0, 0);
  thresholdBar1.display();
  thresholdBar1.update();
  thresholdBar2.display();
  thresholdBar2.update();
}

PImage filterThreshold(PImage img, float threshold, boolean inverted) {
  PImage result = createImage(width, height, RGB);
  // create a new, initially transparent, 'result' image
  loadPixels();
  for (int i = 0; i < img.width * img.height; i++) {
    
    if(inverted)
       result.pixels[i] = (brightness(img.pixels[i]) < threshold) ? 0xFFFFFF : 0x0;
    else
       result.pixels[i] = (brightness(img.pixels[i]) > threshold) ? 0xFFFFFF : 0x0;
  }
  updatePixels();
  return result;
}

PImage hueAsGrayLevel(PImage img) {
  PImage result = createImage(width, height, RGB);
  loadPixels();
  for (int i = 0; i < img.width * img.height; i++) {
    result.pixels[i] = color(round(hue(img.pixels[i])));
  }
  updatePixels();
  return result;
}

PImage selectedHue(PImage img, float threshold1, float threshold2){
  PImage result = createImage(width, height, RGB);
  int originalColor;
  float originalColorHue;
  // create a new, initially transparent, 'result' image
  loadPixels();
  for (int i = 0; i < img.width * img.height; i++) { 
       originalColor = img.pixels[i];
       originalColorHue = hue(originalColor);
       result.pixels[i] = (threshold1 <= originalColorHue && originalColorHue <= threshold2) ? originalColor : 0x0;
  }
  updatePixels();
  return result;
}

public PImage convolve(PImage img, int N, int[][] matrix) {
  PImage result = createImage(width, height, ALPHA);
  float sum;
  float weight = 4.0f;
  int halfN = N/2;

  // kernel size N = 3
  //
  // for each (x,y) pixel in the image:
  //     - multiply intensities for pixels in the range
  //       (x - N/2, y - N/2) to (x + N/2, y + N/2) by the
  //       corresponding weights in the kernel matrix
  //     - sum all these intensities and divide it by the weight
  //     - set result.pixels[y * img.width + x] to this value
  loadPixels();
  for (int y=0; y < img.height; y++) {
    for (int x = 0; x < img.width; x++) {
      sum = 0;
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < N; i++) {
          int xp = x - halfN + i;
          int yp = y - halfN + j;
          if(xp >= 0 && yp >= 0 && xp < img.width && yp < img.height)
            sum += brightness(img.pixels[(yp * img.width) + xp]) * matrix[j][i];
        }
      }
      sum /= weight;
      result.pixels[(y * result.width) + x] = color(sum);
    }
  }
  updatePixels();
  return result;
} 

public PImage sobel(PImage img, int N) {
  float sum;
  float sum_h;
  float sum_v;
 
  float weight = 3.0f;
  int halfN = N/2;
  PImage result = createImage(img.width, img.height, ALPHA);
  
  // clear the image
  loadPixels();
  for (int i = 0; i < img.width * img.height; i++) {
    result.pixels[i] = color(0);
  }
  updatePixels();
  
  float max=0;
  /*
   I went for a two dimensional buffer because it's more representative of the pixels visual repartition on the screen
   Feel free to adapt if it's not relevant
  */
  float[][] buffer = new float[img.width][img.height];
  
  
  for (int y=0; y < img.height; y++) {
    for (int x = 0; x < img.width; x++) {
      sum = 0;
      sum_h = 0;
      sum_v = 0;
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < N; i++) {
          int xp = x - halfN + i;
          int yp = y - halfN + j;
          if(xp >= 0 && yp >= 0 && xp < img.width && yp < img.height)
            sum_h += brightness(img.pixels[(yp * img.width) + xp]) * hKernel[j][i];
            sum_v += brightness(img.pixels[(yp * img.width) + xp]) * vKernel[j][i];
        }
      }
      sum=sqrt(pow(sum_h, 2) + pow(sum_v, 2));
      if(sum > max)
        max = sum;
      buffer[y][x] = sum;
    }
  }
  
  loadPixels();
  for (int y = 2; y < img.height - 2; y++) {
    // Skip top and bottom edges
    for (int x = 2; x < img.width - 2; x++) {
      // Skip left and right
      if (buffer[y][x] > (int)(max * weight)) {
      // 30% of the max
      result.pixels[y * img.width + x] = color(255);
      } else {
      result.pixels[y * img.width + x] = color(0);
      }
    }
  }
  
  updatePixels();  
  return result;
}