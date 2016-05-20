//
// Created by Clément on 06.05.2016.
//

#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

#include "imageProcessing.h"

using namespace cv;

void createTrackbars(HSVbounds &bounds, std::string winName, TrackbarCallback callback) {
    namedWindow(winName, 0);

    createTrackbar("min_H", winName, &bounds.h_Min, bounds.h_max, callback);
    createTrackbar("max_H", winName, &bounds.h_max, bounds.h_max, callback);

    createTrackbar("min_S", winName, &bounds.s_min, bounds.s_max, callback);
    createTrackbar("max_S", winName, &bounds.s_max, bounds.s_max, callback);

    createTrackbar("min_V", winName, &bounds.v_min, bounds.v_max, callback);
    createTrackbar("max_V", winName, &bounds.v_max, bounds.v_max, callback);
}

void hsv_filter(HSVbounds hsv_bounds, Mat &src, Mat &dst) {
    inRange(src,
            Scalar(hsv_bounds.h_Min, hsv_bounds.s_min, hsv_bounds.v_min),
            Scalar(hsv_bounds.h_max, hsv_bounds.s_max, hsv_bounds.v_max),
            dst);
}

void blob_filter(Mat &mat) {

    Mat erodeElement = getStructuringElement(MORPH_RECT, Size(5, 5));
    Mat dilateElement = getStructuringElement(MORPH_RECT, Size(7, 7));

    erode(mat, mat, erodeElement);

    dilate(mat, mat, dilateElement);
}

void canny_edge(Mat &src, Mat &dst) {
    Canny(src, dst, 200, 50);
}

void hough_transform(cv::Mat &src, cv::Mat &dst) {

    std::vector<Vec2f> lines;
    HoughLines(src, lines, 1, CV_PI / 180, 100, 0, 0);

    for (size_t i = 0; i < lines.size(); i++) {
        float rho = lines[i][0], theta = lines[i][1];
        Point pt1, pt2;
        double a = cos(theta), b = sin(theta);
        double x0 = a * rho, y0 = b * rho;
        pt1.x = cvRound(x0 + 1000 * (-b));
        pt1.y = cvRound(y0 + 1000 * (a));
        pt2.x = cvRound(x0 - 1000 * (-b));
        pt2.y = cvRound(y0 - 1000 * (a));
        line(dst, pt1, pt2, Scalar(0, 0, 255), 3, CV_AA);
    }
}

