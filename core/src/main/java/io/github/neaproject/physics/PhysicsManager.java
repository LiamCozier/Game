package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class PhysicsManager {


    public static boolean aabb_overlap(BoundingBox b1, BoundingBox b2) {
        return b1.min.x < b2.max.x &&
            b1.max.x > b2.min.x &&
            b1.min.y < b2.max.y &&
            b1.max.y > b2.min.y;
    }

    private static Vector2[] get_polygon_edges(Polygon p) {
        Vector2[] verts = p.vertices();
        int count = verts.length;
        Vector2[] edges = new Vector2[count];

        for (int i = 0; i < count; i++) {
            edges[i] = verts[(i + 1) % count].cpy().sub(verts[i]);
        }
        return edges;
    }

    private static Vector2[] get_polygon_normals(Polygon... polys) {
        int total_edges = 0;
        for (Polygon p : polys) total_edges += p.vertices().length;

        Vector2[] normals = new Vector2[total_edges];
        int idx = 0;

        for (Polygon p : polys) {
            for (Vector2 e : get_polygon_edges(p)) {
                // CCW outward normal = (-ey, ex)
                normals[idx++] = new Vector2(-e.y, e.x).nor();
            }
        }
        return normals;
    }

    private static float[] project_polygon(Polygon p, Vector2 axis) {
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (Vector2 v : p.vertices()) {
            float d = axis.dot(v);
            if (d < min) min = d;
            if (d > max) max = d;
        }
        return new float[]{min, max};
    }

    private static float get_overlap(float[] r1, float[] r2) {
        float d = Math.min(r1[1], r2[1]) - Math.max(r1[0], r2[0]);
        return Math.max(d, 0f);
    }

    private static Vector2 get_centroid(Polygon p) {
        Vector2 sum = new Vector2();
        for (Vector2 v : p.vertices()) sum.add(v);
        return sum.scl(1f / p.vertices().length);
    }

    public static CollisionManifold sat_overlap(Polygon p1, Polygon p2) {
        final float EPS = 1e-6f;

        float min_overlap = Float.MAX_VALUE;
        Vector2 best_axis = null;

        Vector2[] axes = get_polygon_normals(p1, p2);
        for (Vector2 axis : axes) {
            float[] r1 = project_polygon(p1, axis);
            float[] r2 = project_polygon(p2, axis);

            float overlap = get_overlap(r1, r2);
            if (overlap < EPS)
                return new CollisionManifold(0, new Vector2(), new Vector2[0]);

            if (overlap < min_overlap) {
                min_overlap = overlap;
                best_axis = axis.cpy();
            }
        }

        Vector2 normal = best_axis;

        Vector2 c1 = get_centroid(p1);
        Vector2 c2 = get_centroid(p2);
        Vector2 dir = c2.sub(c1);
        if (best_axis.dot(dir) < 0)
            best_axis.scl(-1f);

        Vector2[] contact_points = calc_contact_points(p1, p2, best_axis, min_overlap);

        if (Math.abs(normal.x) < 1e-4f) normal.x = 0f;
        if (Math.abs(normal.y) < 1e-4f) normal.y = 0f;
        normal.nor();


        return new CollisionManifold(min_overlap, best_axis, contact_points);
    }

    private static Vector2[] calc_contact_points(Polygon p1, Polygon p2, Vector2 normal, float pen_depth) {
        final float EPS_CLIP = 1e-4f;

        ClippingFeatures cf1 = get_features(p1, normal);
        ClippingFeatures cf2 = get_features(p2, normal.cpy().scl(-1f));

        float align1 = cf1.edge_normal.dot(normal);
        float align2 = cf2.edge_normal.dot(normal);

        Polygon ref_poly, inc_poly;
        ClippingFeatures cf_ref;

        if (align1 >= align2) {
            ref_poly = p1;
            cf_ref = cf1;
            inc_poly = p2;
        } else {
            ref_poly = p2;
            cf_ref = cf2;
            inc_poly = p1;
        }

        Vector2[] ref_verts = ref_poly.vertices();
        Vector2 v1 = ref_verts[cf_ref.edge_start_index];
        Vector2 v2 = ref_verts[cf_ref.edge_end_index];

        Vector2 edge = v2.cpy().sub(v1);
        Vector2 ref_normal = cf_ref.edge_normal.cpy();
        if (ref_normal.dot(normal) < 0) ref_normal.scl(-1f);

        Vector2 edge_dir = edge.cpy().nor();

        Vector2 left_normal = edge_dir;
        Vector2 right_normal = edge_dir.cpy().scl(-1f);

        float left_offset = left_normal.dot(v1);
        float right_offset = right_normal.dot(v2);

        Vector2[] inc_verts = inc_poly.vertices();
        float best = Float.MAX_VALUE;
        int inc_start_index = 0, inc_end_index = 1;

        for (int i = 0; i < inc_verts.length; i++) {
            int j = (i + 1) % inc_verts.length;
            Vector2 e = inc_verts[j].cpy().sub(inc_verts[i]).nor();
            Vector2 face_normal = new Vector2(-e.y, e.x).nor(); // CCW outward
            float d = face_normal.dot(ref_normal);              // want most anti-parallel
            if (d < best) {
                best = d;
                inc_start_index = i;
                inc_end_index = j;
            }
        }

        Vector2 inc_v1 = inc_verts[inc_start_index];
        Vector2 inc_v2 = inc_verts[inc_end_index];

        Vector2[] clipped = clip_segment(inc_v1, inc_v2, left_normal, left_offset);
        if (clipped.length == 0)
            return fallback_contact(v1, ref_normal, inc_v1, inc_v2);

        Vector2 a = clipped[0];
        Vector2 b = (clipped.length > 1 ? clipped[1] : clipped[0]);

        clipped = clip_segment(a, b, right_normal, right_offset);
        if (clipped.length == 0)
            return fallback_contact(v1, ref_normal, inc_v1, inc_v2);

        a = clipped[0];
        b = (clipped.length > 1 ? clipped[1] : clipped[0]);

        Vector2 final_plane_normal = ref_normal.cpy().scl(-1f);
        float final_plane_offset = -ref_normal.dot(v1) - EPS_CLIP;
        clipped = clip_segment(a, b, final_plane_normal, final_plane_offset);

        if (clipped.length == 0)
            return fallback_contact(v1, ref_normal, inc_v1, inc_v2);

        List<Vector2> contacts = new ArrayList<>();
        float ref_offset = ref_normal.dot(v1);
        float allowed = pen_depth + 2f * EPS_CLIP;

        for (Vector2 p : clipped) {
            float separation = ref_normal.dot(p) - ref_offset;
            if (separation <= allowed) contacts.add(p.cpy());
        }

        if (contacts.isEmpty())
            return fallback_contact(v1, ref_normal, inc_v1, inc_v2);

        return contacts.toArray(new Vector2[0]);
    }

    private static ClippingFeatures get_features(Polygon polygon, Vector2 collision_normal) {
        Vector2[] vertices = polygon.vertices();
        int vertex_count = vertices.length;

        // find support vertex
        int support_index = 0;
        float max_projection = -Float.MAX_VALUE;

        for (int i = 0; i < vertex_count; i++) {
            float projection = collision_normal.dot(vertices[i]);
            if (projection > max_projection) {
                max_projection = projection;
                support_index = i;
            }
        }

        Vector2 support_vertex = vertices[support_index];

        int prev_index = (support_index - 1 + vertex_count) % vertex_count;
        int next_index = (support_index + 1) % vertex_count;

        Vector2 prev_edge = support_vertex.cpy().sub(vertices[prev_index]);
        Vector2 next_edge = vertices[next_index].cpy().sub(support_vertex);

        Vector2 prev_normal = new Vector2(-prev_edge.y, prev_edge.x);
        Vector2 next_normal = new Vector2(-next_edge.y, next_edge.x);

        if (prev_normal.len2() != 0f) prev_normal.nor();
        if (next_normal.len2() != 0f) next_normal.nor();

        float prev_dot = prev_normal.dot(collision_normal);
        float next_dot = next_normal.dot(collision_normal);

        int edge_start_index;
        int edge_end_index;
        Vector2 edge_direction;
        Vector2 edge_normal;

        // choose the edge whose normal is more aligned with collision_normal
        if (prev_dot > next_dot) {
            edge_start_index = prev_index;
            edge_end_index = support_index;
            edge_direction = support_vertex.cpy().sub(vertices[prev_index]); // prev -> support
            edge_normal = prev_normal;
        } else {
            edge_start_index = support_index;
            edge_end_index = next_index;
            edge_direction = vertices[next_index].cpy().sub(support_vertex); // support -> next
            edge_normal = next_normal;
        }

        if (edge_direction.len2() != 0f) edge_direction.nor();
        if (edge_normal.len2() != 0f) edge_normal.nor();

        ClippingFeatures features = new ClippingFeatures();
        features.support_vertex = support_vertex.cpy();
        features.support_index = support_index;
        features.edge_start_index = edge_start_index;
        features.edge_end_index = edge_end_index;
        features.edge_direction = edge_direction;
        features.edge_normal = edge_normal;

        return features;
    }

    private static Vector2[] clip_segment(Vector2 a, Vector2 b, Vector2 plane_normal, float plane_offset) {
        final float EPS = 1e-4f;
        List<Vector2> out = new ArrayList<>(2);

        float da = a.dot(plane_normal) - plane_offset;
        float db = b.dot(plane_normal) - plane_offset;

        if (da >= -EPS) out.add(a.cpy());
        if (db >= -EPS) out.add(b.cpy());

        if (da * db < 0) {
            float t = da / (da - db);
            out.add(a.cpy().mulAdd(b.cpy().sub(a), t));
        }

        return out.toArray(new Vector2[0]);
    }

    private static Vector2[] fallback_contact(Vector2 face_point, Vector2 face_normal, Vector2 inc_a, Vector2 inc_b) {
        float ref_dot = face_normal.dot(face_point);
        float da = face_normal.dot(inc_a) - ref_dot;
        float db = face_normal.dot(inc_b) - ref_dot;

        Vector2 sp = (da > db ? inc_a : inc_b).cpy();
        float lambda = face_normal.dot(sp) - ref_dot;
        sp.sub(face_normal.cpy().scl(lambda));
        return new Vector2[]{sp};
    }

    public static void resolve_velocity(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {

        // if both bodies are static, do nothing
        if (body_a.get_inv_mass() + body_b.get_inv_mass() == 0f) {
            return;
        }

        if (manifold == null ||
            manifold.collision_normal == null ||
            manifold.contact_points == null ||
            manifold.contact_points.length == 0) {
            return;
        }


        Vector2 normal = manifold.collision_normal.cpy();
        if (normal.len2() == 0f) return;
        normal.nor();

        int contact_count = manifold.contact_points.length;

        // combine material properties
        float restitution = Math.max(body_a.restitution, body_b.restitution);

        float static_friction = (float) Math.sqrt(
            body_a.static_friction * body_a.static_friction +
                body_b.static_friction * body_b.static_friction
        );
        float dynamic_friction = (float) Math.sqrt(
            body_a.dynamic_friction * body_a.dynamic_friction +
                body_b.dynamic_friction * body_b.dynamic_friction
        );


        // make sure impulse arrays exist and match contact count
        if (manifold.normal_impulses == null || manifold.normal_impulses.length != contact_count) {
            manifold.normal_impulses = new float[contact_count];
            manifold.tangent_impulses = new float[contact_count];
        }


        for (int i = 0; i < contact_count; i++) {
            Vector2 contact_point = manifold.contact_points[i];

            // r from centres of mass to contact point
            Vector2 ra = contact_point.cpy().sub(body_a.position);
            Vector2 rb = contact_point.cpy().sub(body_b.position);

            // linear + angular velocity at contact points
            Vector2 velocity_a_at_contact = body_a.velocity.cpy().add(
                -body_a.angular_velocity * ra.y,
                body_a.angular_velocity * ra.x
            );

            Vector2 velocity_b_at_contact = body_b.velocity.cpy().add(
                -body_b.angular_velocity * rb.y,
                body_b.angular_velocity * rb.x
            );


            Vector2 relative_velocity = velocity_b_at_contact.cpy().sub(velocity_a_at_contact);


            // relative velocity along normal
            float contact_velocity_normal = relative_velocity.dot(normal);
            // if separating along normal, no impulse
            if (contact_velocity_normal > 0f) {
                manifold.normal_impulses[i] = 0f;
                manifold.tangent_impulses[i] = 0f;
                continue;
            }

            float ra_cross_n = ra.x * normal.y - ra.y * normal.x;
            float rb_cross_n = rb.x * normal.y - rb.y * normal.x;

            float inv_mass_sum =
                body_a.get_inv_mass() + body_b.get_inv_mass() +
                    (ra_cross_n * ra_cross_n) * body_a.inv_inertia +
                    (rb_cross_n * rb_cross_n) * body_b.inv_inertia;

            if (inv_mass_sum == 0f) {
                manifold.normal_impulses[i] = 0f;
                manifold.tangent_impulses[i] = 0f;
                continue;
            }

            // normal impulse
            float normal_impulse_scalar = -(1f + restitution) * contact_velocity_normal;
            normal_impulse_scalar /= inv_mass_sum;

            Vector2 normal_impulse = normal.cpy().scl(normal_impulse_scalar);

            // apply normal impulse to linear velocity  (pattern A: -J/m, B: +J/m)
            body_a.velocity.sub(normal_impulse.cpy().scl(body_a.get_inv_mass()));
            body_b.velocity.add(normal_impulse.cpy().scl(body_b.get_inv_mass()));

            // apply normal impulse to angular velocity (torque = r x J)
            float ra_cross_jn = ra.x * normal_impulse.y - ra.y * normal_impulse.x;
            float rb_cross_jn = rb.x * normal_impulse.y - rb.y * normal_impulse.x;

            body_a.angular_velocity -= ra_cross_jn * body_a.inv_inertia;
            body_b.angular_velocity += rb_cross_jn * body_b.inv_inertia;

            manifold.normal_impulses[i] = normal_impulse_scalar;

            // friction impulse

            // recompute velocities at contact after normal impulse
            velocity_a_at_contact = body_a.velocity.cpy().add(
                -body_a.angular_velocity * ra.y,
                body_a.angular_velocity * ra.x
            );

            velocity_b_at_contact = body_b.velocity.cpy().add(
                -body_b.angular_velocity * rb.y,
                body_b.angular_velocity * rb.x
            );

            relative_velocity = velocity_b_at_contact.cpy().sub(velocity_a_at_contact);

            // editor_tool_sidebar tangent = v_tangent direction
            Vector2 tangent = relative_velocity.cpy().sub(
                normal.cpy().scl(relative_velocity.dot(normal))
            );
            if (tangent.len2() > 0f) {
                tangent.nor();
            } else {
                manifold.tangent_impulses[i] = 0f;
                continue;
            }

            float contact_velocity_tangent = relative_velocity.dot(tangent);

            float ra_cross_t = ra.x * tangent.y - ra.y * tangent.x;
            float rb_cross_t = rb.x * tangent.y - rb.y * tangent.x;

            float inv_mass_sum_tangent =
                body_a.get_inv_mass() + body_b.get_inv_mass() +
                    (ra_cross_t * ra_cross_t) * body_a.inv_inertia +
                    (rb_cross_t * rb_cross_t) * body_b.inv_inertia;

            if (inv_mass_sum_tangent == 0f) {
                manifold.tangent_impulses[i] = 0f;
                continue;
            }

            // base friction impulse magnitude
            float tangent_impulse_scalar = -contact_velocity_tangent;
            tangent_impulse_scalar /= inv_mass_sum_tangent;

            // correct static-friction clamp: use |Jn|
            float max_static_friction_impulse = Math.abs(normal_impulse_scalar) * static_friction;

            // Coulomb friction: clamp or fall back to dynamic
            if (Math.abs(tangent_impulse_scalar) > max_static_friction_impulse) {
                tangent_impulse_scalar =
                    -Math.signum(contact_velocity_tangent) *
                        Math.abs(normal_impulse_scalar) * dynamic_friction;
            }

            Vector2 friction_impulse = tangent.cpy().scl(tangent_impulse_scalar);

            // apply friction impulse to linear velocity
            body_a.velocity.sub(friction_impulse.cpy().scl(body_a.get_inv_mass()));
            body_b.velocity.add(friction_impulse.cpy().scl(body_b.get_inv_mass()));

            // apply friction impulse to angular velocity
            float ra_cross_jt = ra.x * friction_impulse.y - ra.y * friction_impulse.x;
            float rb_cross_jt = rb.x * friction_impulse.y - rb.y * friction_impulse.x;

            body_a.angular_velocity -= ra_cross_jt * body_a.inv_inertia;
            body_b.angular_velocity += rb_cross_jt * body_b.inv_inertia;

            manifold.tangent_impulses[i] = tangent_impulse_scalar;
        }

    }

    public static void resolve_position(RigidBody body_a, RigidBody body_b, CollisionManifold manifold) {

        final float slop = 0.01f;
        final float percent = 0.25f;
        final float max_correction = 0.5f;

        Vector2 normal = manifold.collision_normal.cpy();
        if (normal.len2() == 0f) return;
        normal.nor();

        float penetration = manifold.minimum_penetration_depth;
        float correction_mag = Math.max(penetration - slop, 0f);
        correction_mag = Math.min(correction_mag, max_correction);

        correction_mag *= percent;

        float inv_mass_sum = body_a.get_inv_mass() + body_b.get_inv_mass();
        if (inv_mass_sum > 0f) {
            Vector2 correction = normal.cpy().scl(correction_mag / inv_mass_sum);

            body_a.position.sub(correction.cpy().scl(body_a.get_inv_mass()));
            body_b.position.add(correction.cpy().scl(body_b.get_inv_mass()));
        }
    }

}
