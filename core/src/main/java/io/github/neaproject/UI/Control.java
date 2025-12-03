package io.github.neaproject.UI;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class Control {

    // (0, 0) is top right of screen
    // y is downwards

    protected Vector2 position;
    protected Control parent;
    protected List<Control> children;

    public Control(Vector2 position) {
        this.position = position;
        this.children = new ArrayList<>(0);
        parent = null;
    }

    public Control(Vector2 position, Control parent) {
        this(position);
        this.parent = parent;
    }

    public abstract void render(ShapeRenderer sr, OrthographicCamera camera);

    public Vector2 position() {
        if (this.parent == null) {
            return this.position.cpy();
        }
        Vector2 parent = this.parent.position.cpy();
        return new Vector2(parent.x + position.x, parent.y + position.y);
    }


}
