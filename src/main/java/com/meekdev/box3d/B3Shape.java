package com.meekdev.box3d;

import com.meekdev.box3d.ffi.b3AABB;
import com.meekdev.box3d.ffi.b3Capsule;
import com.meekdev.box3d.ffi.b3ContactData;
import com.meekdev.box3d.ffi.b3Filter;
import com.meekdev.box3d.ffi.b3Pos;
import com.meekdev.box3d.ffi.b3ShapeId;
import com.meekdev.box3d.ffi.b3Sphere;
import com.meekdev.box3d.ffi.b3SurfaceMaterial;
import com.meekdev.box3d.ffi.b3Vec3;
import com.meekdev.box3d.ffi.b3WorldCastOutput;
import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.util.ArrayList;
import java.util.List;

// thin handle around a native b3ShapeId
public final class B3Shape {

    private final B3World world;
    private final MemorySegment id;

    B3Shape(B3World world, MemorySegment id) {
        this.world = world;
        this.id = id;
    }

    public boolean isValid() {
        return box3d_h.b3Shape_IsValid(id);
    }

    public B3ShapeType type() {
        return B3ShapeType.values()[box3d_h.b3Shape_GetType(id)];
    }

    public B3Body body() {
        return world.bodyByKey(world.shapeBodyKey(id));
    }

    public float friction() {
        return box3d_h.b3Shape_GetFriction(id);
    }

    public void setFriction(float friction) {
        box3d_h.b3Shape_SetFriction(id, friction);
    }

    public float restitution() {
        return box3d_h.b3Shape_GetRestitution(id);
    }

    public void setRestitution(float restitution) {
        box3d_h.b3Shape_SetRestitution(id, restitution);
    }

    public float density() {
        return box3d_h.b3Shape_GetDensity(id);
    }

    public void setDensity(float density) {
        box3d_h.b3Shape_SetDensity(id, density, true);
    }

