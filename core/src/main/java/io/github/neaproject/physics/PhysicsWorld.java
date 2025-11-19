package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsWorld {

    private static class ContactPair {
        public final RigidBody body_a;
        public final RigidBody body_b;
        public final CollisionManifold manifold;

        public ContactPair(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {
            this.body_a = body_a;
            this.body_b = body_b;
            this.manifold = manifold;
        }
    }

    private final List<RigidBody> rigid_bodies = new ArrayList<>();

    public Vector2 gravity = new Vector2(0f, -9.81f);
    public int velocity_iterations = 8;
    public int position_iterations = 3;

    public void add_body(RigidBody body) {
        rigid_bodies.add(body);
    }

    public void clear_bodies() {
        rigid_bodies.clear();
    }

    public List<RigidBody> get_bodies() {
        return rigid_bodies;
    }

    /**
     * Call this once per frame: physics_world.physics_tick(delta_time);
     * Do NOT also call RigidBody.tick/physics_tick externally, or you'll integrate twice.
     */
    public void physics_tick(float delta_time) {
        if (delta_time <= 0f) return;

        // 1. Apply gravity to velocities
        apply_gravity(delta_time);

        // 2. Build contact list (SAT narrow-phase)
        List<ContactPair> contacts = build_contact_list();

        // 3. Resolve velocities via impulses
        for (int i = 0; i < velocity_iterations; i++) {
            for (ContactPair contact : contacts) {
                resolve_collision(contact.body_a, contact.body_b, contact.manifold);
            }
        }

        // 4. Integrate positions and orientations
        integrate_bodies(delta_time);

        // 5. Positional correction for penetration
        for (int i = 0; i < position_iterations; i++) {
            for (ContactPair contact : contacts) {
                positional_correction(contact.body_a, contact.body_b, contact.manifold);
            }
        }
    }

    // -------------------------------------------------------------------------
    // STEP 1: GRAVITY
    // -------------------------------------------------------------------------

    private void apply_gravity(float delta_time) {
        for (RigidBody body : rigid_bodies) {
            if (body.inv_mass == 0f) {
                // static body
                continue;
            }

            // assumes Particle has "velocity" and gravity handled manually here
            body.velocity.mulAdd(gravity, delta_time);
        }
    }

    // -------------------------------------------------------------------------
    // STEP 2: COLLISION DETECTION (SAT)
    // -------------------------------------------------------------------------

    private List<ContactPair> build_contact_list() {
        List<ContactPair> contacts = new ArrayList<>();

        int count = rigid_bodies.size();
        for (int i = 0; i < count; i++) {
            RigidBody body_a = rigid_bodies.get(i);
            if (body_a.inv_mass == 0f && body_a.inv_inertia == 0f) continue;

            for (int j = i + 1; j < count; j++) {
                RigidBody body_b = rigid_bodies.get(j);

                // skip if both are static
                if (body_a.inv_mass == 0f && body_b.inv_mass == 0f) continue;

                Polygon polygon_a = body_a.get_polygon();
                Polygon polygon_b = body_b.get_polygon();

                CollisionManifold manifold = sat_overlap(polygon_a, polygon_b);
                if (manifold.minimum_penetration_depth > 0f &&
                    manifold.contact_points != null &&
                    manifold.contact_points.length > 0) {

                    contacts.add(new ContactPair(body_a, body_b, manifold));
                }
            }
        }

        return contacts;
    }

    private static Vector2[] get_polygon_edges(Polygon polygon) {
        Vector2[] vertices = polygon.vertices();
        int count = vertices.length;
        Vector2[] edges = new Vector2[count];

        for (int i = 0; i < count; i++) {
            Vector2 v0 = vertices[i];
            Vector2 v1 = vertices[(i + 1) % count];
            edges[i] = v1.cpy().sub(v0);
        }

        return edges;
    }

    // CCW outward normals: (-ey, ex)
    private static Vector2[] get_polygon_normals(Polygon... polygons) {
        int total_edges = 0;
        for (Polygon polygon : polygons) {
            total_edges += polygon.vertices().length;
        }

        Vector2[] normals = new Vector2[total_edges];
        int index = 0;

        for (Polygon polygon : polygons) {
            for (Vector2 edge : get_polygon_edges(polygon)) {
                Vector2 normal = new Vector2(-edge.y, edge.x);
                if (normal.len2() != 0f) normal.nor();
                normals[index++] = normal;
            }
        }

        return normals;
    }

    private static float[] project_polygon(Polygon polygon, Vector2 axis) {
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (Vector2 vertex : polygon.vertices()) {
            float dot = axis.dot(vertex);
            if (dot < min) min = dot;
            if (dot > max) max = dot;
        }

        return new float[]{min, max};
    }

    private static float get_overlap(float[] range_a, float[] range_b) {
        float overlap = Math.min(range_a[1], range_b[1]) - Math.max(range_a[0], range_b[0]);
        return Math.max(overlap, 0f);
    }

    private static Vector2 get_centroid(Polygon polygon) {
        Vector2 sum = new Vector2();
        Vector2[] vertices = polygon.vertices();
        for (Vector2 vertex : vertices) {
            sum.add(vertex);
        }
        if (vertices.length == 0) return new Vector2();
        return sum.scl(1f / vertices.length);
    }

    private static Vector2 get_support_point(Polygon polygon, Vector2 direction) {
        Vector2[] vertices = polygon.vertices();
        float max_projection = -Float.MAX_VALUE;
        Vector2 best_vertex = vertices[0];

        for (Vector2 vertex : vertices) {
            float projection = direction.dot(vertex);
            if (projection > max_projection) {
                max_projection = projection;
                best_vertex = vertex;
            }
        }
        return best_vertex.cpy();
    }

    private static Vector2[] calc_contact_points(Polygon polygon_a,
                                                 Polygon polygon_b,
                                                 Vector2 collision_normal,
                                                 float penetration_depth) {
        // Simple, robust: midpoint between the two support points
        Vector2 support_a = get_support_point(polygon_a, collision_normal);
        Vector2 support_b = get_support_point(polygon_b, collision_normal.cpy().scl(-1f));

        Vector2 contact = support_a.cpy().add(support_b).scl(0.5f);
        return new Vector2[]{contact};
    }

    public static CollisionManifold sat_overlap(Polygon polygon_a, Polygon polygon_b) {
        final float EPS = 1e-6f;

        float min_overlap = Float.MAX_VALUE;
        Vector2 best_axis = null;

        Vector2[] axes = get_polygon_normals(polygon_a, polygon_b);
        for (Vector2 axis : axes) {
            float[] range_a = project_polygon(polygon_a, axis);
            float[] range_b = project_polygon(polygon_b, axis);

            float overlap = get_overlap(range_a, range_b);
            if (overlap < EPS) {
                // Separating axis found: no collision
                return new CollisionManifold(0f, new Vector2(), new Vector2[0]);
            }

            if (overlap < min_overlap) {
                min_overlap = overlap;
                best_axis = axis.cpy();
            }
        }

        if (best_axis == null) {
            return new CollisionManifold(0f, new Vector2(), new Vector2[0]);
        }

        Vector2 centroid_a = get_centroid(polygon_a);
        Vector2 centroid_b = get_centroid(polygon_b);
        Vector2 direction = centroid_b.cpy().sub(centroid_a);

        if (best_axis.dot(direction) < 0f) {
            best_axis.scl(-1f);
        }

        Vector2[] contact_points = calc_contact_points(polygon_a, polygon_b, best_axis, min_overlap);

        return new CollisionManifold(min_overlap, best_axis, contact_points);
    }

    // -------------------------------------------------------------------------
    // STEP 3: COLLISION RESOLUTION (IMPULSES + FRICTION)
    // -------------------------------------------------------------------------

    private void resolve_collision(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {
        Vector2 normal = manifold.collision_normal;
        Vector2[] contact_points = manifold.contact_points;

        if (contact_points == null || contact_points.length == 0) return;

        float inv_mass_sum = body_a.inv_mass + body_b.inv_mass;
        if (inv_mass_sum == 0f) return;

        float restitution = Math.min(body_a.restitution, body_b.restitution);
        float static_friction = (float) Math.sqrt(
            body_a.static_friction * body_a.static_friction +
                body_b.static_friction * body_b.static_friction
        );
        float dynamic_friction = (float) Math.sqrt(
            body_a.dynamic_friction * body_a.dynamic_friction +
                body_b.dynamic_friction * body_b.dynamic_friction
        );

        int contact_count = contact_points.length;
        float contact_count_f = (contact_count > 0 ? (float) contact_count : 1f);

        for (Vector2 contact_point : contact_points) {
            Vector2 ra = contact_point.cpy().sub(body_a.position);
            Vector2 rb = contact_point.cpy().sub(body_b.position);

            // Velocity at contact: v + w x r
            Vector2 vel_a = body_a.velocity.cpy().add(
                new Vector2(-body_a.angular_velocity * ra.y, body_a.angular_velocity * ra.x)
            );
            Vector2 vel_b = body_b.velocity.cpy().add(
                new Vector2(-body_b.angular_velocity * rb.y, body_b.angular_velocity * rb.x)
            );

            Vector2 relative_velocity = vel_b.sub(vel_a);
            float vel_along_normal = relative_velocity.dot(normal);

            // If separating, no impulse
            if (vel_along_normal > 0f) continue;

            float ra_cross_n = ra.crs(normal);
            float rb_cross_n = rb.crs(normal);

            float inv_inertia_term_a = body_a.inv_inertia * ra_cross_n * ra_cross_n;
            float inv_inertia_term_b = body_b.inv_inertia * rb_cross_n * rb_cross_n;

            float denominator =
                inv_mass_sum + inv_inertia_term_a + inv_inertia_term_b;

            if (denominator == 0f) continue;

            // Normal impulse scalar
            float j = -(1f + restitution) * vel_along_normal;
            j /= denominator;
            j /= contact_count_f;

            Vector2 impulse = normal.cpy().scl(j);

            // Apply normal impulse
            if (body_a.inv_mass > 0f) {
                body_a.velocity.sub(impulse.cpy().scl(body_a.inv_mass));
                body_a.angular_velocity -= ra.crs(impulse) * body_a.inv_inertia;
            }
            if (body_b.inv_mass > 0f) {
                body_b.velocity.add(impulse.cpy().scl(body_b.inv_mass));
                body_b.angular_velocity += rb.crs(impulse) * body_b.inv_inertia;
            }

            // Recompute relative velocity after normal impulse
            vel_a = body_a.velocity.cpy().add(
                new Vector2(-body_a.angular_velocity * ra.y, body_a.angular_velocity * ra.x)
            );
            vel_b = body_b.velocity.cpy().add(
                new Vector2(-body_b.angular_velocity * rb.y, body_b.angular_velocity * rb.x)
            );
            relative_velocity = vel_b.sub(vel_a);

            // Tangent direction
            float rv_dot_n = relative_velocity.dot(normal);
            Vector2 tangent = relative_velocity.cpy().sub(normal.cpy().scl(rv_dot_n));

            if (tangent.len2() < 1e-8f) continue;
            tangent.nor();

            // Tangential impulse scalar
            float jt = -relative_velocity.dot(tangent);
            jt /= denominator;
            jt /= contact_count_f;

            Vector2 friction_impulse;

            if (Math.abs(jt) < j * static_friction) {
                // Static friction
                friction_impulse = tangent.cpy().scl(jt);
            } else {
                // Dynamic friction
                float sign = Math.signum(jt);
                friction_impulse = tangent.cpy().scl(-j * dynamic_friction * sign);
            }

            // Apply friction impulse
            if (body_a.inv_mass > 0f) {
                body_a.velocity.sub(friction_impulse.cpy().scl(body_a.inv_mass));
                body_a.angular_velocity -= ra.crs(friction_impulse) * body_a.inv_inertia;
            }
            if (body_b.inv_mass > 0f) {
                body_b.velocity.add(friction_impulse.cpy().scl(body_b.inv_mass));
                body_b.angular_velocity += rb.crs(friction_impulse) * body_b.inv_inertia;
            }
        }
    }

    // -------------------------------------------------------------------------
    // STEP 4: INTEGRATION
    // -------------------------------------------------------------------------

    private void integrate_bodies(float delta_time) {
        for (RigidBody body : rigid_bodies) {
            if (body.inv_mass == 0f) continue;

            body.position.mulAdd(body.velocity, delta_time);
            body.orientation += body.angular_velocity * delta_time;
        }
    }

    // -------------------------------------------------------------------------
    // STEP 5: POSITIONAL CORRECTION
    // -------------------------------------------------------------------------

    private void positional_correction(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {
        final float percent = 0.2f; // 20% of penetration
        final float slop = 0.01f;   // allowed penetration

        float penetration = manifold.minimum_penetration_depth;
        if (penetration <= slop) return;

        float inv_mass_sum = body_a.inv_mass + body_b.inv_mass;
        if (inv_mass_sum == 0f) return;

        float correction_magnitude = Math.max(penetration - slop, 0f) / inv_mass_sum * percent;
        Vector2 correction = manifold.collision_normal.cpy().scl(correction_magnitude);

        if (body_a.inv_mass > 0f) {
            body_a.position.sub(correction.cpy().scl(body_a.inv_mass));
        }
        if (body_b.inv_mass > 0f) {
            body_b.position.add(correction.cpy().scl(body_b.inv_mass));
        }
    }
}
