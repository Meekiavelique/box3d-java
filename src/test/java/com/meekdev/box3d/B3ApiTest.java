package com.meekdev.box3d;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3ApiTest {

    @Test
    void contactEventsFire() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 4, 0));
            ball.addSphere(0.5f);

            boolean touched = false;
            for (int i = 0; i < 120 && !touched; i++) {
                world.step(1f / 60f, 4);
                for (B3Events.ContactBegin begin : world.contactEvents().begins()) {
                    touched |= begin.bodyA() == ball || begin.bodyB() == ball;
                }
            }
            assertTrue(touched);
            assertFalse(world.bodyEvents().isEmpty() && !touched);
        }
    }

    @Test
    void distanceJointHolds() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body anchor = world.createBody(B3BodyType.STATIC, new Vec3(0, 10, 0));
            anchor.addSphere(0.1f);
            B3Body weight = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 8, 0));
            weight.addSphere(0.25f);

            B3DistanceJoint joint = world.createDistanceJoint(anchor, weight,
                    new Vec3(0, 10, 0), new Vec3(0, 8, 0), 2f);
            assertTrue(joint.isValid());
            for (int i = 0; i < 300; i++) {
                world.step(1f / 60f, 4);
            }
            assertEquals(8.0, weight.position().y(), 0.2);
            assertEquals(2.0, joint.currentLength(), 0.2);
        }
    }

    @Test
    void heightfieldAndOverlapAndThreads() {
        float[] heights = new float[32 * 32];
        for (int z = 0; z < 32; z++) {
            for (int x = 0; x < 32; x++) {
                heights[x + z * 32] = 0.5f * (float) Math.sin(x * 0.4) ;
            }
        }
        try (B3World world = B3World.create(new Vec3(0, -10, 0), 4);
             B3Heightfield field = B3Heightfield.bake(heights, 32, 32, new Vec3(1, 1, 1))) {
            B3Body terrain = world.createBody(B3BodyType.STATIC, new Vec3(0, 0, 0));
            terrain.addHeightField(field);

            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(16, 5, 16));
            ball.addSphere(0.5f);
            for (int i = 0; i < 240; i++) {
                world.step(1f / 60f, 4);
            }
            assertTrue(ball.position().y() < 2.0 && ball.position().y() > -1.0,
                    "rests on terrain, y=" + ball.position().y());

            List<B3Body> around = world.overlapAABB(
                    ball.position().add(-1, -1, -1), ball.position().add(1, 1, 1));
            assertTrue(around.contains(ball));
            assertTrue(around.contains(terrain));
        }
    }
}
