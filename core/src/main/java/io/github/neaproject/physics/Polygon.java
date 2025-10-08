package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

// CLOCKWISE WINDING
public class Polygon {
    private Vector2[] vertices;

    public Polygon(Vector2[] vertices) {
        this.vertices = vertices;
    }

    public Vector2[] vertices() {
        return vertices;
    }
    public Vector2 get_vertex(int index) {
        return vertices[index];
    }

    public void set_vertex(int index, Vector2 vertex) {
        vertices[index] = vertex.cpy();
    }

    public int size() {
        return vertices.length;
    }

    public float[] get_float_array() {
        int size = size();
        float[] array = new float[2 * size];

        for (int i=0; i<size; i++) {
            array[2 * i] = vertices[i].x;
            array[2 * i + 1] = vertices[i].y;
        }

        return array;
    }
}
