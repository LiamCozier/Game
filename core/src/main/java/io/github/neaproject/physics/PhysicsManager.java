package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

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

        System.out.print("E=");
        for (int i=0; i<edge_count; i++) {
            edges[i] = vertices[(i+1)%edge_count].cpy().sub(vertices[i]);
            System.out.print(edges[i].toString());
            if (i+1!=edge_count) System.out.print(",");
        }
        System.out.println();

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

    public static boolean ranges_overlap(float[] r1, float[] r2) {
        return r1[1] >= r2[0] && r2[1] >= r1[0];
    }

    public static boolean SAT_overlap(Polygon p1, Polygon p2) {
        Vector2[] axis = get_polygon_normals(p1, p2);
        return true;
    }

}
