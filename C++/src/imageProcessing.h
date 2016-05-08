//
// Created by clement on 06.05.2016.
//

#ifndef IMAGEPROCESSING_IMAGEPROC_H
#define IMAGEPROCESSING_IMAGEPROC_H


struct HSVbounds {
    int h_Min = 0, s_min = 0, v_min = 0,
            h_max = 180, s_max = 255, v_max = 255;
};

typedef void (*TrackbarCallback)(int pos, void *userdata);

void createTrackbars(HSVbounds &bounds, std::string winName, TrackbarCallback callback);

void hsv_filter(HSVbounds hsv_bounds, cv::Mat &src, cv::Mat &dst);

void blob_filter(cv::Mat &mat);

void canny_edge(cv::Mat &src, cv::Mat &dst);

void hough_transform(cv::Mat &src, cv::Mat &dst);

#endif //IMAGEPROCESSING_IMAGEPROC_H
