package com.meekdev.box3d;

// a position and rotation, used for body transforms and joint local frames
public record B3Transform(Vec3 position, Quat rotation) {
}
