package io.github.neaproject.physics.shape;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.BoundingBox;
import io.github.neaproject.physics.Polygon;

public class PolygonShape extends Shape{
    private Polygon polygon;

    public PolygonShape(Vector2[] vertices) {
        this.polygon = new Polygon(vertices);
    }

    @Override
    public Polygon get_polygon() {
        return this.polygon;
    }

    @Override
    public BoundingBox get_bounding_box(Vector2 position) {
        return new BoundingBox(
            position.cpy(),
            position.cpy()
        );
    }
}
