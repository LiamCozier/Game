package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

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

    public static boolean SAT_overlap(Polygon p1, Polygon p2) {
        float min_penetration = Float.MAX_VALUE;
        Vector2 collision_normal = null;

        Vector2[] axes = get_polygon_normals(p1, p2);
        for (Vector2 axis: axes) {
            float[] range1 = project_polygon_onto_axis(p1, axis);
            float[] range2 = project_polygon_onto_axis(p2, axis);

            float overlap = get_range_overlap(range1, range2);
            if (overlap < 1e-6f) {
                return false;
            } else if (overlap < min_penetration) {
                min_penetration = overlap;
                collision_normal = axis.cpy();
            }
        }

        return true;
    }

}
