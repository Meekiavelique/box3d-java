package com.meekdev.box3d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class B3IntrospectionTest {

    @Test
    void shapeReadsBackItsOwnGeometryAndBody() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body body = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            B3Shape shape = body.addSphere(0.5f);

            assertEquals(B3ShapeType.SPHERE, shape.type());
            assertEquals(0.5f, shape.sphere().radius(), 1e-4);
            assertSame(body, shape.body());

            assertEquals(1, body.shapeCount());
            assertEquals(1, body.shapes().size());
            assertEquals(B3ShapeType.SPHERE, body.shapes().get(0).type());

            Vec3[] aabb = shape.aabb();
            assertTrue(Double.isFinite(aabb[0].y()) && Double.isFinite(aabb[1].y()));
            assertTrue(aabb[1].y() > aabb[0].y());
        }
    }

    @Test
    void closestPointAndRayCastFindTheSphere() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body body = world.createBody(B3BodyType.STATIC, new Vec3(0, 5, 0));
            B3Shape shape = body.addSphere(0.5f);

            Vec3 near = shape.closestPoint(new Vec3(0, 10, 0));
            assertTrue(near.y() > 5.0 && near.y() < 6.0, "closest point off the sphere: " + near.y());

            B3World.RayHit hit = shape.rayCast(new Vec3(0, 5, 5), new Vec3(0, 0, -10));
            assertTrue(hit.hit(), "ray missed the sphere");
            assertSame(body, hit.body());
            assertTrue(hit.point().z() > 0.0, "hit point on the wrong side: " + hit.point().z());
        }
    }

    @Test
    void capsuleAndMaterialTakeLiveEdits() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body body = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            B3Shape shape = body.addCapsule(new Vec3(0, -0.4, 0), new Vec3(0, 0.4, 0), 0.2f);

            assertEquals(B3ShapeType.CAPSULE, shape.type());
            assertEquals(0.2f, shape.capsule().radius(), 1e-4);

            shape.setCapsule(new B3Capsule(new Vec3(0, -0.6, 0), new Vec3(0, 0.6, 0), 0.3f));
            assertEquals(0.3f, shape.capsule().radius(), 1e-4);

            shape.setSurfaceMaterial(new B3SurfaceMat(0.7f, 0.4f, 0.1f));
            assertEquals(0.7f, shape.surfaceMaterial().friction(), 1e-4);
            assertEquals(0.4f, shape.surfaceMaterial().restitution(), 1e-4);
        }
    }

    @Test
    void userDataRoundTripsOnBodyShapeAndJoint() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body a = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            B3Body b = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 6, 0));
            B3Shape shape = a.addSphere(0.3f);
            B3SphericalJoint joint = world.createSphericalJoint(a, b, new Vec3(0, 5.5, 0));

            a.setUserData(42L);
            shape.setUserData(7L);
            joint.setUserData(99L);
            assertEquals(42L, a.userData());
            assertEquals(7L, shape.userData());
            assertEquals(99L, joint.userData());
        }
    }

    @Test
    void jointReportsItsTypeBodiesAndReaction() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body anchor = world.createBody(B3BodyType.STATIC, new Vec3(0, 6, 0));
            B3Body hanging = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            hanging.addSphere(0.3f);
            B3SphericalJoint joint = world.createSphericalJoint(anchor, hanging, new Vec3(0, 6, 0));

            assertEquals(B3JointType.SPHERICAL, joint.type());
            assertSame(anchor, joint.bodyA());
            assertSame(hanging, joint.bodyB());

            joint.setForceThreshold(1234f);
            assertEquals(1234f, joint.forceThreshold(), 1e-2);

            for (int i = 0; i < 10; i++) world.step(1f / 60f, 4);
            assertNotNull(joint.constraintForce());
            assertTrue(Double.isFinite(joint.constraintForce().y()));
        }
    }
}
