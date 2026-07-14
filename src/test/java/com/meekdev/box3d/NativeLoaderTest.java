package com.meekdev.box3d;

import com.meekdev.box3d.ffi.box3d_h;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NativeLoaderTest {

    @Test
    void loadsAndCalls() {
        NativeLoader.load();
        assertTrue(box3d_h.b3IsDoublePrecision());
    }

    @Test
    void loadIsIdempotent() {
        NativeLoader.load();
        NativeLoader.load();
        assertTrue(box3d_h.b3IsDoublePrecision());
    }
}
