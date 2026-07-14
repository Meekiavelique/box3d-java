package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3MoverTest {

    @Test
    void slidesAlongGroundAndStopsAtWall() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(50, 1, 50));
            B3Body wall = world.createBody(B3BodyType.STATIC, new Vec3(5, 2, 0));
            wall.addBox(new Vec3(1, 2, 1));

            try (B3Mover mover = new B3Mover(world, 0.4f, 0.9f)) {
                B3Mover.MoveResult r = mover.move(new Vec3(0, 0.01, 0), new Vec3(1, -0.5, 0));
                assertTrue(r.grounded());
                assertTrue(r.groundNormal().y() > 0.9);
                assertTrue(r.position().x() > 0.5);
                assertEquals(0.0, r.position().y(), 0.05);

                Vec3 pos = r.position();
                for (int i = 0; i < 20; i++) {
                    r = mover.move(pos, new Vec3(0.5, 0, 0));
                    pos = r.position();
                }
                assertTrue(pos.x() < 4.0 - 0.4 + 0.05);
                assertTrue(pos.x() > 3.0);
            }
        }
    }

    @Test
    void climbsAStepAndRecoversFromOverlap() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(50, 1, 50));
            B3Body step = world.createBody(B3BodyType.STATIC, new Vec3(3, 0.25, 0));
            step.addBox(new Vec3(1, 0.25, 1));

            try (B3Mover mover = new B3Mover(world, 0.4f, 0.9f)) {
                Vec3 pos = new Vec3(0, 0, 0);
                for (int i = 0; i < 8; i++) {
                    pos = mover.move(pos, new Vec3(0.3, -0.08, 0), 0.6f).position();
                }
                assertTrue(pos.x() > 2.2, "did not climb, x=" + pos.x());
                assertEquals(0.5, pos.y(), 0.05);

                B3Mover.MoveResult r = mover.move(new Vec3(-5, -0.15, 0), new Vec3(0.3, 0, 0));
                assertTrue(r.position().y() > -0.05, "still embedded at y=" + r.position().y());
                r = mover.move(r.position(), new Vec3(0.3, -0.08, 0));
                assertTrue(r.grounded());
                assertTrue(r.position().x() > -4.8);
            }
        }
    }
}
