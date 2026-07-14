package com.meekdev.box3d;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3ApiSurfaceTest {

    @Test
    void sensorSeesVisitor() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body zone = world.createBody(B3BodyType.STATIC, new Vec3(0, 1, 0));
            zone.addBox(new Vec3(2, 2, 2), B3ShapeConfig.defaults().asSensor());
            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 6, 0));
            ball.addSphere(0.4f);

            boolean seen = false;
            for (int i = 0; i < 180 && !seen; i++) {
                world.step(1f / 60f, 4);
                for (B3World.SensorTouch touch : world.sensorEvents()[0]) {
                    seen |= touch.sensor() == zone && touch.visitor() == ball;
                }
            }
            assertTrue(seen);
        }
    }

    @Test
    void filteredRayAndSphereCast() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body wall = world.createBody(B3BodyType.STATIC, new Vec3(5, 0, 0));
            wall.addBox(new Vec3(0.5, 3, 3), B3ShapeConfig.defaults().withFilter(8L, ~0L));

            B3World.RayHit hit = world.castRayClosest(new Vec3(0, 0, 0), new Vec3(10, 0, 0));
            assertTrue(hit.hit());
            assertEquals(wall, hit.body());

            B3World.RayHit miss = world.castRayClosest(new Vec3(0, 0, 0), new Vec3(10, 0, 0),
                    B3Filter.everything.hitting(~8L));
            assertFalse(miss.hit());

            B3World.ShapeHit sweep = world.castSphereClosest(new Vec3(0, 0, 0), 0.5f,
                    new Vec3(10, 0, 0), B3Filter.everything);
            assertTrue(sweep.hit());
            assertEquals(wall, sweep.body());
            assertTrue(sweep.fraction() < 0.45);
        }
    }

    @Test
    void compoundAndLocksAndReplay() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0));
             B3Compound decor = new B3Compound()
                     .addBox(new Vec3(0, 0.5, 4), new Vec3(1, 0.5, 1))
                     .addSphere(new Vec3(0, 1.5, 4), 0.5f)
                     .bake();
             B3Recording recording = B3Recording.create()) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body statue = world.createBody(B3BodyType.STATIC, new Vec3(0, 0, 0));
            statue.addCompound(decor);

            world.startRecording(recording);

            B3Body weights = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 3, 0));
            weights.addSphereAt(new Vec3(-1, 0, 0), 0.4f);
            weights.addSphereAt(new Vec3(1, 0, 0), 0.4f);
            weights.addBoxAt(new Vec3(0, 0, 0), new Vec3(0.8, 0.1, 0.1));
            weights.setMotionLocks(false, false, true, false, false, false);

            for (int i = 0; i < 120; i++) {
                world.step(1f / 60f, 4);
            }
            world.stopRecording();

            assertTrue(weights.position().y() < 1.5);
            assertEquals(0.0, weights.position().z(), 1e-6);

            try (B3Replay replay = B3Replay.of(recording)) {
                assertTrue(replay.frameCount() > 100);
                while (replay.stepFrame()) {
                }
                assertFalse(replay.hasDiverged());
            }
        }
    }

    @Test
    void contactListenerAndCustomFilter() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 3, 0));
            ball.addSphere(0.4f);

            boolean[] touched = {false};
            world.onContactBegin(begin -> touched[0] = true);
            world.setContactFilter((a, b) -> a != ball && b != ball);

            for (int i = 0; i < 120; i++) {
                world.step(1f / 60f, 4);
            }
            assertFalse(touched[0]);
            assertTrue(ball.position().y() < -2);
            assertNotNull(world.profile());
            assertTrue(world.counters().bodies() >= 2);
        }
    }
}
