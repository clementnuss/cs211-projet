PImage img;
int max = 0xFFFFFF;
HScrollbar thresholdBar1;
HScrollbar thresholdBar2;
PImage toDisplay;

float oldBarValue1;
float oldBarValue2;

final float[]sobelKernel = {1f,0f,-1f};
final int SOBEL_LENGTH = sobelKernel.length;
final float SOBEL_WEIGHT = 1f;
final float SOBEL_PERCENTAGE = 0.3f;

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
  {1,0,1}, //<>//
  {0,1,0},
};

void settings() {
  size(800, 600, P2D);
}
void setup() {
  img = loadImage("board2.jpg");
  thresholdBar1 = new HScrollbar(0, 0, width, 20);
  thresholdBar2 = new HScrollbar(0, 25, width, 20);
  oldBarValue1 = 0;
  oldBarValue2 = 0;
}
void draw() {
  if(oldBarValue1 != thresholdBar1.getPos() || oldBarValue2 != thresholdBar2.getPos()){
    background(0);
      oldBarValue1 = thresholdBar1.getPos();
      oldBarValue2 = thresholdBar2.getPos();
      
      //IMAGE TREATMENT PIPELINE
      toDisplay = brightnessThreshold(saturationThreshold(hueThreshold(img.copy(),100,140), 80, 255),1,false);
      //MAYBE BLURR THIS IMAGE ?
     image(sobel(toDisplay),0,0);
  }
  
  thresholdBar1.display();
  thresholdBar1.update();
  thresholdBar2.display();
  thresholdBar2.update();
}

//A good value to extract the board would be 87 to 255
PImage saturationThreshold(PImage img, float t1, float t2){
  img.loadPixels();
  System.out.println("[INFO:] SATURATION threshold selection is " +t1+ "-" +t2+" MIN-MAX");
  
  for (int i = 0; i < img.width * img.height; i++) {
     int originalColor = img.pixels[i];
     float sat = saturation(originalColor);
     img.pixels[i] = (t1 <= sat && sat <= t2) ? originalColor : 0x0;
  }
  img.updatePixels();
  return img;
}


//Takes an image, a threshold between 0 and 1 and it will set all pixels above the threshold to WHITE and the others to BLACK
PImage brightnessThreshold(PImage img, float t, boolean inverted) {
  img.loadPixels();
  for (int i = 0; i < img.width * img.height; i++) {
    
    if(inverted)
       img.pixels[i] = (brightness(img.pixels[i]) < t) ? 0xFFFFFFFF : 0x0;
    else
       img.pixels[i] = (brightness(img.pixels[i]) > t) ? 0xFFFFFFFF : 0x0;
  }
  img.updatePixels();
  return img;
}

PImage hueAsGrayLevel(PImage img) {
  for (int i = 0; i < img.width * img.height; i++) {
    img.pixels[i] = color(hue(img.pixels[i]));
  }
  return img;
}

//A good value to extract the board would be 115 to 132
PImage hueThreshold(PImage img, float t1, float t2){
  img.loadPixels();
  int originalColor;
  float originalColorHue;
  
  System.out.println("[INFO:] HUE threshold selection is " +t1+ "-" +t2+" MIN-MAX");
  
  for (int i = 0; i < img.width * img.height; i++) {
       originalColor = img.pixels[i];
       originalColorHue = hue(originalColor);
       img.pixels[i] = (t1 <= originalColorHue && originalColorHue <= t2) ? originalColor : 0x0;
  }
  img.updatePixels();
  return img;
}

private float computeWeight(int[][] m){
 int s= 0;
 for(int i = 0; i < m.length; i++){
   for(int j =0; j < m[i].length; j++){
      s += m[i][j];
   }
 }
 return s;
}

public PImage convolve(PImage img, int[][] matrix) {
  PImage result = createImage(width, height, ALPHA);
  float sum;
  float weight = computeWeight(matrix)*2;
  int N = matrix.length;
  int halfN = N/2;
  
  for (int y=halfN; y < img.height - halfN; y++) {
    for (int x = halfN; x < img.width - halfN; x++) {
      sum = 0;
      for (int j = 0; j < N; j++) {
        for (int i = 0; i < N; i++) {
          int xp = x - halfN + i;
          int yp = y - halfN + j;
          sum += brightness(img.pixels[(yp * img.width) + xp]) * matrix[j][i];
        }
      }
      sum /= weight;
      result.pixels[(y * result.width) + x] = color(sum);
    }
  }
  return result;
}

public PImage sobel(PImage img) {
  float sum_h;
  float sum_v;
  float[][] buffer = new float[img.height][img.width];
  PImage result = createImage(img.width, img.height, ALPHA);

  /* Convolve operation is separeted in two rectangular matrices to save computations, namely 2*n instead of n^2 per image pixel  */
  //Vertical convolution  
  for (int y=1; y < img.height-1; y++) {
    for (int x = 1; x < img.width-1; x++) {
      sum_h = 0; sum_v = 0;
      
     //Horiontal convolution
      int xp = y * img.width + x;
      sum_h += sobelKernel[0]*(img.pixels[xp-1] & 0x1);
      sum_h += sobelKernel[2]*(img.pixels[xp+1] & 0x1);
      
      //Vertical convolution      
      sum_v += sobelKernel[0]*(img.pixels[(y-1) * img.width + x] & 0x1);
      sum_v += sobelKernel[2]*(img.pixels[(y+1) * img.width + x] & 0x1);
      
      //Compute de gradient
      float sum=sqrt(pow(sum_h, 2) + pow(sum_v, 2));
      buffer[y][x] = sum;
    }
  }

  for (int y = 2; y < img.height - 2; y++) { //<>//
    for (int x = 2; x < img.width - 2; x++) {
      if (buffer[y][x] > (SOBEL_PERCENTAGE)) 
        result.pixels[y * img.width + x] = 0xFFFFFFFF;
      else 
        result.pixels[y * img.width + x] = 0xFF000000;
    }
  }
  return result;
}