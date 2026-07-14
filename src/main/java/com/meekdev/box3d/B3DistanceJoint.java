package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public final class B3DistanceJoint extends B3Joint {

    B3DistanceJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public float length() {
        return box3d_h.b3DistanceJoint_GetLength(id);
    }

    public void setLength(float length) {
        box3d_h.b3DistanceJoint_SetLength(id, length);
    }

    public float currentLength() {
        return box3d_h.b3DistanceJoint_GetCurrentLength(id);
    }

    public void enableSpring(boolean enable) {
        box3d_h.b3DistanceJoint_EnableSpring(id, enable);
    }

    public boolean springEnabled() {
        return box3d_h.b3DistanceJoint_IsSpringEnabled(id);
    }

    // sets both spring params and turns the spring on
    public void setSpring(float hertz, float dampingRatio) {
        box3d_h.b3DistanceJoint_SetSpringHertz(id, hertz);
        box3d_h.b3DistanceJoint_SetSpringDampingRatio(id, dampingRatio);
        box3d_h.b3DistanceJoint_EnableSpring(id, true);
    }

    public void setSpringHertz(float hertz) {
        box3d_h.b3DistanceJoint_SetSpringHertz(id, hertz);
    }

    public float springHertz() {
        return box3d_h.b3DistanceJoint_GetSpringHertz(id);
    }

    public void setSpringDampingRatio(float dampingRatio) {
        box3d_h.b3DistanceJoint_SetSpringDampingRatio(id, dampingRatio);
    }

    public float springDampingRatio() {
        return box3d_h.b3DistanceJoint_GetSpringDampingRatio(id);
    }

    public void setSpringForceRange(float lower, float upper) {
        box3d_h.b3DistanceJoint_SetSpringForceRange(id, lower, upper);
    }

    // lower then upper spring force
    public float[] springForceRange() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment lo = temp.allocate(ValueLayout.JAVA_FLOAT);
            MemorySegment hi = temp.allocate(ValueLayout.JAVA_FLOAT);
            box3d_h.b3DistanceJoint_GetSpringForceRange(id, lo, hi);
            return new float[] {lo.get(ValueLayout.JAVA_FLOAT, 0), hi.get(ValueLayout.JAVA_FLOAT, 0)};
        }
    }

    // sets the length limits and turns the limit on
    public void setLengthRange(float min, float max) {
        box3d_h.b3DistanceJoint_SetLengthRange(id, min, max);
        box3d_h.b3DistanceJoint_EnableLimit(id, true);
    }

    public void enableLimit(boolean enable) {
        box3d_h.b3DistanceJoint_EnableLimit(id, enable);
    }

    public boolean limitEnabled() {
        return box3d_h.b3DistanceJoint_IsLimitEnabled(id);
    }

    public float minLength() {
        return box3d_h.b3DistanceJoint_GetMinLength(id);
    }

    public float maxLength() {
        return box3d_h.b3DistanceJoint_GetMaxLength(id);
    }

    public void enableMotor(boolean enable) {
        box3d_h.b3DistanceJoint_EnableMotor(id, enable);
    }

    public boolean motorEnabled() {
        return box3d_h.b3DistanceJoint_IsMotorEnabled(id);
    }

    public void setMotorSpeed(float speed) {
        box3d_h.b3DistanceJoint_SetMotorSpeed(id, speed);
    }

    public float motorSpeed() {
        return box3d_h.b3DistanceJoint_GetMotorSpeed(id);
    }

    public void setMaxMotorForce(float force) {
        box3d_h.b3DistanceJoint_SetMaxMotorForce(id, force);
    }

    public float maxMotorForce() {
        return box3d_h.b3DistanceJoint_GetMaxMotorForce(id);
    }

    public float motorForce() {
        return box3d_h.b3DistanceJoint_GetMotorForce(id);
    }
}
