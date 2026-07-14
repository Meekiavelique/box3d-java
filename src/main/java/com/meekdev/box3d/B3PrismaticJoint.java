package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.MemorySegment;

// slider along the frame axis
public final class B3PrismaticJoint extends B3Joint {

    B3PrismaticJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public float translation() {
        return box3d_h.b3PrismaticJoint_GetTranslation(id);
    }

    public void setLimits(float lower, float upper) {
        box3d_h.b3PrismaticJoint_SetLimits(id, lower, upper);
        box3d_h.b3PrismaticJoint_EnableLimit(id, true);
    }

    public void setMotor(float speed, float maxForce) {
        box3d_h.b3PrismaticJoint_SetMotorSpeed(id, speed);
        box3d_h.b3PrismaticJoint_SetMaxMotorForce(id, maxForce);
        box3d_h.b3PrismaticJoint_EnableMotor(id, true);
        wakeBodies();
    }

    public void setSpring(float hertz, float dampingRatio, float targetTranslation) {
        box3d_h.b3PrismaticJoint_SetSpringHertz(id, hertz);
        box3d_h.b3PrismaticJoint_SetSpringDampingRatio(id, dampingRatio);
        box3d_h.b3PrismaticJoint_SetTargetTranslation(id, targetTranslation);
        box3d_h.b3PrismaticJoint_EnableSpring(id, true);
        wakeBodies();
    }

    public void enableSpring(boolean flag) {
        box3d_h.b3PrismaticJoint_EnableSpring(id, flag);
    }

    public boolean springEnabled() {
        return box3d_h.b3PrismaticJoint_IsSpringEnabled(id);
    }

    public void setSpringHertz(float hertz) {
        box3d_h.b3PrismaticJoint_SetSpringHertz(id, hertz);
    }

    public float springHertz() {
        return box3d_h.b3PrismaticJoint_GetSpringHertz(id);
    }

    public void setSpringDampingRatio(float dampingRatio) {
        box3d_h.b3PrismaticJoint_SetSpringDampingRatio(id, dampingRatio);
    }

    public float springDampingRatio() {
        return box3d_h.b3PrismaticJoint_GetSpringDampingRatio(id);
    }

    public void setTargetTranslation(float targetTranslation) {
        box3d_h.b3PrismaticJoint_SetTargetTranslation(id, targetTranslation);
    }

    public float targetTranslation() {
        return box3d_h.b3PrismaticJoint_GetTargetTranslation(id);
    }

    public void enableLimit(boolean flag) {
        box3d_h.b3PrismaticJoint_EnableLimit(id, flag);
    }

    public boolean limitEnabled() {
        return box3d_h.b3PrismaticJoint_IsLimitEnabled(id);
    }

    public float lowerLimit() {
        return box3d_h.b3PrismaticJoint_GetLowerLimit(id);
    }

    public float upperLimit() {
        return box3d_h.b3PrismaticJoint_GetUpperLimit(id);
    }

    public void enableMotor(boolean flag) {
        box3d_h.b3PrismaticJoint_EnableMotor(id, flag);
    }

    public boolean motorEnabled() {
        return box3d_h.b3PrismaticJoint_IsMotorEnabled(id);
    }

    public void setMotorSpeed(float motorSpeed) {
        box3d_h.b3PrismaticJoint_SetMotorSpeed(id, motorSpeed);
    }

    public float motorSpeed() {
        return box3d_h.b3PrismaticJoint_GetMotorSpeed(id);
    }

    public void setMaxMotorForce(float force) {
        box3d_h.b3PrismaticJoint_SetMaxMotorForce(id, force);
    }

    public float maxMotorForce() {
        return box3d_h.b3PrismaticJoint_GetMaxMotorForce(id);
    }

    // solver force from last step
    public float motorForce() {
        return box3d_h.b3PrismaticJoint_GetMotorForce(id);
    }

    public float speed() {
        return box3d_h.b3PrismaticJoint_GetSpeed(id);
    }
}
