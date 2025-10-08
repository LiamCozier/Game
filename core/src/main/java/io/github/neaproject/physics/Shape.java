package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

public abstract class Shape {

    public abstract Polygon get_polygon();
    public abstract BoundingBox get_bounding_box(Vector2 position);
}
