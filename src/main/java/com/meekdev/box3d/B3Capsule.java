package com.meekdev.box3d;

// a capsule read back off a shape, both centers are body local
public record B3Capsule(Vec3 center1, Vec3 center2, float radius) {
}
