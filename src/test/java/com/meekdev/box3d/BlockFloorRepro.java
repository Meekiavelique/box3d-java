package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockFloorRepro {

    @Test
    void walksAcrossSectionFloors() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            for (int sx = -16; sx <= 16; sx += 16) {
                for (int sy = 16; sy <= 48; sy += 16) {
                    B3Body section = world.createBody(B3BodyType.STATIC, new Vec3(sx - 160, sy, 208));
                    section.addBoxAt(new Vec3(8, 8, 8), new Vec3(8, 8, 8));
                }
            }

            try (B3Mover mover = new B3Mover(world, 0.3f, 0.9f)) {
                Vec3 pos = new Vec3(-155.5, 64.0, 216.5);
                double startX = pos.x();
                boolean groundedSeen = false;
                for (int i = 0; i < 40; i++) {
                    B3Mover.MoveResult r = mover.move(pos, new Vec3(0.2, -0.08, 0));
                    pos = r.position();
                    groundedSeen |= r.grounded();
                }
                assertTrue(groundedSeen, "never grounded");
                assertTrue(pos.x() - startX > 4.0, "only moved " + (pos.x() - startX));
                assertTrue(Math.abs(pos.y() - 64.0) < 0.1, "sank or floated to " + pos.y());
            }
        }
    }
}
