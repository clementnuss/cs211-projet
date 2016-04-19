cmake_minimum_required(VERSION 3.5)
project(imageProcessing)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/bin")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fpermissive -static")

if (UNIX)
    include_directories(${OpenCV_INCLUDE_DIRS})
    set(CMAKE_MODULE_PATH /usr/local/lib/cmake)
    find_package(OpenCV REQUIRED)
    find_package(raspicam REQUIRED)
elseif (WIN32)
    set(OpenCV_DIR "C:\\OpenCV\\opencv-2.4.11")
    find_package(OpenCV REQUIRED)
endif ()

set(SOURCE_FILES main.cpp)
add_executable(imageProcessing ${SOURCE_FILES})

if (UNIX)
    target_link_libraries(imageProcessing ${OpenCV_LIBS} pthread)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
elseif (WIN32)
    target_link_libraries(imageProcessing ${OpenCV_LIBS})
endif ()

