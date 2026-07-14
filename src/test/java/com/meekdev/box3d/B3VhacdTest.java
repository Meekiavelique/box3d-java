package com.meekdev.box3d;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class B3VhacdTest {

    @Test
    void lShapeSplitsAlongTheConcavity() throws Exception {
        String obj = """
                v 0 0 0
                v 2 0 0
                v 2 1 0
                v 1 1 0
                v 1 2 0
                v 0 2 0
                v 0 0 1
                v 2 0 1
                v 2 1 1
                v 1 1 1
                v 1 2 1
                v 0 2 1
                f 1 2 3
                f 1 3 4
                f 1 4 5
                f 1 5 6
                f 7 9 8
                f 7 10 9
                f 7 11 10
                f 7 12 11
                f 1 8 2
                f 1 7 8
                f 2 9 3
                f 2 8 9
                f 3 10 4
                f 3 9 10
                f 4 11 5
                f 4 10 11
                f 5 12 6
                f 5 11 12
                f 6 7 1
                f 6 12 7
                """;
        assumeTrue(NativeLoader.hasVhacd(), "vhacd native not packaged");
        B3Model model = B3Model.loadObj(new ByteArrayInputStream(obj.getBytes(StandardCharsets.UTF_8)));
        List<B3Hull> hulls = model.decomposeExact(4, 100_000, 32);
        assertTrue(hulls.size() >= 2, "l shape should split, got " + hulls.size());

        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body prop = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 3, 0));
            for (B3Hull hull : hulls) {
                prop.addHull(hull);
            }
            for (int i = 0; i < 240; i++) {
                world.step(1f / 60f, 4);
            }
            assertTrue(prop.position().y() < 2 && prop.position().y() > -0.5,
                    "prop rests at " + prop.position().y());
            hulls.forEach(B3Hull::close);
        }
    }
}
