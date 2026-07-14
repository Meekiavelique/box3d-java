<h1 align="center">
 box3d-java
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <a href="https://discord.gg/avSH2JTfef"><img src="https://img.shields.io/badge/Discord-online-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Discord" />
</p>

[box3d](https://github.com/erincatto/box3d) for java, through the foreign and a thin wrapper on top makes it something you'd actually want to call. positions are double precision, so a world can get big without the far corners turning to jitter.

java 25 or newer.

## Quick start

```java
try (B3World world = B3World.create(new Vec3(0, -10, 0))) {
    B3Body ground = world.createBody(B3BodyType.STATIC, new Vec3(0, -1, 0));
    ground.addBox(new Vec3(50, 1, 50));

    B3Body ball = world.createBody(B3BodyType.DYNAMIC, new Vec3(0, 5, 0));
    ball.addSphere(0.5f);

    for (int i = 0; i < 120; i++) {
        world.step(1f / 60f, 4);
    }
}
```

yeah that's the whole shape of it. make a world, put bodies in it, step it.

one rule worth knowing up front: a world belongs to the thread that created it, keep your calls on that thread. hand `B3World.create(gravity, workers)` a worker count and the solver fans out across a pool internally, but the api around it stays single threaded.

## What is implemented

shapes are boxes, spheres, capsules, convex hulls, triangle meshes and height fields, plus static compounds that fuse a pile of primitives into one collider. friction, restitution, density, filters, sensors, all editable through `B3Shape` after the fact.

bodies do the usual. forces and impulses at a point, damping, gravity scale, bullet mode for small fast things, sleep tuning, motion locks, kinematic targets. anything you'd want to read back has a getter.

all nine joint types are here (distance, revolute, prismatic, spherical, weld, motor, wheel, parallel, filter) with the full set of motor, spring and limit knobs on each. wheels carry suspension and steering, which is most of what you need to make a car. a joint can break once it passes a force or torque threshold, and it turns up in `jointEvents()` when it does.

`B3Mover` is a kinematic capsule character controller, the collide/solve/cast loop lifted from the box3d character sample with step up, ground snap and depenetration already wired in.

for queries there's `castRayClosest`, `castSphereClosest`, `overlapAABB`, and per shape `rayCast` and `closestPoint`. after a `step` you can pull `contactEvents()`, `bodyEvents()`, `sensorEvents()` and `jointEvents()`, or register `onContactBegin` / `onContactHit` and let them come to you. `setContactFilter` hands java the last word on whether a pair collides.

introspection runs both directions. take a shape and ask it its type, geometry, filter, aabb, materials, who it's touching. walk `body.shapes()` and `body.joints()`, or go back the other way with `shape.body()`. reshape or re-material a shape while the sim runs. park a `long` of userData on a body, shape or joint and read it back later.

and `B3Recording` with `B3Replay` capture a world and play it back.

## What is not implemented

the wrapper covers every body, shape, joint and world method you actually reach for. a few are left out:

- the rotational inertia getters hand back a 3x3 matrix and there's no matrix type for it yet
- the single body casts (`Body.castRay` and friends) do the same job as the world level casts, which are already wrapped
- reading raw geometry back off a shape, pulling the hull points or mesh triangles out again

none of that is a wall. everything box3d exports still sits in the generated `com.meekdev.box3d.ffi` layer, the wrapper just doesn't dress it up. reach straight in when you need one of them.

a few of box3d's own rules leak through and are good to know. compounds and height fields only go on static bodies. a triangle mesh sits on any body type but carries no mass, so treat it as static or kinematic. for a dynamic concave model, `B3Hull.bake` gives you a convex hull, `B3Model.decompose` does a quick approximate split, and `B3Model.decomposeExact` runs real v-hacd (that one rides along as a second small native). `B3Model.loadObj` reads the triangles straight out of the file.

## natives

`./gradlew buildNative` builds the vendored box3d submodule with cmake for whatever host you're on. ci does the same across linux, windows and macos and packs all of them into the jar, then `NativeLoader` fishes out the right one at runtime. already have a build lying around? point `bkun.box3d.library` at it and it wins.

bumped the submodule, regenerate the bindings with `./gradlew regenerateBindings` and rebuild the native.
