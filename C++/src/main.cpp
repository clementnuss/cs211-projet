#include <iostream>
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "imageProcessing.h"

using namespace cv;

Mat src, src_hsv, hsv_filtered, img_canny, hough_result;
HSVbounds bounds;

const char *hsv_window = "HSV";
const char *hsv_trackbars = "HSV Trackbars";

const char *canny_window = "Canny";
const char *hough_window = "Hough";


void hsv_show(int, void *);

void canny_show();

void hough_show();

int main(int argc, char **argv) {

    if (argc < 2) {
        std::cerr << "You need to call this program with the path of the image to display !";
        return 0xbeef;
    }
    /// Load the image
    src = imread(argv[1], 1);

    // Convert image to HSV color space
    cvtColor(src, src_hsv, COLOR_BGR2HSV);

    // Create the trackbars to filter the HSV image
    createTrackbars(bounds, hsv_trackbars, hsv_show);

    // Run the callback at initialization.
    // It will be called whenever a change to one of the HSV bounds occurs.
    hsv_show(0, 0);

    /// Wait until user finishes program
    while (true) {

        int c = waitKey(20);
        if ((char) c == 27) { break; }
    }

}

/*
 * @function Callback function, called whenever a change occurs to the HSV trackbars
*/

void hsv_show(int, void *) {

    hsv_filter(bounds, src_hsv, hsv_filtered);

    blob_filter(hsv_filtered);

    imshow(hsv_window, hsv_filtered);

    canny_show();
}

void canny_show() {

    canny_edge(hsv_filtered, img_canny);

    imshow(canny_window, img_canny);

    Mat hough_result;

    cvtColor(img_canny, hough_result, COLOR_GRAY2BGR);

    hough_transform(img_canny, hough_result);

    imshow(hough_window, hough_result);

}
void hough_show() {

    // Declare a CV mat to print the lines obtained with the Hough method
    hough_result = Mat(src.rows, src.cols, CV_8UC3);

    hough_transform(img_canny, hough_result);

    imshow(hough_window, hough_result);
}

