package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.MemorySegment;

public final class B3RevoluteJoint extends B3Joint {

    B3RevoluteJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public float angle() {
        return box3d_h.b3RevoluteJoint_GetAngle(id);
    }

    public void setLimits(float lower, float upper) {
        box3d_h.b3RevoluteJoint_SetLimits(id, lower, upper);
        box3d_h.b3RevoluteJoint_EnableLimit(id, true);
    }

    public void setMotor(float speed, float maxTorque) {
        box3d_h.b3RevoluteJoint_SetMotorSpeed(id, speed);
        box3d_h.b3RevoluteJoint_SetMaxMotorTorque(id, maxTorque);
        box3d_h.b3RevoluteJoint_EnableMotor(id, true);
        wakeBodies();
    }

    public float motorTorque() {
        return box3d_h.b3RevoluteJoint_GetMotorTorque(id);
    }

    public void enableSpring(boolean flag) {
        box3d_h.b3RevoluteJoint_EnableSpring(id, flag);
    }

    public boolean springEnabled() {
        return box3d_h.b3RevoluteJoint_IsSpringEnabled(id);
    }

    public void setSpringHertz(float hertz) {
        box3d_h.b3RevoluteJoint_SetSpringHertz(id, hertz);
    }

    public float springHertz() {
        return box3d_h.b3RevoluteJoint_GetSpringHertz(id);
    }

    public void setSpringDampingRatio(float dampingRatio) {
        box3d_h.b3RevoluteJoint_SetSpringDampingRatio(id, dampingRatio);
    }

    public float springDampingRatio() {
        return box3d_h.b3RevoluteJoint_GetSpringDampingRatio(id);
    }

    // radians
    public void setTargetAngle(float targetRadians) {
        box3d_h.b3RevoluteJoint_SetTargetAngle(id, targetRadians);
    }

    public float targetAngle() {
        return box3d_h.b3RevoluteJoint_GetTargetAngle(id);
    }

    public void enableLimit(boolean flag) {
        box3d_h.b3RevoluteJoint_EnableLimit(id, flag);
    }

    public boolean limitEnabled() {
        return box3d_h.b3RevoluteJoint_IsLimitEnabled(id);
    }

    public float lowerLimit() {
        return box3d_h.b3RevoluteJoint_GetLowerLimit(id);
    }

    public float upperLimit() {
        return box3d_h.b3RevoluteJoint_GetUpperLimit(id);
    }

    public void enableMotor(boolean flag) {
        box3d_h.b3RevoluteJoint_EnableMotor(id, flag);
    }

    public boolean motorEnabled() {
        return box3d_h.b3RevoluteJoint_IsMotorEnabled(id);
    }

    public void setMotorSpeed(float motorSpeed) {
        box3d_h.b3RevoluteJoint_SetMotorSpeed(id, motorSpeed);
    }

    public float motorSpeed() {
        return box3d_h.b3RevoluteJoint_GetMotorSpeed(id);
    }

    public void setMaxMotorTorque(float torque) {
        box3d_h.b3RevoluteJoint_SetMaxMotorTorque(id, torque);
    }

    public float maxMotorTorque() {
        return box3d_h.b3RevoluteJoint_GetMaxMotorTorque(id);
    }
}
