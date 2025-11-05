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

        System.out.print("Edges: ");
        for (int i=0; i<edge_count; i++) {
            edges[i] = vertices[(i+1)%edge_count].cpy().sub(vertices[i]);
            System.out.print(edges[i].toString());
        }
        System.out.println();

        return edges;
    }

    public static Vector2[] get_polygon_normals(Polygon p) {

        Vector2[] edges = get_polygon_edges(p);
        int edge_count = edges.length;
        Vector2[] normals = new Vector2[edge_count];

        System.out.print("Normals: ");
        for (int i=0; i<edge_count; i++) {
            normals[i] = new Vector2(-edges[i].y, edges[i].x);
            normals[i].nor();
            System.out.print(normals[i].toString() + ",");
        }
        System.out.println();

        return normals;
    }

}
