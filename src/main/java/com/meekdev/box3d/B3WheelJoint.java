package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.MemorySegment;

// wheel with suspension, steering and a spin motor
public final class B3WheelJoint extends B3Joint {

    B3WheelJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public void setSuspension(float hertz, float dampingRatio, float lower, float upper) {
        box3d_h.b3WheelJoint_SetSuspensionHertz(id, hertz);
        box3d_h.b3WheelJoint_SetSuspensionDampingRatio(id, dampingRatio);
        box3d_h.b3WheelJoint_SetSuspensionLimits(id, lower, upper);
        box3d_h.b3WheelJoint_EnableSuspension(id, true);
        box3d_h.b3WheelJoint_EnableSuspensionLimit(id, true);
    }

    public void enableSuspension(boolean flag) {
        box3d_h.b3WheelJoint_EnableSuspension(id, flag);
    }

    public boolean suspensionEnabled() {
        return box3d_h.b3WheelJoint_IsSuspensionEnabled(id);
    }

    public void setSuspensionHertz(float hertz) {
        box3d_h.b3WheelJoint_SetSuspensionHertz(id, hertz);
    }

    public float suspensionHertz() {
        return box3d_h.b3WheelJoint_GetSuspensionHertz(id);
    }

    public void setSuspensionDampingRatio(float dampingRatio) {
        box3d_h.b3WheelJoint_SetSuspensionDampingRatio(id, dampingRatio);
    }

    public float suspensionDampingRatio() {
        return box3d_h.b3WheelJoint_GetSuspensionDampingRatio(id);
    }

    public void enableSuspensionLimit(boolean flag) {
        box3d_h.b3WheelJoint_EnableSuspensionLimit(id, flag);
    }

    public boolean suspensionLimitEnabled() {
        return box3d_h.b3WheelJoint_IsSuspensionLimitEnabled(id);
    }

    public float lowerSuspensionLimit() {
        return box3d_h.b3WheelJoint_GetLowerSuspensionLimit(id);
    }

    public float upperSuspensionLimit() {
        return box3d_h.b3WheelJoint_GetUpperSuspensionLimit(id);
    }

    public void setSuspensionLimits(float lower, float upper) {
        box3d_h.b3WheelJoint_SetSuspensionLimits(id, lower, upper);
    }

    public void setSpinMotor(float speed, float maxTorque) {
        box3d_h.b3WheelJoint_SetSpinMotorSpeed(id, speed);
        box3d_h.b3WheelJoint_SetMaxSpinTorque(id, maxTorque);
        box3d_h.b3WheelJoint_EnableSpinMotor(id, true);
        wakeBodies();
    }

    public void enableSpinMotor(boolean flag) {
        box3d_h.b3WheelJoint_EnableSpinMotor(id, flag);
    }

    public boolean spinMotorEnabled() {
        return box3d_h.b3WheelJoint_IsSpinMotorEnabled(id);
    }

    public void setSpinMotorSpeed(float speed) {
        box3d_h.b3WheelJoint_SetSpinMotorSpeed(id, speed);
    }

    public float spinMotorSpeed() {
        return box3d_h.b3WheelJoint_GetSpinMotorSpeed(id);
    }

    public void setMaxSpinTorque(float torque) {
        box3d_h.b3WheelJoint_SetMaxSpinTorque(id, torque);
    }

    public float maxSpinTorque() {
        return box3d_h.b3WheelJoint_GetMaxSpinTorque(id);
    }

    public float spinSpeed() {
        return box3d_h.b3WheelJoint_GetSpinSpeed(id);
    }

    public float spinTorque() {
        return box3d_h.b3WheelJoint_GetSpinTorque(id);
    }

    public void setSteering(float targetAngle, float maxTorque) {
        box3d_h.b3WheelJoint_SetTargetSteeringAngle(id, targetAngle);
        box3d_h.b3WheelJoint_SetMaxSteeringTorque(id, maxTorque);
        box3d_h.b3WheelJoint_EnableSteering(id, true);
        wakeBodies();
    }

    public void enableSteering(boolean flag) {
        box3d_h.b3WheelJoint_EnableSteering(id, flag);
    }

    public boolean steeringEnabled() {
        return box3d_h.b3WheelJoint_IsSteeringEnabled(id);
    }

    public void setSteeringHertz(float hertz) {
        box3d_h.b3WheelJoint_SetSteeringHertz(id, hertz);
    }

    public float steeringHertz() {
        return box3d_h.b3WheelJoint_GetSteeringHertz(id);
    }

    public void setSteeringDampingRatio(float dampingRatio) {
        box3d_h.b3WheelJoint_SetSteeringDampingRatio(id, dampingRatio);
    }

    public float steeringDampingRatio() {
        return box3d_h.b3WheelJoint_GetSteeringDampingRatio(id);
    }

    public void setMaxSteeringTorque(float torque) {
        box3d_h.b3WheelJoint_SetMaxSteeringTorque(id, torque);
    }

    public float maxSteeringTorque() {
        return box3d_h.b3WheelJoint_GetMaxSteeringTorque(id);
    }

    public void enableSteeringLimit(boolean flag) {
        box3d_h.b3WheelJoint_EnableSteeringLimit(id, flag);
    }

    public boolean steeringLimitEnabled() {
        return box3d_h.b3WheelJoint_IsSteeringLimitEnabled(id);
    }

    public float lowerSteeringLimit() {
        return box3d_h.b3WheelJoint_GetLowerSteeringLimit(id);
    }

    public float upperSteeringLimit() {
        return box3d_h.b3WheelJoint_GetUpperSteeringLimit(id);
    }

    public void setSteeringLimits(float lowerRadians, float upperRadians) {
        box3d_h.b3WheelJoint_SetSteeringLimits(id, lowerRadians, upperRadians);
    }

    public void setTargetSteeringAngle(float radians) {
        box3d_h.b3WheelJoint_SetTargetSteeringAngle(id, radians);
    }

    public float targetSteeringAngle() {
        return box3d_h.b3WheelJoint_GetTargetSteeringAngle(id);
    }

    public float steeringAngle() {
        return box3d_h.b3WheelJoint_GetSteeringAngle(id);
    }

    public float steeringTorque() {
        return box3d_h.b3WheelJoint_GetSteeringTorque(id);
    }
}
