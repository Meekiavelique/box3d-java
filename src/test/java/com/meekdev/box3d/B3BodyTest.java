package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3BodyTest {

    @Test
    void sphereRestsOnGroundBox() {
        try (B3World world = B3World.create(new Vec3(0, -9.8, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, 0, 0));
            ground.addBox(new Vec3(50, 1, 50));

            B3Body sphere = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            sphere.addSphere(0.5f);

            for (int i = 0; i < 120; i++) {
                world.step(1f / 60f, 4);
            }

            Vec3 pos = sphere.position();
            assertEquals(1.5, pos.y(), 0.1);

            Vec3 vel = sphere.linearVelocity();
            double speed = Math.sqrt(vel.x() * vel.x() + vel.y() * vel.y() + vel.z() * vel.z());
            assertTrue(speed < 0.1, "expected sphere to be at rest, got speed " + speed);
        }
    }
}
