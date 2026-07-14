package com.meekdev.box3d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class B3CarTest {

    @Test
    void cartDrivesForward() {
        try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(200, 1, 20), B3ShapeConfig.defaults().withFriction(0.9f));

            B3Body chassis = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 0.8, 0));
            chassis.addBox(new Vec3(1.1, 0.25, 0.6), B3ShapeConfig.defaults().withDensity(400));

            double[][] anchors = {{0.8, 0.45, 0.7}, {0.8, 0.45, -0.7}, {-0.8, 0.45, 0.7}, {-0.8, 0.45, -0.7}};
            B3WheelJoint[] wheels = new B3WheelJoint[4];
            for (int i = 0; i < 4; i++) {
                Vec3 at = new Vec3(anchors[i][0], anchors[i][1], anchors[i][2]);
                B3Body wheel = world.createBody(B3BodyType.DYNAMIC, at);
                wheel.addSphere(0.35f, B3ShapeConfig.defaults().withFriction(1.2f)
                        .withFilter(2L, ~2L));
                wheels[i] = world.createWheelJoint(chassis, wheel, at,
                        new Vec3(0, 1, 0), new Vec3(0, 0, 1));
                wheels[i].setSuspension(4f, 0.7f, -0.15f, 0.15f);
            }

            for (int i = 0; i < 60; i++) {
                world.step(1f / 60f, 4);
            }
            double startX = chassis.position().x();

            for (B3WheelJoint wheel : wheels) {
                wheel.setSpinMotor(-20f, 400f);
            }
            for (int i = 0; i < 240; i++) {
                world.step(1f / 60f, 4);
            }
            double driven = chassis.position().x() - startX;
            assertTrue(Math.abs(driven) > 3.0, "cart barely moved: " + driven);
            assertTrue(chassis.position().y() > 0.2, "chassis collapsed to " + chassis.position().y());
        }
    }
}
