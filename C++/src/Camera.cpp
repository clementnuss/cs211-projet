//
// Created by Clément on 07.05.2016.
//

#define FRAME_WIDTH 800
#define FRAME_HEIGHT 600

#include <c++/4.8.3/iostream>
#include <opencv2/videoio/videoio_c.h>
#include "Camera.h"

void Camera::read(cv::Mat &image) {
    cap.read(image);
}

Camera::Camera() {

    cap.open(0);

    if (!cap.isOpened())  // no success --> exit program
    {
        std::cerr << "Cannot open the web cam\n";
    }

    cap.set(CV_CAP_PROP_FRAME_WIDTH, FRAME_WIDTH);
    cap.set(CV_CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT);
}



