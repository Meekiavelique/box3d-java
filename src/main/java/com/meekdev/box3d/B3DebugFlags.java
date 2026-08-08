package com.meekdev.box3d;

public record B3DebugFlags(boolean shapes, boolean joints, boolean bounds,
                           boolean contacts, boolean contactNormals) {

    public static B3DebugFlags shapesOnly() {
        return new B3DebugFlags(true, false, false, false, false);
    }

    public static B3DebugFlags shapesAndJoints() {
        return new B3DebugFlags(true, true, false, false, false);
    }
}