    public B3SurfaceMat surfaceMaterial() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment mat = box3d_h.b3Shape_GetSurfaceMaterial(temp, id);
            return new B3SurfaceMat(b3SurfaceMaterial.friction(mat),
                    b3SurfaceMaterial.restitution(mat),
                    b3SurfaceMaterial.rollingResistance(mat));
        }
    }

    // keeps the material's other fields, only swaps the three response values
    public void setSurfaceMaterial(B3SurfaceMat material) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment mat = box3d_h.b3Shape_GetSurfaceMaterial(temp, id);
            b3SurfaceMaterial.friction(mat, material.friction());
            b3SurfaceMaterial.restitution(mat, material.restitution());
            b3SurfaceMaterial.rollingResistance(mat, material.rollingResistance());
            box3d_h.b3Shape_SetSurfaceMaterial(id, mat);
        }
    }

    public boolean isSensor() {
        return box3d_h.b3Shape_IsSensor(id);
    }

    public B3Filter filter() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment filter = box3d_h.b3Shape_GetFilter(temp, id);
            return new B3Filter(b3Filter.categoryBits(filter), b3Filter.maskBits(filter));
        }
    }

    public void setFilter(long categoryBits, long maskBits) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment filter = box3d_h.b3Shape_GetFilter(temp, id);
            b3Filter.categoryBits(filter, categoryBits);
            b3Filter.maskBits(filter, maskBits);
            box3d_h.b3Shape_SetFilter(id, filter, true);
        }
    }

    // world space bounds of this one shape, lower then upper
    public Vec3[] aabb() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment box = box3d_h.b3Shape_GetAABB(temp, id);
            MemorySegment lo = b3AABB.lowerBound(box);
            MemorySegment hi = b3AABB.upperBound(box);
            return new Vec3[] {
                    new Vec3(b3Vec3.x(lo), b3Vec3.y(lo), b3Vec3.z(lo)),
                    new Vec3(b3Vec3.x(hi), b3Vec3.y(hi), b3Vec3.z(hi))};
        }
    }

    public B3Sphere sphere() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment s = box3d_h.b3Shape_GetSphere(temp, id);
            MemorySegment c = b3Sphere.center(s);
            return new B3Sphere(new Vec3(b3Vec3.x(c), b3Vec3.y(c), b3Vec3.z(c)), b3Sphere.radius(s));
        }
    }

    public void setSphere(B3Sphere sphere) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment s = b3Sphere.allocate(temp);
            b3Sphere.center(s, vec3(temp, sphere.center()));
            b3Sphere.radius(s, sphere.radius());
            box3d_h.b3Shape_SetSphere(id, s);
        }
    }

    public B3Capsule capsule() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment cap = box3d_h.b3Shape_GetCapsule(temp, id);
            MemorySegment c1 = b3Capsule.center1(cap);
            MemorySegment c2 = b3Capsule.center2(cap);
            return new B3Capsule(
                    new Vec3(b3Vec3.x(c1), b3Vec3.y(c1), b3Vec3.z(c1)),
                    new Vec3(b3Vec3.x(c2), b3Vec3.y(c2), b3Vec3.z(c2)),
                    b3Capsule.radius(cap));
        }
    }

    public void setCapsule(B3Capsule capsule) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment cap = b3Capsule.allocate(temp);
            b3Capsule.center1(cap, vec3(temp, capsule.center1()));
            b3Capsule.center2(cap, vec3(temp, capsule.center2()));
            b3Capsule.radius(cap, capsule.radius());
            box3d_h.b3Shape_SetCapsule(id, cap);
        }
    }

    // nearest point on this shape to a world target
    public Vec3 closestPoint(Vec3 target) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment p = box3d_h.b3Shape_GetClosestPoint(temp, id, vec3(temp, target));
            return new Vec3(b3Vec3.x(p), b3Vec3.y(p), b3Vec3.z(p));
        }
    }

    // ray from origin to origin plus translation against this shape alone
    public B3World.RayHit rayCast(Vec3 origin, Vec3 translation) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment out = box3d_h.b3Shape_RayCast(temp, id, pos(temp, origin), vec3(temp, translation));
            boolean hit = b3WorldCastOutput.hit(out);
            MemorySegment point = b3WorldCastOutput.point(out);
            MemorySegment normal = b3WorldCastOutput.normal(out);
            return new B3World.RayHit(hit,
                    new Vec3(b3Pos.x(point), b3Pos.y(point), b3Pos.z(point)),
                    new Vec3(b3Vec3.x(normal), b3Vec3.y(normal), b3Vec3.z(normal)),
                    b3WorldCastOutput.fraction(out),
                    hit ? body() : null);
        }
    }

    public void enableContactEvents(boolean flag) {
        box3d_h.b3Shape_EnableContactEvents(id, flag);
    }

    public boolean contactEventsEnabled() {
        return box3d_h.b3Shape_AreContactEventsEnabled(id);
    }

    public void enableHitEvents(boolean flag) {
        box3d_h.b3Shape_EnableHitEvents(id, flag);
    }

    public boolean hitEventsEnabled() {
        return box3d_h.b3Shape_AreHitEventsEnabled(id);
    }

    public void enablePreSolveEvents(boolean flag) {
        box3d_h.b3Shape_EnablePreSolveEvents(id, flag);
    }

    public boolean preSolveEventsEnabled() {
        return box3d_h.b3Shape_ArePreSolveEventsEnabled(id);
    }

    public void enableSensorEvents(boolean flag) {
        box3d_h.b3Shape_EnableSensorEvents(id, flag);
    }

    public boolean sensorEventsEnabled() {
        return box3d_h.b3Shape_AreSensorEventsEnabled(id);
    }

    public void setUserData(long userData) {
        box3d_h.b3Shape_SetUserData(id, MemorySegment.ofAddress(userData));
    }

    public long userData() {
        return box3d_h.b3Shape_GetUserData(id).address();
    }

    public void setName(String name) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3Shape_SetName(id, temp.allocateFrom(name));
        }
    }

    public String name() {
        MemorySegment n = box3d_h.b3Shape_GetName(id);
        return n.equals(MemorySegment.NULL) ? "" : n.reinterpret(32).getString(0);
    }

    // aerodynamic push on this shape, drag along the wind, lift across it
    public void applyWind(Vec3 wind, float drag, float lift, float maxSpeed) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3Shape_ApplyWind(id, vec3(temp, wind), drag, lift, maxSpeed, true);
        }
    }

    // bodies currently overlapping this sensor shape
    public List<B3Body> sensorOverlaps() {
        try (Arena temp = Arena.ofConfined()) {
            int capacity = box3d_h.b3Shape_GetSensorCapacity(id);
            if (capacity <= 0) {
                return List.of();
            }
            MemorySegment visitors = b3ShapeId.allocateArray(capacity, temp);
            int count = box3d_h.b3Shape_GetSensorData(id, visitors, capacity);
            List<B3Body> bodies = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                B3Body body = world.bodyByKey(world.shapeBodyKey(b3ShapeId.asSlice(visitors, i)));
                if (body != null) {
                    bodies.add(body);
                }
            }
            return bodies;
        }
    }

    public B3World world() {
        return world;
    }

    // distinct bodies currently in contact with this shape
    public List<B3Body> contacts() {
        int capacity = box3d_h.b3Shape_GetContactCapacity(id);
        if (capacity <= 0) {
            return List.of();
        }
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment array = b3ContactData.allocateArray(capacity, temp);
            int count = box3d_h.b3Shape_GetContactData(id, array, capacity);
            List<B3Body> others = new ArrayList<>(count);
            B3Body self = body();
            for (int i = 0; i < count; i++) {
                MemorySegment data = b3ContactData.asSlice(array, i);
                B3Body a = world.bodyByKey(world.shapeBodyKey(b3ContactData.shapeIdA(data)));
                B3Body b = world.bodyByKey(world.shapeBodyKey(b3ContactData.shapeIdB(data)));
                B3Body other = self == a ? b : a;
                if (other != null && !others.contains(other)) {
                    others.add(other);
                }
            }
            return others;
        }
    }

    MemorySegment idSegment() {
        return id;
    }

    public void destroy() {
        box3d_h.b3DestroyShape(id, true);
    }

    private static MemorySegment pos(SegmentAllocator allocator, Vec3 v) {
        MemorySegment seg = b3Pos.allocate(allocator);
        b3Pos.x(seg, v.x());
        b3Pos.y(seg, v.y());
        b3Pos.z(seg, v.z());
        return seg;
    }

    private static MemorySegment vec3(SegmentAllocator allocator, Vec3 v) {
        MemorySegment seg = b3Vec3.allocate(allocator);
        b3Vec3.x(seg, (float) v.x());
        b3Vec3.y(seg, (float) v.y());
        b3Vec3.z(seg, (float) v.z());
        return seg;
    }
}
