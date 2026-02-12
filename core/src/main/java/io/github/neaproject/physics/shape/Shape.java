package io.github.neaproject.physics.shape;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.BoundingBox;
import io.github.neaproject.physics.Polygon;

public abstract class Shape {

    public abstract Polygon get_polygon();
    public abstract BoundingBox get_bounding_box(Vector2 position);
    public abstract Shape cpy();
}
