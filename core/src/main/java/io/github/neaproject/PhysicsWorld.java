package io.github.neaproject;

import io.github.neaproject.physics.CollisionManifold;
import io.github.neaproject.physics.PhysicsManager;
import io.github.neaproject.physics.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {
    private static final int ITERATIONS = 25;

    private List<RigidBody> bodies;

    public PhysicsWorld() {
        bodies = new ArrayList<>(0);
    }

    public void physics_tick(float delta_time) {

        for (int k=0; k<8; k++) {
            for (RigidBody body : bodies) {
                body.physics_tick(delta_time/8);
            }

            for (int i = 0; i < bodies.size(); i++) {
                for (int j = 0; j < bodies.size(); j++) {
                    if (i == j) continue;

                    RigidBody body1 = bodies.get(i);
                    RigidBody body2 = bodies.get(j);

                    // ignore if bounding boxes do not intersect
//                    if (!PhysicsManager.aabb_overlap(body1.get_bounding_box(), body2.get_bounding_box())) continue;
//                    if (body1.mass == 0 && body2.mass == 0) continue;

                    // calculate and resolve collision
                    for (int n = 0; n < ITERATIONS; n++) {
                        CollisionManifold cm = PhysicsManager.sat_overlap(body1.get_polygon(), body2.get_polygon());
                        // ignore if no penetration
                        if (cm.minimum_penetration_depth < 1e-4f) continue;
                        PhysicsManager.resolve_collision(body1, body2, cm);
                    }
                }
            }
        }
    }

    public void add_body(RigidBody body) {
        bodies.add(body);
    }

    public RigidBody[] get_bodies() {
        return bodies.toArray(new RigidBody[0]);
    }
}
