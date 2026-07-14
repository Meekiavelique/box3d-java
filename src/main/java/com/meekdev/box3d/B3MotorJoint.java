package com.meekdev.box3d;

import com.meekdev.box3d.ffi.b3Vec3;
import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

// drives the relative velocity between two bodies, good for conveyor and hover things
public final class B3MotorJoint extends B3Joint {

    B3MotorJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public void setLinearVelocity(Vec3 v) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3MotorJoint_SetLinearVelocity(id, vec3(temp, v));
        }
        wakeBodies();
    }

    public Vec3 linearVelocity() {
        MemorySegment v = box3d_h.b3MotorJoint_GetLinearVelocity(world.scratch(), id);
        return new Vec3(b3Vec3.x(v), b3Vec3.y(v), b3Vec3.z(v));
    }

    public void setAngularVelocity(Vec3 v) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3MotorJoint_SetAngularVelocity(id, vec3(temp, v));
        }
        wakeBodies();
    }

    public Vec3 angularVelocity() {
        MemorySegment v = box3d_h.b3MotorJoint_GetAngularVelocity(world.scratch(), id);
        return new Vec3(b3Vec3.x(v), b3Vec3.y(v), b3Vec3.z(v));
    }

    public void setMaxVelocityForce(float force) {
        box3d_h.b3MotorJoint_SetMaxVelocityForce(id, force);
    }

    public float maxVelocityForce() {
        return box3d_h.b3MotorJoint_GetMaxVelocityForce(id);
    }

    public void setMaxVelocityTorque(float torque) {
        box3d_h.b3MotorJoint_SetMaxVelocityTorque(id, torque);
    }

    public float maxVelocityTorque() {
        return box3d_h.b3MotorJoint_GetMaxVelocityTorque(id);
    }

    public void setLinearHertz(float hertz) {
        box3d_h.b3MotorJoint_SetLinearHertz(id, hertz);
    }

    public float linearHertz() {
        return box3d_h.b3MotorJoint_GetLinearHertz(id);
    }

    public void setLinearDampingRatio(float damping) {
        box3d_h.b3MotorJoint_SetLinearDampingRatio(id, damping);
    }

    public float linearDampingRatio() {
        return box3d_h.b3MotorJoint_GetLinearDampingRatio(id);
    }

    public void setAngularHertz(float hertz) {
        box3d_h.b3MotorJoint_SetAngularHertz(id, hertz);
    }

    public float angularHertz() {
        return box3d_h.b3MotorJoint_GetAngularHertz(id);
    }

    public void setAngularDampingRatio(float damping) {
        box3d_h.b3MotorJoint_SetAngularDampingRatio(id, damping);
    }

    public float angularDampingRatio() {
        return box3d_h.b3MotorJoint_GetAngularDampingRatio(id);
    }

    public void setMaxSpringForce(float force) {
        box3d_h.b3MotorJoint_SetMaxSpringForce(id, force);
    }

    public float maxSpringForce() {
        return box3d_h.b3MotorJoint_GetMaxSpringForce(id);
    }

    public void setMaxSpringTorque(float torque) {
        box3d_h.b3MotorJoint_SetMaxSpringTorque(id, torque);
    }

    public float maxSpringTorque() {
        return box3d_h.b3MotorJoint_GetMaxSpringTorque(id);
    }

    private static MemorySegment vec3(Arena arena, Vec3 v) {
        MemorySegment seg = b3Vec3.allocate(arena);
        b3Vec3.x(seg, (float) v.x());
        b3Vec3.y(seg, (float) v.y());
        b3Vec3.z(seg, (float) v.z());
        return seg;
    }
}
