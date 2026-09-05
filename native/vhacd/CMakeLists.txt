cmake_minimum_required(VERSION 3.20)
project(bkunvhacd CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(bkunvhacd SHARED vhacd_wrapper.cpp)
find_package(Threads REQUIRED)
target_link_libraries(bkunvhacd PRIVATE Threads::Threads)

# the wrapper marks its entry points extern "C" but not __declspec(dllexport), which gcc does not
# care about because it exports everything anyway. msvc exports nothing, so the dll linked but had
# an empty export table and no symbol the loader could bind to.
set_target_properties(bkunvhacd PROPERTIES WINDOWS_EXPORT_ALL_SYMBOLS ON)
