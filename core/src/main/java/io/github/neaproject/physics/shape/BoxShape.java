package io.github.neaproject.physics.shape;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.BoundingBox;
import io.github.neaproject.physics.Polygon;

public class BoxShape extends Shape {
    private float width, height;

    public BoxShape(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Polygon get_polygon() {
        Vector2[] vertices = new Vector2[4];
        float hheight = height/2;
        float hwidth = width/2;

        vertices[0] = new Vector2(-hwidth, -hheight);
        vertices[1] = new Vector2(-hwidth, hheight);
        vertices[2] = new Vector2(hwidth, hheight);
        vertices[3] = new Vector2(hwidth, -hheight);

        return new Polygon(vertices);
    }

    @Override
    public BoundingBox get_bounding_box(Vector2 position) {
        return new BoundingBox(
            position.cpy().add(new Vector2(-width/2, -height/2)),
            position.cpy().add(new Vector2(width/2, height/2))
        );
    }

    public float get_width() {
        return width;
    }

    public void set_width(float width) {
        this.width = width;
    }

    public float get_height() {
        return height;
    }

    public void set_height(float height) {
        this.height = height;
    }
}
