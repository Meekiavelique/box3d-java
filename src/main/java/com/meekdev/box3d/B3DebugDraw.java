package com.meekdev.box3d;

public interface B3DebugDraw {

    void segment(float startX, float startY, float startZ,
                 float endX, float endY, float endZ, int color);

    default void point(float x, float y, float z, float size, int color) {
        float half = size * 0.5f;
        segment(x - half, y, z, x + half, y, z, color);
        segment(x, y - half, z, x, y + half, z, color);
        segment(x, y, z - half, x, y, z + half, color);
    }

    default void sphere(float x, float y, float z, float radius, int color) {
        segment(x - radius, y, z, x + radius, y, z, color);
        segment(x, y - radius, z, x, y + radius, z, color);
        segment(x, y, z - radius, x, y, z + radius, color);
    }

    default void capsule(float startX, float startY, float startZ,
                         float endX, float endY, float endZ, float radius, int color) {
        segment(startX, startY, startZ, endX, endY, endZ, color);
        sphere(startX, startY, startZ, radius, color);
        sphere(endX, endY, endZ, radius, color);
    }

    default void box(float halfX, float halfY, float halfZ,
                     float centerX, float centerY, float centerZ,
                     float rotationX, float rotationY, float rotationZ, float rotationW, int color) {
        for (int corner = 0; corner < 4; corner++) {
            float signY = (corner & 1) == 0 ? -1.0f : 1.0f;
            float signZ = (corner & 2) == 0 ? -1.0f : 1.0f;
            segment(centerX - halfX, centerY + halfY * signY, centerZ + halfZ * signZ,
                    centerX + halfX, centerY + halfY * signY, centerZ + halfZ * signZ, color);
        }
    }

    B3DebugFlags flags();
}
