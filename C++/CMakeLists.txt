cmake_minimum_required(VERSION 3.3)
project(imageProcessing)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/bin")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fpermissive -static")

if (UNIX)
    include_directories(${OpenCV_INCLUDE_DIRS})
    set(CMAKE_MODULE_PATH /usr/local/lib/cmake)
    find_package(OpenCV REQUIRED)
    find_package(raspicam REQUIRED)
elseif (WIN32)
    set(OpenCV_DIR "C:\\clm\\opencv-3.1.0")
    find_package(OpenCV REQUIRED)
endif ()

set(SOURCE_FILES src/main.cpp src/imageProcessing.cpp src/imageProcessing.h src/Camera.cpp src/Camera.h)
add_executable(imageProcessing ${SOURCE_FILES})

if (UNIX)
    target_link_libraries(imageProcessing ${OpenCV_LIBS} pthread)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
elseif (WIN32)
    target_link_libraries(imageProcessing ${OpenCV_LIBS})
endif ()

