cmake_minimum_required(VERSION 3.20)
project(bkunvhacd CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(bkunvhacd SHARED vhacd_wrapper.cpp)
find_package(Threads REQUIRED)
target_link_libraries(bkunvhacd PRIVATE Threads::Threads)
