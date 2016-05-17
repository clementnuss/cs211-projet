//
// Created by Clément on 07.05.2016.
//

#ifndef IMAGEPROCESSING_CAMERA_H
#define IMAGEPROCESSING_CAMERA_H


#include <opencv2/videoio.hpp>

class Camera {
    cv::VideoCapture cap;

public:
    Camera();

    void read(cv::Mat &image);
};

#endif //IMAGEPROCESSING_CAMERA_H
