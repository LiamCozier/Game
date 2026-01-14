package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private static final int VELOCITY_ITERATIONS = 16;
    private static final int POSITIONAL_ITERATIONS = 4;
    private static final int SUBSTEPS = 16;

    private final List<RigidBody> bodies;
    private final List<Contact> contacts;

    private static class Contact {
        final RigidBody body_a;
        final RigidBody body_b;

        Contact(RigidBody body_a, RigidBody body_b) {
            this.body_a = body_a;
            this.body_b = body_b;
        }
    }

    public PhysicsWorld() {
        bodies = new ArrayList<>();
        contacts = new ArrayList<>();
    }

    public void physics_tick(float delta_time) {
        if (delta_time <= 0f) return;

        int body_count = bodies.size();
        if (body_count == 0) return;

        float sub_dt = delta_time / (float) SUBSTEPS;

        for (int s = 0; s < SUBSTEPS; s++) {

            // ---- 1) INTEGRATE (always integrate all dynamic bodies) ----
            for (RigidBody body : bodies) {
                if (body.inv_mass == 0f) continue; // static → no integration
                body.physics_tick(sub_dt);
            }

            // build contact list
            contacts.clear();

            for (int i = 0; i < body_count; i++) {
                RigidBody body_a = bodies.get(i);

                for (int j = i + 1; j < body_count; j++) {
                    RigidBody body_b = bodies.get(j);

                    // both static → ignore
                    if (body_a.inv_mass + body_b.inv_mass == 0f) continue;

                    // AABB early-out
                    if (!PhysicsManager.aabb_overlap(
                        body_a.get_bounding_box(),
                        body_b.get_bounding_box()
                    )) continue;

                    CollisionManifold manifold = PhysicsManager.sat_overlap(
                        body_a.get_polygon(), body_b.get_polygon()
                    );
                    if (manifold.minimum_penetration_depth < 1e-4f) continue;

                    contacts.add(new Contact(body_a, body_b));
                }
            }

            int contact_count = contacts.size();
            if (contact_count == 0) continue;

            for (int iter = 0; iter < VELOCITY_ITERATIONS; iter++) {
                for (Contact c : contacts) {

                    CollisionManifold manifold = PhysicsManager.sat_overlap(
                        c.body_a.get_polygon(), c.body_b.get_polygon()
                    );
                    if (manifold.minimum_penetration_depth < 1e-4f) continue;

                    PhysicsManager.resolve_velocity(c.body_a, c.body_b, manifold);
                }
            }

            for (int iter = 0; iter < POSITIONAL_ITERATIONS; iter++) {
                for (Contact c: contacts) {

                    CollisionManifold manifold = PhysicsManager.sat_overlap(
                        c.body_a.get_polygon(), c.body_b.get_polygon()
                    );
                    if (manifold.minimum_penetration_depth < 1e-4f) continue;

                    PhysicsManager.resolve_position(c.body_a, c.body_b, manifold);
                }
            }
        }
    }

    public void add_body(RigidBody body) {
        if (body == null) return;
        bodies.add(body);
    }

    public void remove_body(RigidBody body) {
        bodies.remove(body);
    }

    public RigidBody[] get_bodies() {
        return bodies.toArray(new RigidBody[0]);
    }

    public RigidBody get_body(int index) {
        return bodies.get(index);
    }

    public int get_body_count() {
        return bodies.size();
    }
}
