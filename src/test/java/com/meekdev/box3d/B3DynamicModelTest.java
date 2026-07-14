package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class B3DynamicModelTest {

    @Test
    void hullFromPointsRollsAround() {
        float[] points = new float[8 * 3 * 4];
        int n = 0;
        for (int i = 0; i < 32; i++) {
            double a = i * 0.61, b = i * 1.13;
            points[n++] = (float) (Math.cos(a) * Math.sin(b) * (0.5 + 0.1 * (i % 3)));
            points[n++] = (float) (Math.cos(b) * (0.5 + 0.07 * (i % 5)));
            points[n++] = (float) (Math.sin(a) * Math.sin(b) * (0.5 + 0.1 * (i % 4)));
        }
        try (B3World world = B3World.create(new Vec3(0, -10, 0));
             B3Hull hull = B3Hull.bake(points, 32)) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(20, 1, 20));
            B3Body rock = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 4, 0));
            rock.addHull(hull);
            for (int i = 0; i < 240; i++) {
                world.step(1f / 60f, 4);
            }
            assertTrue(rock.position().y() < 1.0 && rock.position().y() > 0.1,
                    "rock rests at " + rock.position().y());
        }
    }

    @Test
    void kinematicMeshCarriesABall() {
        float[] verts = {-3, 0, -3, 3, 0, -3, 3, 0, 3, -3, 0, 3};
        int[] idx = {0, 2, 1, 0, 3, 2};
        try (B3Mesh mesh = B3Mesh.bake(verts, idx);
             B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body platform = world.createBody(B3BodyType.KINEMATIC, new Vec3(0, 2, 0));
            platform.addMesh(mesh);
            B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 4, 0));
            ball.addSphere(0.4f);

            for (int i = 0; i < 240; i++) {
                platform.setTargetTransform(new Vec3(0, 2 + i * 0.01, 0), Quat.identity, 1f / 60f);
                world.step(1f / 60f, 4);
            }
            assertTrue(ball.position().y() > 4.5, "ball fell through, y=" + ball.position().y());
        }
    }
}
