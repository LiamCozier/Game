package io.github.neaproject.UI;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class Control {

    // (0, 0) is bottom right of screen
    // y is upwards

    protected Vector2 viewport_position;
    protected Control parent;
    protected List<Control> children;

    public Control(Vector2 viewport_position) {
        this.viewport_position = viewport_position;
        this.children = new ArrayList<>(0);
        parent = null;
    }

    public Control(Vector2 viewport_position, Control parent) {
        this(viewport_position);
        this.parent = parent;
    }

    public abstract void render(ShapeRenderer sr, OrthographicCamera camera);

    public Vector2 position() {
        if (this.parent == null) {
            return this.viewport_position.cpy();
        }
        Vector2 parent = this.parent.viewport_position.cpy();
        return new Vector2(parent.x + viewport_position.x, parent.y + viewport_position.y);
    }

    public void translate(Vector2 v) {
        this.viewport_position.add(v);
    }

    protected Vector2 screen_position(OrthographicCamera camera) {
        Vector2 offset = new Vector2(camera.viewportWidth, camera.viewportHeight).scl(-0.5f * camera.zoom);
        offset.add(camera.position.x, camera.position.y);
        return position().scl(camera.zoom).add(offset);
    }


}
