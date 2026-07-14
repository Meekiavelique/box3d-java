package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class B3MeshTest {

    @Test
    void raycastHitsBakedQuad() {
        float[] verts = {
                -10, 0, -10,
                10, 0, -10,
                10, 0, 10,
                -10, 0, 10,
        };
        int[] indices = {0, 2, 1, 0, 3, 2};

        try (B3Mesh mesh = B3Mesh.bake(verts, indices);
             B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, 0, 0));
            ground.addMesh(mesh);

            B3World.RayHit hit = world.castRayClosest(new Vec3(0, 5, 0), new Vec3(0, -10, 0));
            assertTrue(hit.hit());
            assertEquals(0.0, hit.point().y(), 1e-3);
            assertTrue(hit.normal().y() > 0.99);

            B3Body sphere = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 3, 0));
            sphere.addSphere(0.5f);
            for (int i = 0; i < 180; i++) {
                world.step(1f / 60f, 4);
            }
            assertEquals(0.5, sphere.position().y(), 0.1);
        }
    }
}
