package com.meekdev.box3d;

import com.meekdev.box3d.ffi.b3Quat;
import com.meekdev.box3d.ffi.b3Vec3;
import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

// ball and socket
public final class B3SphericalJoint extends B3Joint {

    B3SphericalJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    // sets the cone half angle and turns the cone limit on
    public void setConeLimit(float radians) {
        box3d_h.b3SphericalJoint_SetConeLimit(id, radians);
        box3d_h.b3SphericalJoint_EnableConeLimit(id, true);
    }

    public void enableConeLimit(boolean enable) {
        box3d_h.b3SphericalJoint_EnableConeLimit(id, enable);
    }

    public boolean coneLimitEnabled() {
        return box3d_h.b3SphericalJoint_IsConeLimitEnabled(id);
    }

    public float coneLimit() {
        return box3d_h.b3SphericalJoint_GetConeLimit(id);
    }

    // current angle off the cone axis
    public float coneAngle() {
        return box3d_h.b3SphericalJoint_GetConeAngle(id);
    }

    // sets the twist range and turns the twist limit on
    public void setTwistLimits(float lower, float upper) {
        box3d_h.b3SphericalJoint_SetTwistLimits(id, lower, upper);
        box3d_h.b3SphericalJoint_EnableTwistLimit(id, true);
    }

    public void enableTwistLimit(boolean enable) {
        box3d_h.b3SphericalJoint_EnableTwistLimit(id, enable);
    }

    public boolean twistLimitEnabled() {
        return box3d_h.b3SphericalJoint_IsTwistLimitEnabled(id);
    }

    public float lowerTwistLimit() {
        return box3d_h.b3SphericalJoint_GetLowerTwistLimit(id);
    }

    public float upperTwistLimit() {
        return box3d_h.b3SphericalJoint_GetUpperTwistLimit(id);
    }

    public float twistAngle() {
        return box3d_h.b3SphericalJoint_GetTwistAngle(id);
    }

    // sets both spring params and turns the spring on
    public void setSpring(float hertz, float dampingRatio) {
        box3d_h.b3SphericalJoint_SetSpringHertz(id, hertz);
        box3d_h.b3SphericalJoint_SetSpringDampingRatio(id, dampingRatio);
        box3d_h.b3SphericalJoint_EnableSpring(id, true);
    }

    public void enableSpring(boolean enable) {
        box3d_h.b3SphericalJoint_EnableSpring(id, enable);
    }

    public boolean springEnabled() {
        return box3d_h.b3SphericalJoint_IsSpringEnabled(id);
    }

    public void setSpringHertz(float hertz) {
        box3d_h.b3SphericalJoint_SetSpringHertz(id, hertz);
    }

    public float springHertz() {
        return box3d_h.b3SphericalJoint_GetSpringHertz(id);
    }

    public void setSpringDampingRatio(float dampingRatio) {
        box3d_h.b3SphericalJoint_SetSpringDampingRatio(id, dampingRatio);
    }

    public float springDampingRatio() {
        return box3d_h.b3SphericalJoint_GetSpringDampingRatio(id);
    }

    public void setTargetRotation(Quat target) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment q = b3Quat.allocate(temp);
            MemorySegment v = b3Quat.v(q);
            b3Vec3.x(v, target.x());
            b3Vec3.y(v, target.y());
            b3Vec3.z(v, target.z());
            b3Quat.s(q, target.s());
            box3d_h.b3SphericalJoint_SetTargetRotation(id, q);
        }
    }

    public Quat targetRotation() {
        MemorySegment q = box3d_h.b3SphericalJoint_GetTargetRotation(world.scratch(), id);
        MemorySegment v = b3Quat.v(q);
        return new Quat(b3Vec3.x(v), b3Vec3.y(v), b3Vec3.z(v), b3Quat.s(q));
    }

    public void enableMotor(boolean enable) {
        box3d_h.b3SphericalJoint_EnableMotor(id, enable);
    }

    public boolean motorEnabled() {
        return box3d_h.b3SphericalJoint_IsMotorEnabled(id);
    }

    public void setMotorVelocity(Vec3 velocity) {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment v = b3Vec3.allocate(temp);
            b3Vec3.x(v, (float) velocity.x());
            b3Vec3.y(v, (float) velocity.y());
            b3Vec3.z(v, (float) velocity.z());
            box3d_h.b3SphericalJoint_SetMotorVelocity(id, v);
        }
    }

    public Vec3 motorVelocity() {
        MemorySegment v = box3d_h.b3SphericalJoint_GetMotorVelocity(world.scratch(), id);
        return new Vec3(b3Vec3.x(v), b3Vec3.y(v), b3Vec3.z(v));
    }

    public Vec3 motorTorque() {
        MemorySegment t = box3d_h.b3SphericalJoint_GetMotorTorque(world.scratch(), id);
        return new Vec3(b3Vec3.x(t), b3Vec3.y(t), b3Vec3.z(t));
    }

    public void setMaxMotorTorque(float torque) {
        box3d_h.b3SphericalJoint_SetMaxMotorTorque(id, torque);
    }

    public float maxMotorTorque() {
        return box3d_h.b3SphericalJoint_GetMaxMotorTorque(id);
    }
}
