package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

public class BoundingBox {
    public Vector2 min;
    public Vector2 max;

    public BoundingBox(Vector2 min, Vector2 max) {
        this.min = min;
        this.max = max;
    }

    public float get_width() {
        return max.x - min.x;
    }

    public float get_height() {
        return max.y - min.y;
    }


}
