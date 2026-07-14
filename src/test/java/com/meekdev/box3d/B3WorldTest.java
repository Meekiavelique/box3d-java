package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3WorldTest {

    @Test
    void createStepAndClose() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            assertTrue(world.isValid());
            for (int i = 0; i < 60; i++) {
                world.step(1f / 60f, 4);
            }
        }
    }

    @Test
    void closeInvalidatesWorld() {
        B3World world = B3World.create(new Vec3(0, -10, 0));
        world.close();
        assertFalse(world.isValid());
    }

    @Test
    void twoWorldsCanCoexist() {
        try (B3World a = B3World.create(new Vec3(0, -10, 0));
             B3World b = B3World.create(new Vec3(0, -1, 0))) {
            assertTrue(a.isValid());
            assertTrue(b.isValid());
        }
    }
}
