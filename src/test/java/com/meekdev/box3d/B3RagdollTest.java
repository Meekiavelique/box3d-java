package com.meekdev.box3d;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class B3RagdollTest {

    private static final float G = 20f;
    private static final long BONE_CAT = 8L;
    private static final float DENSITY = 300f;

    @Test
    void ragdollLaunchesAndSettlesWithoutExploding() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
            ground.addBox(new Vec3(60, 1, 60), B3ShapeConfig.defaults().withFriction(0.9f));

            B3Body torso = bone(world, 0.00, 1.15, 0.0, 0.22, 0.35, 0.13);
            B3Body head  = bone(world, 0.00, 1.75, 0.0, 0.16, 0.16, 0.16);
            B3Body armL  = bone(world, 0.31, 1.15, 0.0, 0.09, 0.30, 0.09);
            B3Body armR  = bone(world,-0.31, 1.15, 0.0, 0.09, 0.30, 0.09);
            B3Body legL  = bone(world, 0.11, 0.40, 0.0, 0.10, 0.35, 0.10);
            B3Body legR  = bone(world,-0.11, 0.40, 0.0, 0.10, 0.35, 0.10);
            B3Body[] bones = {torso, head, armL, armR, legL, legR};

            joint(world, torso, head, new Vec3(0.00, 1.55, 0.0), 0.6f, -0.4f, 0.4f);
            joint(world, torso, armL, new Vec3(0.22, 1.45, 0.0), 1.4f, -1.2f, 1.2f);
            joint(world, torso, armR, new Vec3(-0.22,1.45, 0.0), 1.4f, -1.2f, 1.2f);
            joint(world, torso, legL, new Vec3(0.11, 0.80, 0.0), 1.0f, -0.6f, 0.6f);
            joint(world, torso, legR, new Vec3(-0.11,0.80, 0.0), 1.0f, -0.6f, 0.6f);

            for (B3Body b : bones) b.setLinearVelocity(new Vec3(6, 4, 0));
            torso.applyImpulseAt(new Vec3(40, 20, 0), new Vec3(0.0, 0.85, 0.10));

            float h = 1f / 60f;
            int restedFor = 0;
            for (int step = 0; step < 600; step++) {
                for (B3Body b : bones) b.applyForceToCenter(new Vec3(0, -G * b.mass(), 0));
                world.step(h, 4);
                if (maxSpeed(bones) < 0.4) { if (++restedFor > 30) break; } else restedFor = 0;
            }

            for (int i = 0; i < bones.length; i++) {
                Vec3 p = bones[i].position();
                assertTrue(Double.isFinite(p.x()) && Double.isFinite(p.y()) && Double.isFinite(p.z()),
                        "bone " + i + " went NaN");
                assertTrue(p.y() > -0.5 && p.y() < 6.0, "bone " + i + " flew away / sank: y=" + p.y());
            }
            assertTrue(maxSpeed(bones) < 0.5, "ragdoll never came to rest, maxSpeed=" + maxSpeed(bones));
            double headToTorso = head.position().sub(torso.position()).length();
            assertTrue(headToTorso < 1.5, "head separated from torso: " + headToTorso);
        }
    }

    @Test
    void gravityIsZeroInTheWorldSoBonesFallOnlyFromManualForce() {
        try (B3World world = B3World.create(new Vec3(0, 0, 0))) {
            B3Body b = bone(world, 0, 5, 0, 0.2, 0.2, 0.2);
            double y0 = b.position().y();
            for (int i = 0; i < 60; i++) world.step(1f / 60f, 4);
            assertEquals(y0, b.position().y(), 1e-3, "world applied gravity on its own");
        }
    }

    private static B3Body bone(B3World world, double x, double y, double z, double hx, double hy, double hz) {
        B3Body b = world.createBody(B3BodyType.DYNAMIC, new Vec3(x, y, z));
        b.addBox(new Vec3(hx, hy, hz),
                B3ShapeConfig.defaults().withDensity(DENSITY).withFriction(0.6f).withFilter(BONE_CAT, ~BONE_CAT));
        b.setAngularDamping(0.8f);
        return b;
    }

    private static void joint(B3World world, B3Body a, B3Body b, Vec3 pivot, float cone, float twLo, float twHi) {
        B3SphericalJoint j = world.createSphericalJoint(a, b, pivot);
        j.setConeLimit(cone);
        j.setTwistLimits(twLo, twHi);
        j.setSpring(8f, 0.8f);
    }

    private static double maxSpeed(B3Body[] bones) {
        double m = 0;
        for (B3Body b : bones) m = Math.max(m, b.linearVelocity().length());
        return m;
    }
}
