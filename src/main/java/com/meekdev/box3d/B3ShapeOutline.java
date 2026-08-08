package com.meekdev.box3d;

import com.meekdev.box3d.ffi.b3Capsule;
import com.meekdev.box3d.ffi.b3DebugShape;
import com.meekdev.box3d.ffi.b3HullData;
import com.meekdev.box3d.ffi.b3Mesh;
import com.meekdev.box3d.ffi.b3MeshData;
import com.meekdev.box3d.ffi.b3Sphere;
import com.meekdev.box3d.ffi.b3Vec3;

import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class B3ShapeOutline {

    private static final int SPHERE_SEGMENTS = 16;
    private static final int MAXIMUM_MESH_EDGES = 40000;
    private static final long VECTOR_STRIDE = b3Vec3.layout().byteSize();
    private static final long TRIANGLE_STRIDE = 12L;
    private static final long HALF_EDGE_STRIDE = 4L;
    private static final int CAPSULE = 0;
    private static final int HULL = 3;
    private static final int MESH = 4;
    private static final int SPHERE = 5;

    private B3ShapeOutline() {
    }

    static float[] build(MemorySegment debugShape) {
        List<Float> points = new ArrayList<>();
        switch (b3DebugShape.type(debugShape)) {
            case CAPSULE -> appendCapsule(points, b3DebugShape.capsule(debugShape));
            case HULL -> appendHull(points, b3DebugShape.hull(debugShape));
            case MESH -> appendMesh(points, b3DebugShape.mesh(debugShape));
            case SPHERE -> appendSphere(points, b3DebugShape.sphere(debugShape));
            default -> {
            }
        }
        float[] result = new float[points.size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = points.get(index);
        }
        return result;
    }

    private static void appendSphere(List<Float> points, MemorySegment sphere) {
        MemorySegment shape = sphere.reinterpret(b3Sphere.layout().byteSize());
        MemorySegment center = b3Sphere.center(shape);
        float radius = b3Sphere.radius(shape);
        appendRings(points, b3Vec3.x(center), b3Vec3.y(center), b3Vec3.z(center), radius);
    }

    private static void appendCapsule(List<Float> points, MemorySegment capsule) {
        MemorySegment shape = capsule.reinterpret(b3Capsule.layout().byteSize());
        MemorySegment first = b3Capsule.center1(shape);
        MemorySegment second = b3Capsule.center2(shape);
        float radius = b3Capsule.radius(shape);
        appendRings(points, b3Vec3.x(first), b3Vec3.y(first), b3Vec3.z(first), radius);
        appendRings(points, b3Vec3.x(second), b3Vec3.y(second), b3Vec3.z(second), radius);
        segment(points, b3Vec3.x(first) + radius, b3Vec3.y(first), b3Vec3.z(first),
                b3Vec3.x(second) + radius, b3Vec3.y(second), b3Vec3.z(second));
        segment(points, b3Vec3.x(first) - radius, b3Vec3.y(first), b3Vec3.z(first),
                b3Vec3.x(second) - radius, b3Vec3.y(second), b3Vec3.z(second));
        segment(points, b3Vec3.x(first), b3Vec3.y(first), b3Vec3.z(first) + radius,
                b3Vec3.x(second), b3Vec3.y(second), b3Vec3.z(second) + radius);
        segment(points, b3Vec3.x(first), b3Vec3.y(first), b3Vec3.z(first) - radius,
                b3Vec3.x(second), b3Vec3.y(second), b3Vec3.z(second) - radius);
    }

    private static void appendRings(List<Float> points, float x, float y, float z, float radius) {
        for (int step = 0; step < SPHERE_SEGMENTS; step++) {
            double first = 2.0 * Math.PI * step / SPHERE_SEGMENTS;
            double second = 2.0 * Math.PI * (step + 1) / SPHERE_SEGMENTS;
            float cosFirst = (float) Math.cos(first) * radius;
            float sinFirst = (float) Math.sin(first) * radius;
            float cosSecond = (float) Math.cos(second) * radius;
            float sinSecond = (float) Math.sin(second) * radius;
            segment(points, x + cosFirst, y + sinFirst, z, x + cosSecond, y + sinSecond, z);
            segment(points, x + cosFirst, y, z + sinFirst, x + cosSecond, y, z + sinSecond);
            segment(points, x, y + cosFirst, z + sinFirst, x, y + cosSecond, z + sinSecond);
        }
    }

    private static void appendHull(List<Float> points, MemorySegment hull) {
        MemorySegment header = hull.reinterpret(b3HullData.layout().byteSize());
        int vertexCount = b3HullData.vertexCount(header);
        int pointOffset = b3HullData.pointOffset(header);
        int halfEdgeCount = b3HullData.edgeCount(header);
        int edgeOffset = b3HullData.edgeOffset(header);
        if (vertexCount <= 1 || pointOffset == 0) {
            return;
        }
        long span = Math.max(pointOffset + (long) vertexCount * VECTOR_STRIDE,
                edgeOffset + (long) halfEdgeCount * HALF_EDGE_STRIDE);
        MemorySegment blob = hull.reinterpret(span);
        float[] vertices = readVectors(blob, pointOffset, vertexCount);
        if (halfEdgeCount <= 0) {
            appendVertexCloud(points, vertices, vertexCount);
            return;
        }
        for (int index = 0; index < halfEdgeCount; index++) {
            long base = edgeOffset + (long) index * HALF_EDGE_STRIDE;
            int twin = Byte.toUnsignedInt(blob.get(java.lang.foreign.ValueLayout.JAVA_BYTE, base + 1));
            if (twin <= index || twin >= halfEdgeCount) {
                continue;
            }
            int origin = Byte.toUnsignedInt(blob.get(java.lang.foreign.ValueLayout.JAVA_BYTE, base + 2));
            long twinBase = edgeOffset + (long) twin * HALF_EDGE_STRIDE;
            int target = Byte.toUnsignedInt(blob.get(java.lang.foreign.ValueLayout.JAVA_BYTE, twinBase + 2));
            if (origin >= vertexCount || target >= vertexCount) {
                continue;
            }
            segment(points, vertices[origin * 3], vertices[origin * 3 + 1], vertices[origin * 3 + 2],
                    vertices[target * 3], vertices[target * 3 + 1], vertices[target * 3 + 2]);
        }
    }

    private static void appendVertexCloud(List<Float> points, float[] vertices, int vertexCount) {
        for (int first = 0; first < vertexCount; first++) {
            for (int second = first + 1; second < vertexCount; second++) {
                segment(points, vertices[first * 3], vertices[first * 3 + 1], vertices[first * 3 + 2],
                        vertices[second * 3], vertices[second * 3 + 1], vertices[second * 3 + 2]);
            }
        }
    }

    private static void appendMesh(List<Float> points, MemorySegment mesh) {
        MemorySegment wrapper = mesh.reinterpret(b3Mesh.layout().byteSize());
        MemorySegment data = b3Mesh.data(wrapper);
        if (data.address() == 0L) {
            return;
        }
        MemorySegment header = data.reinterpret(b3MeshData.layout().byteSize());
        int vertexCount = b3MeshData.vertexCount(header);
        int vertexOffset = b3MeshData.vertexOffset(header);
        int triangleCount = b3MeshData.triangleCount(header);
        int triangleOffset = b3MeshData.triangleOffset(header);
        if (vertexCount <= 0 || triangleCount <= 0) {
            return;
        }
        long span = Math.max(vertexOffset + (long) vertexCount * VECTOR_STRIDE,
                triangleOffset + (long) triangleCount * TRIANGLE_STRIDE);
        MemorySegment blob = data.reinterpret(span);
        float[] vertices = readVectors(blob, vertexOffset, vertexCount);
        Set<Long> seen = new HashSet<>();
        int drawn = 0;
        for (int triangle = 0; triangle < triangleCount && drawn < MAXIMUM_MESH_EDGES; triangle++) {
            long base = triangleOffset + (long) triangle * TRIANGLE_STRIDE;
            int first = blob.get(java.lang.foreign.ValueLayout.JAVA_INT, base);
            int second = blob.get(java.lang.foreign.ValueLayout.JAVA_INT, base + 4);
            int third = blob.get(java.lang.foreign.ValueLayout.JAVA_INT, base + 8);
            drawn += edge(points, vertices, vertexCount, seen, first, second);
            drawn += edge(points, vertices, vertexCount, seen, second, third);
            drawn += edge(points, vertices, vertexCount, seen, third, first);
        }
    }

    private static int edge(List<Float> points, float[] vertices, int vertexCount,
                            Set<Long> seen, int first, int second) {
        if (first < 0 || second < 0 || first >= vertexCount || second >= vertexCount) {
            return 0;
        }
        long low = Math.min(first, second);
        long high = Math.max(first, second);
        if (!seen.add((low << 32) | high)) {
            return 0;
        }
        segment(points, vertices[first * 3], vertices[first * 3 + 1], vertices[first * 3 + 2],
                vertices[second * 3], vertices[second * 3 + 1], vertices[second * 3 + 2]);
        return 1;
    }

    private static float[] readVectors(MemorySegment blob, long offset, int count) {
        float[] values = new float[count * 3];
        for (int index = 0; index < count; index++) {
            long base = offset + (long) index * VECTOR_STRIDE;
            values[index * 3] = blob.get(java.lang.foreign.ValueLayout.JAVA_FLOAT, base);
            values[index * 3 + 1] = blob.get(java.lang.foreign.ValueLayout.JAVA_FLOAT, base + 4);
            values[index * 3 + 2] = blob.get(java.lang.foreign.ValueLayout.JAVA_FLOAT, base + 8);
        }
        return values;
    }

    private static void segment(List<Float> points, float startX, float startY, float startZ,
                                float endX, float endY, float endZ) {
        points.add(startX);
        points.add(startY);
        points.add(startZ);
        points.add(endX);
        points.add(endY);
        points.add(endZ);
    }
}
