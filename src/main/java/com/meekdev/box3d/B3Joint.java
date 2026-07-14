package com.meekdev.box3d;

import com.meekdev.box3d.ffi.b3Quat;
import com.meekdev.box3d.ffi.b3Transform;
import com.meekdev.box3d.ffi.b3Vec3;
import com.meekdev.box3d.ffi.box3d_h;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

// handle around a native b3JointId, typed accessors live on the subclasses
public class B3Joint {

    final B3World world;
    final MemorySegment id;

    B3Joint(B3World world, MemorySegment id) {
        this.world = world;
        this.id = id;
    }

    public boolean isValid() {
        return box3d_h.b3Joint_IsValid(id);
    }

    public B3JointType type() {
        return B3JointType.values()[box3d_h.b3Joint_GetType(id)];
    }

    public B3Body bodyA() {
        return world.bodyByKey(B3World.bodyKey(box3d_h.b3Joint_GetBodyA(world.scratch(), id)));
    }

    public B3Body bodyB() {
        return world.bodyByKey(B3World.bodyKey(box3d_h.b3Joint_GetBodyB(world.scratch(), id)));
    }

    public boolean collideConnected() {
        return box3d_h.b3Joint_GetCollideConnected(id);
    }

    public void setCollideConnected(boolean collide) {
        box3d_h.b3Joint_SetCollideConnected(id, collide);
    }

    // reaction force the constraint pulled with last step, world axes
    public Vec3 constraintForce() {
        MemorySegment f = box3d_h.b3Joint_GetConstraintForce(world.scratch(), id);
        return new Vec3(b3Vec3.x(f), b3Vec3.y(f), b3Vec3.z(f));
    }

    public Vec3 constraintTorque() {
        MemorySegment t = box3d_h.b3Joint_GetConstraintTorque(world.scratch(), id);
        return new Vec3(b3Vec3.x(t), b3Vec3.y(t), b3Vec3.z(t));
    }

    // how far the anchors have drifted apart, a solver quality readout
    public float linearSeparation() {
        return box3d_h.b3Joint_GetLinearSeparation(id);
    }

    public float angularSeparation() {
        return box3d_h.b3Joint_GetAngularSeparation(id);
    }

    // soft constraint stiffness, hertz zero means rigid
    public void setConstraintTuning(float hertz, float dampingRatio) {
        box3d_h.b3Joint_SetConstraintTuning(id, hertz, dampingRatio);
    }

    // snap the joint once its reaction force passes this, shows up in jointEvents
    public void setForceThreshold(float threshold) {
        box3d_h.b3Joint_SetForceThreshold(id, threshold);
    }

    public float forceThreshold() {
        return box3d_h.b3Joint_GetForceThreshold(id);
    }

    public void setTorqueThreshold(float threshold) {
        box3d_h.b3Joint_SetTorqueThreshold(id, threshold);
    }

    public float torqueThreshold() {
        return box3d_h.b3Joint_GetTorqueThreshold(id);
    }

    public void setUserData(long userData) {
        box3d_h.b3Joint_SetUserData(id, MemorySegment.ofAddress(userData));
    }

    public long userData() {
        return box3d_h.b3Joint_GetUserData(id).address();
    }

    // the joint frame on body a, measured from that body's origin
    public B3Transform localFrameA() {
        return readFrame(box3d_h.b3Joint_GetLocalFrameA(world.scratch(), id));
    }

    public B3Transform localFrameB() {
        return readFrame(box3d_h.b3Joint_GetLocalFrameB(world.scratch(), id));
    }

    public void setLocalFrameA(B3Transform frame) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3Joint_SetLocalFrameA(id, writeFrame(temp, frame));
        }
    }

    public void setLocalFrameB(B3Transform frame) {
        try (Arena temp = Arena.ofConfined()) {
            box3d_h.b3Joint_SetLocalFrameB(id, writeFrame(temp, frame));
        }
    }

    // current soft constraint tuning, hertz then damping ratio
    public float[] constraintTuning() {
        try (Arena temp = Arena.ofConfined()) {
            MemorySegment hertz = temp.allocate(ValueLayout.JAVA_FLOAT);
            MemorySegment damping = temp.allocate(ValueLayout.JAVA_FLOAT);
            box3d_h.b3Joint_GetConstraintTuning(id, hertz, damping);
            return new float[] {hertz.get(ValueLayout.JAVA_FLOAT, 0), damping.get(ValueLayout.JAVA_FLOAT, 0)};
        }
    }

    private static B3Transform readFrame(MemorySegment frame) {
        MemorySegment p = b3Transform.p(frame);
        MemorySegment q = b3Transform.q(frame);
        MemorySegment v = b3Quat.v(q);
        return new B3Transform(
                new Vec3(b3Vec3.x(p), b3Vec3.y(p), b3Vec3.z(p)),
                new Quat(b3Vec3.x(v), b3Vec3.y(v), b3Vec3.z(v), b3Quat.s(q)));
    }

    private static MemorySegment writeFrame(Arena arena, B3Transform frame) {
        MemorySegment seg = b3Transform.allocate(arena);
        MemorySegment p = b3Transform.p(seg);
        b3Vec3.x(p, (float) frame.position().x());
        b3Vec3.y(p, (float) frame.position().y());
        b3Vec3.z(p, (float) frame.position().z());
        MemorySegment q = b3Transform.q(seg);
        MemorySegment v = b3Quat.v(q);
        b3Vec3.x(v, frame.rotation().x());
        b3Vec3.y(v, frame.rotation().y());
        b3Vec3.z(v, frame.rotation().z());
        b3Quat.s(q, frame.rotation().s());
        return seg;
    }

    public void wakeBodies() {
        box3d_h.b3Joint_WakeBodies(id);
    }

    public void destroy() {
        box3d_h.b3DestroyJoint(id, true);
    }
}
