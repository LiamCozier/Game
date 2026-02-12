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
        Vector2[] verts = this.polygon.vertices();

        float min_x = Float.MAX_VALUE, min_y = Float.MAX_VALUE;
        float max_x = -Float.MAX_VALUE, max_y = -Float.MAX_VALUE;

        for (Vector2 v : verts) {
            if (v.x < min_x) min_x = v.x;
            if (v.y < min_y) min_y = v.y;
            if (v.x > max_x) max_x = v.x;
            if (v.y > max_y) max_y = v.y;
        }

        return new BoundingBox(new Vector2(min_x, min_y), new Vector2(max_x, max_y));
    }

    @Override
    public Shape cpy() {
        return new PolygonShape(this.polygon.vertices());
    }
}
