package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class Control {

    public static final Color DARK_GREY = new Color(0.1f, 0.1f, 0.1f, 1);

    // (0, 0) is top right of screen
    // y is downwards

    protected Vector2 viewport_position;
    protected Control parent;
    protected List<Control> children;
    protected float width, height;
    protected boolean show;

    public Control(Vector2 viewport_position, float width, float height) {
        this.viewport_position = viewport_position;
        this.width = width;
        this.height = height;
        this.children = new ArrayList<>(0);
        parent = null;
        show = true;
    }

    public Control(Vector2 viewport_position, float width, float height, Control parent) {
        this(viewport_position, width, height);
        this.parent = parent;
        parent.add_child(this);
    }

    public abstract void shape_render(ShapeRenderer sr);
    public abstract void batch_render(SpriteBatch batch);

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

    public void add_child(Control child) {
        this.children.add(child);
    }

    public List<Control> get_children() {
        return this.children;
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

    public boolean is_invisible() {
        return !show;
    }

    public void show() {
        show = true;
    }

    public void hide() {
        show = false;
    }
}
