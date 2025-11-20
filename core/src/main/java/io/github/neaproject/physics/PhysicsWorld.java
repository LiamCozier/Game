package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    // How many times we iterate over all contacts per substep (sequential impulses)
    private static final int ITERATIONS = 10;

    // How many substeps per frame (stability for stacks / fast motion)
    private static final int SUBSTEPS = 8;

    private final List<RigidBody> bodies;
    private final List<Contact> contacts;

    // Internal helper to store a pair + its manifold
    private static class Contact {
        final RigidBody body_a;
        final RigidBody body_b;
        final CollisionManifold manifold;

        Contact(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {
            this.body_a = body_a;
            this.body_b = body_b;
            this.manifold = manifold;
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

            // ---- 2) BUILD CONTACT LIST ----
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
                    )) {
                        continue;
                    }

                    // SAT narrow-phase
                    CollisionManifold manifold = PhysicsManager.sat_overlap(body_a.get_polygon(), body_b.get_polygon());


                    if (manifold == null ||
                        manifold.collision_normal == null ||
                        manifold.contact_points == null ||
                        manifold.contact_points.length == 0 ||
                        manifold.minimum_penetration_depth < 1e-4f) {
                        continue;
                    }

                    contacts.add(new Contact(body_a, body_b, manifold));
                }
            }

            int contact_count = contacts.size();
            if (contact_count == 0) continue;

            // ---- 3) SOLVE CONTACTS (sequential impulses) ----
            for (int iter = 0; iter < ITERATIONS; iter++) {
                for (int k = 0; k < contact_count; k++) {
                    Contact c = contacts.get(k);
                    PhysicsManager.resolve_collision(c.body_a, c.body_b, c.manifold);
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
