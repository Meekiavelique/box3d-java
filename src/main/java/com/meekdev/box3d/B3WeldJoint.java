package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.MemorySegment;

public final class B3WeldJoint extends B3Joint {

    B3WeldJoint(B3World world, MemorySegment id) {
        super(world, id);
    }

    // zero hertz is fully rigid, otherwise the weld behaves like a spring
    public void setLinearSpring(float hertz, float dampingRatio) {
        box3d_h.b3WeldJoint_SetLinearHertz(id, hertz);
        box3d_h.b3WeldJoint_SetLinearDampingRatio(id, dampingRatio);
    }

    public void setAngularSpring(float hertz, float dampingRatio) {
        box3d_h.b3WeldJoint_SetAngularHertz(id, hertz);
        box3d_h.b3WeldJoint_SetAngularDampingRatio(id, dampingRatio);
    }

    public float linearHertz() {
        return box3d_h.b3WeldJoint_GetLinearHertz(id);
    }

    public float linearDampingRatio() {
        return box3d_h.b3WeldJoint_GetLinearDampingRatio(id);
    }

    public float angularHertz() {
        return box3d_h.b3WeldJoint_GetAngularHertz(id);
    }

    public float angularDampingRatio() {
        return box3d_h.b3WeldJoint_GetAngularDampingRatio(id);
    }
}
