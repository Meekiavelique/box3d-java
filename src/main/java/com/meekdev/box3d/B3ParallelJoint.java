package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.MemorySegment;

// keeps two frames rotationally aligned, translation stays free
public final class B3ParallelJoint extends B3Joint {

    B3ParallelJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    public void setSpring(float hertz, float dampingRatio) {
        box3d_h.b3ParallelJoint_SetSpringHertz(id, hertz);
        box3d_h.b3ParallelJoint_SetSpringDampingRatio(id, dampingRatio);
    }

    public void setMaxTorque(float torque) {
        box3d_h.b3ParallelJoint_SetMaxTorque(id, torque);
    }

    public float springHertz() {
        return box3d_h.b3ParallelJoint_GetSpringHertz(id);
    }

    public float springDampingRatio() {
        return box3d_h.b3ParallelJoint_GetSpringDampingRatio(id);
    }

    public float maxTorque() {
        return box3d_h.b3ParallelJoint_GetMaxTorque(id);
    }
}
