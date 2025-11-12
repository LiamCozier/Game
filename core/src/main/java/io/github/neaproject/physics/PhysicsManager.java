package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsManager {

    private static boolean AABB_overlap(BoundingBox b1, BoundingBox b2) {

        // Apply previously mentioned conditions
        return  b1.min.x < b2.max.x &&
            b1.max.x > b2.min.x &&
            b1.min.y < b2.max.y &&
            b1.max.y > b2.min.y;
    }

    private static Vector2[] get_polygon_edges(Polygon p) {

        Vector2[] vertices = p.vertices();
        int edge_count = vertices.length;
        Vector2[] edges = new Vector2[edge_count];

        for (int i=0; i<edge_count; i++) {
            edges[i] = vertices[(i+1)%edge_count].cpy().sub(vertices[i]);
        }

        return edges;
    }

    public static Vector2[] get_polygon_normals(Polygon... polygons) {

        int edge_count = 0;
        for (Polygon p:polygons) {
            edge_count += p.vertices().length;
        }

        Vector2[] normals = new Vector2[edge_count];
        int i = 0;
        for (Polygon p:polygons) {
            Vector2[] edges = get_polygon_edges(p);

            for (int j = 0; j < edges.length; j++) {
                normals[i] = new Vector2(-edges[j].y, edges[j].x);
                normals[i].nor();
                i++;
            }
        }

        return normals;
    }

    public static float[] project_polygon_onto_axis(Polygon p, Vector2 axis) {
        Vector2[] vertices = p.vertices();
        float min_projection = Float.MAX_VALUE;
        float max_projection = -Float.MAX_VALUE;

        for (int i=0; i<vertices.length; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < min_projection) min_projection = projection;
            if (projection > max_projection) max_projection = projection;
        }

        return new float[]{min_projection, max_projection};
    }

    private static float get_range_overlap(float[] r1, float[] r2) {
        float overlap = Math.min(r1[1], r2[1]) - Math.max(r1[0], r2[0]);
        // return 0 if the overlap value is negative (no overlap)
        return Math.max(overlap, 0f);
    }

    private static Vector2 centroid(Polygon p) {
        Vector2[] vertices = p.vertices();
        Vector2 c = new Vector2();
        for (Vector2 vertex : vertices) c.add(vertex);
        return c.scl(1f / vertices.length);
    }

    public static CollisionManifold SAT_overlap(Polygon p1, Polygon p2) {
        float min_penetration = Float.MAX_VALUE;
        Vector2 collision_normal = null;

        Vector2[] axes = get_polygon_normals(p1, p2);
        for (Vector2 axis: axes) {
            float[] range1 = project_polygon_onto_axis(p1, axis);
            float[] range2 = project_polygon_onto_axis(p2, axis);

            float overlap = get_range_overlap(range1, range2);
            if (overlap < 1e-6f) {
                return new CollisionManifold(0, new Vector2(0, 0), new Vector2[0]);
            } else if (overlap < min_penetration) {
                min_penetration = overlap;
                collision_normal = axis.cpy();
            }
        }

        Vector2 c1 = centroid(p1);
        Vector2 c2 = centroid(p2);
        assert collision_normal != null;
        if (collision_normal.dot(c2.cpy().sub(c1)) < 0) {
            collision_normal.scl(-1);
        }

        Vector2[] contact_points = calc_contact_points(p1, p2, collision_normal, min_penetration);

        return new CollisionManifold(min_penetration, collision_normal, contact_points);
    }

    public static Vector2[] calc_contact_points(Polygon p1, Polygon p2, Vector2 collision_normal, float pen_depth) {
        Polygon reference, incident;
        ClippingFeatures cf_ref, cf_inc;
        boolean flip = false;

        ClippingFeatures cf1 = get_features(p1, collision_normal);
        ClippingFeatures cf2 = get_features(p2, collision_normal.cpy().scl(-1));

        float alignment2 = Math.abs(collision_normal.dot(cf2.edge_direction.cpy().nor()));
        float alignment1 = Math.abs(collision_normal.dot(cf1.edge_direction.cpy().nor()));

        if (alignment1 <= alignment2) {
            reference = p1;
            cf_ref = cf1;
            incident = p2;
            cf_inc = cf2;
            System.out.println("ref: p1 inc: p2");
        } else {
            reference = p2;
            cf_ref = cf2;
            incident = p1;
            cf_inc = cf1;
            flip = true;
            System.out.println("ref: p2 inc: p1");
        }

        Vector2 v1 = reference.vertices()[cf_ref.edge_start_index];
        Vector2 v2 = reference.vertices()[cf_ref.edge_end_index];
        Vector2 edge = v2.cpy().sub(v1);
        Vector2 ref_normal = new Vector2(edge.y, -edge.x).nor();

        Vector2 edge_dir = edge.cpy().nor();

        if (ref_normal.dot(collision_normal) <= 0) ref_normal.scl(-1);


        Vector2 left_normal  = edge_dir;
        Vector2 right_normal = edge_dir.cpy().scl(-1);

        float left_offset  = left_normal.dot(v1);
        float right_offset = right_normal.dot(v2);


        Vector2 inc_vertex1 = incident.vertices()[cf_inc.edge_start_index];
        Vector2 inc_vertex2 = incident.vertices()[cf_inc.edge_end_index];


        Vector2[] clipped = clip(inc_vertex1, inc_vertex2, left_normal, left_offset);
        if (clipped.length == 0) return new Vector2[0];

        Vector2 a = clipped[0];
        Vector2 b = clipped.length > 1 ? clipped[1] : clipped[0];
        clipped = clip(a, b, right_normal, right_offset);
        if (clipped.length == 0) return new Vector2[0];

        a = clipped[0];
        b = clipped.length > 1 ? clipped[1] : clipped[0];
        Vector2 ref_plane_normal = ref_normal.cpy().scl(-1);
        if (flip) ref_plane_normal.scl(-1);
        float ref_plane_offset = -ref_normal.dot(v1);
        clipped = clip(a, b, ref_plane_normal, ref_plane_offset);
//        if (clipped.length == 0) return new Vector2[0];


        List<Vector2> contacts = new ArrayList<>();
        float ref_offset = ref_normal.dot(v1);

        for (Vector2 p : clipped) {
            float separation = ref_normal.dot(p) - ref_offset;
            if (separation <= pen_depth + 0) {
                contacts.add(p.cpy());
            }
        }


        return contacts.toArray(new Vector2[0]);
    }

    public static Vector2[] clip(Vector2 v1, Vector2 v2, Vector2 plane_normal, float plane_offset) {
        final float EPS = 0;
        List<Vector2> contact_points = new ArrayList<>(2);

        float d1 = v1.dot(plane_normal) - plane_offset;
        float d2 = v2.dot(plane_normal) - plane_offset;

        if (d1 >= -EPS) contact_points.add(v1.cpy());
        if (d2 >= -EPS) contact_points.add(v2.cpy());

        if (d1 * d2 < 0) { // segment crosses the plane
            float t = d1 / (d1 - d2);
            contact_points.add(v1.cpy().mulAdd(v2.cpy().sub(v1), t));
        }

        return contact_points.toArray(new Vector2[0]);
    }

    public static ClippingFeatures get_features(Polygon p, Vector2 collision_normal) {
        ClippingFeatures cf = new ClippingFeatures();
        Vector2[] vertices = p.vertices();

        Vector2 support_vertex = null;
        float support_distance = -Float.MAX_VALUE;
        int support_index = -1;

        // calculate maximum distance vertex along the collision normal
        for (int i=0; i<vertices.length; i++) {
            float dot = vertices[i].dot(collision_normal);
            if (dot > support_distance) {
                support_vertex = vertices[i].cpy();
                support_distance = dot;
                support_index = i;
            }
        }

        // calculate the most perpendicular edge to the collision normal
        int edge_count = vertices.length;
        assert support_vertex != null;
        Vector2 prev_edge_direction = support_vertex.cpy().sub(vertices[(support_index-1+edge_count)%edge_count]);
        Vector2 next_edge_direction = vertices[(support_index+1)%edge_count].cpy().sub(support_vertex);

        float prev_dot = prev_edge_direction.dot(collision_normal);
        float next_dot = next_edge_direction.dot(collision_normal);

        Vector2 edge_direction;
        int edge_start_index, edge_end_index;
        if (Math.abs(prev_dot) < Math.abs(next_dot)) {
            edge_direction = prev_edge_direction.cpy().nor();
            edge_start_index = (support_index - 1 + edge_count) % edge_count;
            edge_end_index   = support_index;
        } else {
            edge_direction = next_edge_direction.cpy().nor();
            edge_start_index = support_index;
            edge_end_index   = (support_index + 1) % edge_count;
        }

        cf.edge_start_index = edge_start_index;
        cf.edge_end_index = edge_end_index;
        cf.support_vertex = support_vertex;
        cf.edge_direction = edge_direction;
        cf.support_index = support_index;
        return cf;

    }

}
