#include <iostream>
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

using namespace cv;

struct HSVbounds {
    int hMin = 0, sMin = 0, vMin = 0,
            hMax = 180, sMax = 255, vMax = 255;
};

typedef void (*TrackbarCallback)(int pos, void* userdata);

int threshold_value = 0;
int threshold_type = 3;;
int const max_value = 255;
int const max_type = 4;
int const max_BINARY_value = 255;

Mat src, src_gray, src_hsv, hsv_filtered, dst;
HSVbounds bounds;

const char *threshold_window = "Threshold";
const char *hsv_window = "HSV";
const char *hsv_trackbars = "HSV Trackbars";


const char *trackbar_type = "Type: \n 0: Binary \n 1: Binary Inverted \n 2: Truncate \n 3: To Zero \n 4: To Zero Inverted";
const char *trackbar_value = "Value";

/// Function headers
void threshold_show(int, void *);

void hsv_show(int, void *);

void createTrackbars(HSVbounds &bounds, std::string winName, TrackbarCallback callback);

/**
 * @function main
 */
int main(int argc, char **argv) {

    if (argc < 2) {
        std::cerr  << "You need to call this program with the path of the image to display !";
        return 0xbeef;
    }
    /// Load an image
    src = imread(argv[1], 1);

    /// Convert the image to Gray
    cvtColor(src, src_gray, CV_BGR2GRAY);

    /// Create a window to display results
    namedWindow(threshold_window, CV_WINDOW_AUTOSIZE);

    /// Create Trackbar to choose type of Threshold
    createTrackbar(trackbar_type, threshold_window, &threshold_type, max_type, threshold_show);

    createTrackbar(trackbar_value, threshold_window, &threshold_value, max_value, threshold_show);

    cvtColor(src, src_hsv, COLOR_BGR2HSV);

    createTrackbars(bounds, hsv_trackbars, hsv_show);

    /// Call the function to initialize
    threshold_show(0, 0);

    hsv_show(0,0);

    /// Wait until user finishes program
    while (true) {
        int c;
        c = waitKey(20);
        if ((char) c == 27) { break; }
    }

}


/**
 * @function threshold_show
 */
void threshold_show(int, void *) {
    /* 0: Binary
       1: Binary Inverted
       2: Threshold Truncated
       3: Threshold to Zero
       4: Threshold to Zero Inverted
     */

    threshold(src_gray, dst, threshold_value, max_BINARY_value, threshold_type);

    imshow(threshold_window, dst);
}

void hsv_show(int, void *) {

    inRange(src_hsv,
            Scalar(bounds.hMin, bounds.sMin, bounds.vMin),
            Scalar(bounds.hMax, bounds.sMax, bounds.vMax),
            hsv_filtered);

    imshow(hsv_window, hsv_filtered);
}

void createTrackbars(HSVbounds &bounds, std::string winName, TrackbarCallback callback) {
    namedWindow(winName, 0);

    createTrackbar("min_H", winName, &bounds.hMin, bounds.hMax, callback);
    createTrackbar("max_H", winName, &bounds.hMax, bounds.hMax, callback);

    createTrackbar("min_S", winName, &bounds.sMin, bounds.sMax, callback);
    createTrackbar("max_S", winName, &bounds.sMax, bounds.sMax, callback);

    createTrackbar("min_V", winName, &bounds.vMin, bounds.vMax, callback);
    createTrackbar("max_V", winName, &bounds.vMax, bounds.vMax, callback);
}