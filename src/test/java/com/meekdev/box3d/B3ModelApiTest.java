package com.meekdev.box3d;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3ModelApiTest {

    private static final String L_SHAPE_OBJ = """
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

    @Test
    void objLoadsAndDecomposes() throws Exception {
        B3Model model = B3Model.loadObj(new ByteArrayInputStream(L_SHAPE_OBJ.getBytes(StandardCharsets.UTF_8)));
        assertEquals(12, model.vertices().length / 3);
        assertTrue(model.indices().length / 3 >= 20);

        List<B3Hull> hulls = model.decompose(2, 16);
        assertTrue(hulls.size() >= 1 && hulls.size() <= 2);

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

    @Test
    void hullTransformsAndMassOverride() {
        try (B3Hull rock = B3Hull.rock(0.5f);
             B3Hull big = rock.scaled(2.0);
             B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body body = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 3, 0));
            body.addHull(big);
            float derived = body.mass();
            assertTrue(derived > 0);
            body.setMass(derived * 10, Vec3.zero);
            assertEquals(derived * 10, body.mass(), derived * 0.01);
            body.recomputeMass();
            assertEquals(derived, body.mass(), derived * 0.01);
        }
    }

    @Test
    void dynamicMeshVersusMesh() {
        float[] verts = {-1, 0, -1, 1, 0, -1, 1, 0, 1, -1, 0, 1};
        int[] idx = {0, 2, 1, 0, 3, 2};
        try (B3Mesh mesh = B3Mesh.bake(verts, idx);
             B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body lower = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 1, 0));
            lower.addMesh(mesh);
            B3Body upper = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 4, 0));
            upper.addMesh(mesh);
            for (int i = 0; i < 240; i++) {
                world.step(1f / 60f, 4);
            }
            System.out.println("mesh vs mesh rest: lower=" + lower.position().y()
                    + " upper=" + upper.position().y());
        }
    }

    @Test
    void frictionMixerRuns() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            boolean[] called = {false};
            world.setFrictionMixer((a, ida, b, idb) -> {
                called[0] = true;
                return Math.min(a, b);
            });
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 2, 0));
            ball.addSphere(0.4f);
            for (int i = 0; i < 120; i++) {
                world.step(1f / 60f, 4);
            }
            assertTrue(called[0]);
        }
    }
}
