package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITIONAL_ITERATIONS = 4;
    private static final int SUBSTEPS = 4;

    private List<RigidBody> bodies;
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

        float sub_dt = delta_time / SUBSTEPS;

        for (int s = 0; s < SUBSTEPS; s++) {

            for (RigidBody body : bodies) {
                if (body.get_inv_mass() == 0f) continue;
                if (body.sleeping) continue;
                body.physics_tick(sub_dt);
            }

            // editor_tool_sidebar contact list
            contacts.clear();

            for (int i = 0; i < body_count; i++) {
                RigidBody body_a = bodies.get(i);
                if (body_a.sleeping) continue;

                for (int j = i + 1; j < body_count; j++) {
                    RigidBody body_b = bodies.get(j);
                    if (body_b.sleeping) continue;

                    if (body_a.get_inv_mass() + body_b.get_inv_mass() == 0f) continue;

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

            for (int i = 0; i < VELOCITY_ITERATIONS; i++) {
                for (Contact c : contacts) {

                    CollisionManifold manifold = PhysicsManager.sat_overlap(
                        c.body_a.get_polygon(), c.body_b.get_polygon()
                    );
                    if (manifold.minimum_penetration_depth < 1e-4f) continue;

                    PhysicsManager.resolve_velocity(c.body_a, c.body_b, manifold);
                }
            }

            for (int i = 0; i < POSITIONAL_ITERATIONS; i++) {
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

    public int get_index_of_body(RigidBody body) {
        return bodies.indexOf(body);
    }

    public int get_body_count() {
        return bodies.size();
    }

    public void update_body(int index, Vector2 position, Vector2 velocity, float orientation, float angular_velocity) {
        RigidBody body = bodies.get(index);
        body.position.set(position);
        body.velocity.set(velocity);
        body.orientation = orientation;
        body.angular_velocity = angular_velocity;
    }

}
