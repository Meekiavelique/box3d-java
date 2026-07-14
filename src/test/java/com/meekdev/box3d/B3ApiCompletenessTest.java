package com.meekdev.box3d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class B3ApiCompletenessTest {

    @Test
    void bodyNameTransformAndLocks() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body body = world.createBody(B3BodyType.DYNAMIC, new Vec3(1, 2, 3));
            body.addSphere(0.5f);

            body.setName("chassis");
            assertEquals("chassis", body.name());

            B3Transform t = body.transform();
            assertEquals(2.0, t.position().y(), 1e-6);

            body.setMotionLocks(true, false, true, false, true, false);
            boolean[] locks = body.motionLocks();
            assertTrue(locks[0] && !locks[1] && locks[2] && !locks[3] && locks[4] && !locks[5]);

            Vec3 wp = body.worldPoint(new Vec3(0, 0, 0));
            assertEquals(2.0, wp.y(), 1e-6);
            assertTrue(Double.isFinite(body.worldCenterOfMass().y()));
        }
    }

    @Test
    void bodyForcesEnableStateAndJoints() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body a = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            a.addSphere(0.5f);
            B3Body b = world.createBody(B3BodyType.STATIC, new Vec3(0, 6, 0));
            B3SphericalJoint joint = world.createSphericalJoint(b, a, new Vec3(0, 6, 0));

            assertEquals(1, a.jointCount());
            assertEquals(B3JointType.SPHERICAL, a.joints().get(0).type());

            assertTrue(a.isEnabled());
            a.disable();
            assertFalse(a.isEnabled());
            a.enable();

            B3Body free = world.createBody(B3BodyType.DYNAMIC, new Vec3(5, 5, 0));
            free.addSphere(0.5f);
            free.applyForce(new Vec3(0, 100, 0), free.position());
            world.step(1f / 60f, 4);
            assertTrue(free.linearVelocity().y() > 0, "force did not push the body up");

            joint.destroy();
        }
    }

    @Test
    void worldTogglesThresholdsAndUserData() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0)).addSphere(0.5f);
            world.step(1f / 60f, 4);

            world.enableContinuous(false);
            assertFalse(world.continuousEnabled());
            world.enableSleeping(true);
            assertTrue(world.sleepingEnabled());

            world.setHitEventThreshold(3.5f);
            assertEquals(3.5f, world.hitEventThreshold(), 1e-4);
            world.setRestitutionThreshold(1.25f);
            assertEquals(1.25f, world.restitutionThreshold(), 1e-4);

            world.setUserData(4242L);
            assertEquals(4242L, world.userData());

            assertTrue(world.workerCount() >= 1);
            assertTrue(Double.isFinite(world.bounds()[1].y()));
        }
    }

    @Test
    void jointFramesTuningAndWheelAccessors() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body chassis = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
            chassis.addBox(new Vec3(1, 0.3, 2));
            B3Body wheel = world.createBody(B3BodyType.DYNAMIC, new Vec3(0.9, 4.6, 1.2));
            wheel.addSphere(0.4f);

            B3WheelJoint wj = world.createWheelJoint(chassis, wheel,
                    new Vec3(0.9, 4.6, 1.2), new Vec3(0, 1, 0), new Vec3(1, 0, 0));

            wj.enableSuspension(true);
            wj.setSuspensionHertz(4.5f);
            assertEquals(4.5f, wj.suspensionHertz(), 1e-4);
            assertTrue(wj.suspensionEnabled());

            wj.setConstraintTuning(6f, 0.7f);
            float[] tuning = wj.constraintTuning();
            assertEquals(6f, tuning[0], 1e-3);
            assertEquals(0.7f, tuning[1], 1e-3);

            B3Transform frameA = wj.localFrameA();
            wj.setLocalFrameA(frameA);
            assertEquals(frameA.position().x(), wj.localFrameA().position().x(), 1e-5);
        }
    }
}
